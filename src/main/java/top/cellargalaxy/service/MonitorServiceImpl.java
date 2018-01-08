package top.cellargalaxy.service;


import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import top.cellargalaxy.bean.monitor.Equipment;
import top.cellargalaxy.bean.monitor.Malfunction;
import top.cellargalaxy.bean.monitor.Place;
import top.cellargalaxy.bean.serviceBean.Build;
import top.cellargalaxy.configuration.MonitorConfiguration;
import top.cellargalaxy.dao.EquipmentMapper;
import top.cellargalaxy.dao.MalfunctionMapper;
import top.cellargalaxy.dao.PlaceMapper;
import top.cellargalaxy.util.CsvDeal;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by cellargalaxy on 17-12-8.
 */
@Service
@Transactional
public class MonitorServiceImpl implements MonitorService {
	@Autowired
	private EquipmentMapper equipmentMapper;
	@Autowired
	private MalfunctionMapper malfunctionMapper;
	@Autowired
	private PlaceMapper placeMapper;
	@Autowired
	private MonitorConfiguration monitorConfiguration;
	@Autowired
	private PersonelSdk personelSdk;
	@Autowired
	private WxSdk wxSdk;
	
	private LinkedList<Build> builds;
	private LinkedList<Build> netview;
	private volatile int malfunctionPageCount;
	private final String nullBuildName = "空闲设备";
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public MonitorServiceImpl() {
		malfunctionPageCount = -1;
	}
	
	@Override
	public synchronized boolean init() {
		try {
			builds = null;
			initNetview(getBuilds());
			countMalfunctionPageCount();
			return true;
		} catch (Exception e) {
			dealException(e);
		}
		return false;
	}
	
	@Override
	public void addChangeStatusEquipment(Equipment equipment) {
		try {
			if (equipment.isStatus()) {
				logger.info("通 " + equipment.getFullName());
			} else {
				logger.info("挂 " + equipment.getFullName());
			}
			wxSdk.addChangeStatusEquipment(equipment);
			malfunctionMapper.insertMalfunction(new Malfunction(equipment));
		} catch (Exception e) {
			dealException(e);
		}
	}
	
	@Override
	public synchronized LinkedList<Build> findWarmNetview() {
		try {
			LinkedList<Build> warmBuilds = new LinkedList<>();
			for (Build build : getNetview()) {
				if (!build.isStatus()) {
					Build b = new Build(build.getName());
					for (Equipment equipment : build.getEquipments()) {
						if (!equipment.isStatus()) {
							b.addEquipment(equipment);
						}
					}
					warmBuilds.add(b);
				}
			}
			return warmBuilds;
		} catch (Exception e) {
			dealException(e);
		}
		return null;
	}
	
	@Override
	public synchronized LinkedList<Build> findNetview() {
		return getNetview();
	}
	
	///////////////////////////////////////////////////////////////
	@Override
	public boolean addPlace(Place place) {
		try {
			if (placeMapper.insertPlace(place) > 0) {
				addBuild(place.getBuild());
				return true;
			}
		} catch (Exception e) {
			dealException(e);
		}
		return false;
	}
	
	@Override
	public boolean removePlace(Place place) {
		try {
			equipmentMapper.deleteEquipmentByPlace(place);
			if (placeMapper.deletePlace(place) > 0) {
				removeBuild(place.getBuild());
				return true;
			}
		} catch (Exception e) {
			dealException(e);
		}
		return false;
	}
	
	@Override
	public Place[] findAllPlace() {
		try {
			return placeMapper.selectAllPlace();
		} catch (Exception e) {
			dealException(e);
		}
		return new Place[0];
	}
	
	/////////////////////////////////////////////////////////////////////
	@Override
	public boolean addEquipment(Equipment equipment) {
		try {
			if (equipment.getIp() != null && equipment.getIp().length() == 0) {
				equipment.setIp(null);
			}
			Place place = new Place(equipment.getArea(), equipment.getBuild(), equipment.getFloor(), equipment.getNumber());
			addBuild(place.getBuild());
			placeMapper.insertPlace(place);
		} catch (Exception e) {
		}
		try {
			if (equipmentMapper.insertEquipment(equipment) > 0) {
				addEquipmentIntoBuilds(equipment);
				return true;
			}
		} catch (Exception e) {
			dealException(e);
		}
		return false;
	}
	
	@Override
	public LinkedList<Equipment> addEquipments(File file) {
		try {
			Iterable<CSVRecord> records = CsvDeal.createCSVRecords(file);
			if (records == null) {
				return null;
			}
			Iterator<CSVRecord> iterator = records.iterator();
			iterator.next();
			LinkedList<Equipment> equipments = new LinkedList<>();
			while (iterator.hasNext()) {
				CSVRecord record = iterator.next();
				Map<String, String> map = record.toMap();
				String id = map.get("id").trim();
				if (id == null || id.length() == 0) {
					id = UUID.randomUUID().toString();
				}
				String area = map.get("area");
				if (area == null || area.length() == 0) {
					area = "龙洞";
				}
				Integer number = CsvDeal.string2Int(map.get("number"));
				if (number == null) {
					number = 0;
				}
				Date buyDate = CsvDeal.string2Date(map.get("buyDate"));
				if (buyDate == null) {
					buyDate = new Date();
				}
				Integer checkTimes = CsvDeal.string2Int(map.get("checkTimes"));
				if (checkTimes == null) {
					checkTimes = Equipment.DEFAULT_CHECK_TIMES;
				}
				Integer isWarn = CsvDeal.string2Int(map.get("isWarn"));
				if (isWarn == null) {
					isWarn = Equipment.IS_WARM_NUM;
				}
				Date installDate = CsvDeal.string2Date(map.get("installDate"));
				equipments.add(new Equipment(id, map.get("model"), map.get("name"), buyDate, area, map.get("build"),
						map.get("floor"), number, map.get("ip"), checkTimes, isWarn, map.get("remark"), installDate));
			}
			LinkedList<Equipment> fail = new LinkedList<>();
			for (Equipment equipment : equipments) {
				if (!addEquipment(equipment)) {
					fail.add(equipment);
				}
			}
			return fail;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public synchronized boolean writeEquipmentFile(File file) {
		CSVPrinter csvPrinter = null;
		try {
			csvPrinter = CsvDeal.createCSVPrinter(file);
			if (csvPrinter == null) {
				return false;
			}
			csvPrinter.printRecord("id", "model", "name", "buyDate", "area", "build", "floor", "number", "ip", "checkTimes", "isWarn", "remark", "installDate");
			csvPrinter.printRecord("编号(字符串)", "机型(字符串)", "名字(字符串)", "购买日期(" + CsvDeal.DATE_FORMAT_STRING + ")", "校区(字符串)", "楼栋(字符串)", "楼层(字符串)", "序号(数字)", "ip(字符串)", "检测次数(数字)", "是否警告(0:否,1:是)", "备注(字符串)", "安装日期(" + CsvDeal.DATE_FORMAT_STRING + ")");
			LinkedList<Build> builds = getBuilds();
			for (Build build : builds) {
				for (Equipment e : build.getEquipments()) {
					String buyDate = CsvDeal.date2String(e.getBuyDate());
					String installDate = CsvDeal.date2String(e.getInstallDate());
					csvPrinter.printRecord(e.getId(), e.getModel(), e.getName(), buyDate, e.getArea(), e.getBuild(), e.getFloor(),
							e.getNumber(), e.getIp(), e.getCheckTimes(), e.getIsWarn(), e.getRemark(), installDate);
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (csvPrinter != null) {
				try {
					csvPrinter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean removeEquipment(String id) {
		try {
			if (equipmentMapper.deleteEquipment(id) > 0) {
				removeEquipmentFromBuilds(id);
				return true;
			}
		} catch (Exception e) {
			dealException(e);
		}
		return false;
	}
	
	@Override
	public Equipment findEquipmentById(String id) {
		try {
			return equipmentMapper.selectEquipmentById(id);
		} catch (Exception e) {
			dealException(e);
		}
		return null;
	}
	
	@Override
	public LinkedList<Build> findAllEquipment() {
		return getBuilds();
	}
	
	@Override
	public boolean changeEquipment(Equipment equipment) {
		try {
			if (equipment.getIp() != null && equipment.getIp().length() == 0) {
				equipment.setIp(null);
			}
			Place place = new Place(equipment.getArea(), equipment.getBuild(), equipment.getFloor(), equipment.getNumber());
			addBuild(place.getBuild());
			placeMapper.insertPlace(place);
		} catch (Exception e) {
		}
		try {
			if (equipmentMapper.updateEquipment(equipment) > 0) {
				changeEquipmentFromBuilds(equipment);
				return true;
			}
		} catch (Exception e) {
			dealException(e);
		}
		return false;
	}
	
	/////////////////////////////////////////////////////////////////
	
	@Override
	public boolean removeMalfunction(Malfunction malfunction) {
		try {
			malfunctionPageCount = -1;
			return malfunctionMapper.deleteMalfunction(malfunction) > 0;
		} catch (Exception e) {
			dealException(e);
		}
		return false;
	}
	
	@Override
	public Malfunction[] findMalfunctions(int page) {
		try {
			int len = monitorConfiguration.getListMalfunctionLength();
			int off = (page - 1) * len;
			return malfunctionMapper.selectMalfunctions(off, len);
		} catch (Exception e) {
			dealException(e);
		}
		return new Malfunction[0];
	}
	
	@Override
	public int getMalfunctionPageCount() {
		try {
			if (malfunctionPageCount == -1) {
				countMalfunctionPageCount();
			}
			return malfunctionPageCount;
		} catch (Exception e) {
			dealException(e);
		}
		return 0;
	}
	
	///////////////////////////////////////////////////////////////////
	@Override
	public boolean checkToken(String token) {
		try {
			if (token != null) {
				return token.equals(monitorConfiguration.getToken());
			}
		} catch (Exception e) {
			dealException(e);
		}
		return false;
	}
	
	@Override
	public boolean checkPassword(String id, String password) {
		return personelSdk.checkPassword(id, password);
	}
	
	@Override
	public boolean checkMonitorDisabled(String id) {
		return personelSdk.checkMonitorDisabled(id);
	}
	
	@Override
	public boolean checkMonitorAdmin(String id) {
		return personelSdk.checkMonitorAdmin(id);
	}
	
	@Override
	public boolean checkMonitorRoot(String id) {
		return personelSdk.checkMonitorRoot(id);
	}
	
	/////////////////////////////////////////////////////////////////////////
	private synchronized void initBuilds() {
		Equipment[] equipments = equipmentMapper.selectAllEquipment();
		builds = new LinkedList<>();
		Build nullBuild = new Build(nullBuildName);
		builds.add(nullBuild);
		mian:
		for (Equipment equipment : equipments) {
			if (equipment.getBuild() == null || equipment.getBuild().length() == 0) {
				nullBuild.addEquipment(equipment);
				continue mian;
			}
			for (Build build : builds) {
				if (build.getName().equals(equipment.getBuild())) {
					build.addEquipment(equipment);
					continue mian;
				}
			}
			Build build = new Build(equipment.getBuild());
			build.addEquipment(equipment);
			builds.add(build);
		}
		Collections.sort(builds);
		netview = null;
	}
	
	private LinkedList<Build> getBuilds() {
		if (builds == null) {
			initBuilds();
		}
		return builds;
	}
	
	private synchronized void addBuild(String buildName) {
		if (buildName == null || buildName.length() == 0) {
			return;
		}
		LinkedList<Build> builds = getBuilds();
		for (Build build : builds) {
			if (build.getName().equals(buildName)) {
				return;
			}
		}
		builds.add(new Build(buildName));
		initNetview(getBuilds());
	}
	
	private synchronized void removeBuild(String buildName) {
		if (buildName == null) {
			return;
		}
		LinkedList<Build> builds = getBuilds();
		Iterator<Build> iterator = builds.iterator();
		while (iterator.hasNext()) {
			Build build = iterator.next();
			if (build.getName().equals(buildName)) {
				iterator.remove();
				return;
			}
		}
		initNetview(getBuilds());
	}
	
	private synchronized void addEquipmentIntoBuilds(Equipment equipment) {
		if (equipment == null) {
			return;
		}
		LinkedList<Build> builds = getBuilds();
		Build nullBuild = null;
		for (Build build : builds) {
			if (build.getName().equals(equipment.getBuild())) {
				build.addEquipment(equipment);
				return;
			} else if (build.getName().equals(nullBuildName)) {
				nullBuild = build;
			}
		}
		if (equipment.getBuild() != null && equipment.getBuild().length() > 0) {
			Build build = new Build(equipment.getBuild());
			build.addEquipment(equipment);
			builds.add(build);
		} else {
			nullBuild.addEquipment(equipment);
		}
		initNetview(getBuilds());
	}
	
	private synchronized void removeEquipmentFromBuilds(String id) {
		if (id == null) {
			return;
		}
		LinkedList<Build> builds = getBuilds();
		Iterator<Build> iterator1 = builds.iterator();
		while (iterator1.hasNext()) {
			Build build = iterator1.next();
			Iterator<Equipment> iterator2 = build.getEquipments().iterator();
			while (iterator2.hasNext()) {
				Equipment equipment1 = iterator2.next();
				if (equipment1.getId().equals(id)) {
					iterator2.remove();
					if (build.getEquipments().size() == 0) {
						iterator1.remove();
					}
					return;
				}
			}
		}
		initNetview(getBuilds());
	}
	
	private synchronized void changeEquipmentFromBuilds(Equipment equipment) {
		if (equipment == null) {
			return;
		}
		LinkedList<Build> builds = getBuilds();
		for (Build build : builds) {
			if (build.getName().equals(equipment.getBuild())) {
				for (Equipment equipment1 : build.getEquipments()) {
					if (equipment1.getId().equals(equipment.getId())) {
						equipment1.set(equipment);
						return;
					}
				}
			} else if (build.getName().equals(nullBuildName)) {
				for (Equipment equipment1 : build.getEquipments()) {
					if (equipment1.getId().equals(equipment.getId())) {
						equipment1.set(equipment);
						return;
					}
				}
			}
		}
		initNetview(getBuilds());
	}
	
	private synchronized void initNetview(LinkedList<Build> builds) {
		if (builds == null) {
			return;
		}
		netview = new LinkedList<>();
		for (Build build : builds) {
			Build b = new Build(build.getName());
			for (Equipment equipment : build.getEquipments()) {
				if (equipment.getIp() != null && equipment.getIp().length() > 0) {
					b.addEquipment(equipment);
				}
			}
			if (b.getEquipments().size() > 0) {
				netview.add(b);
			}
		}
	}
	
	private LinkedList<Build> getNetview() {
		if (netview == null) {
			initNetview(getBuilds());
		}
		return netview;
	}
	
	private void countMalfunctionPageCount() {
		int count = malfunctionMapper.selectMalfunctionCount();
		int len = monitorConfiguration.getListMalfunctionLength();
		malfunctionPageCount = countPageCount(count, len);
	}
	
	private int countPageCount(int count, int len) {
		if (count % len == 0) {
			return count / len;
		} else {
			return count / len + 1;
		}
	}
	
	private void dealException(Exception e) {
		e.printStackTrace();
		TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	}
}

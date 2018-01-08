package top.cellargalaxy.controlor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.cellargalaxy.bean.controlorBean.ReturnBean;
import top.cellargalaxy.bean.monitor.Equipment;
import top.cellargalaxy.bean.monitor.Malfunction;
import top.cellargalaxy.bean.monitor.Place;
import top.cellargalaxy.bean.serviceBean.Build;
import top.cellargalaxy.service.MonitorService;
import top.cellargalaxy.util.ControlorUtil;
import top.cellargalaxy.util.LogUtil;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.LinkedList;

/**
 * Created by cellargalaxy on 17-12-28.
 */
@RestController
@RequestMapping(MonitorAdminControlor.MONITOR_ADMIN_CONTROLOR_URL)
public class MonitorAdminControlor {
	public static final String MONITOR_ADMIN_CONTROLOR_URL = "/admin";
	@Autowired
	private MonitorService monitorService;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@ResponseBody
	@GetMapping("/downloadEquipmentFile")
	public Resource downloadEquipmentFile(HttpServletResponse response, HttpSession session) {
		LogUtil.info(logger, session, "下载设备文件");
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment;filename=\"ip.csv\"");
		File file = new File("src/main/resources/static/upload/equipment.csv");
		monitorService.writeEquipmentFile(file);
		return new FileSystemResource(file.getAbsoluteFile());
	}
	
	@ResponseBody
	@PostMapping("/uploadEquipmentFile")
	public ReturnBean uploadEquipmentFile(@RequestParam("file") MultipartFile multipartFile, HttpSession session) {
		File file = ControlorUtil.saveFile(multipartFile);
		if (file == null) {
			LogUtil.info(logger, session, "文件上传失败");
			return new ReturnBean(false, "文件上传失败");
		} else {
			LinkedList<Equipment> equipments = monitorService.addEquipments(file);
			file.delete();
			if (equipments == null) {
				LogUtil.info(logger, session, "文件解析失败");
				return new ReturnBean(false, "文件解析失败");
			}
			if (equipments.size() == 0) {
				LogUtil.info(logger, session, "全部设备添加成功");
				return new ReturnBean(false, "全部设备添加成功");
			}
			LogUtil.info(logger, session, "添加失败设备");
			return new ReturnBean(true, new Build("添加失败设备", equipments));
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	@ResponseBody
	@PostMapping("/refreshNetview")
	public ReturnBean refreshNetview(HttpSession session) {
		if (monitorService.init()) {
			LogUtil.info(logger, session, "重置监控状态成功");
			return new ReturnBean(true, "重置监控状态成功");
		} else {
			LogUtil.info(logger, session, "重置监控状态失败");
			return new ReturnBean(false, "重置监控状态失败");
		}
	}
	
	//////////////////////////////////////////////////////////
	@ResponseBody
	@PostMapping("/addEquipment")
	public ReturnBean addEquipment(Equipment equipment, HttpSession session) {
		if (monitorService.addEquipment(equipment)) {
			LogUtil.info(logger, session, "添加设备成功 " + equipment);
			return new ReturnBean(true, "添加设备成功");
		} else {
			LogUtil.info(logger, session, "添加设备失败 " + equipment);
			return new ReturnBean(false, "添加设备失败");
		}
	}
	
	@ResponseBody
	@PostMapping("/removeEquipment")
	public ReturnBean removeEquipment(String id, HttpSession session) {
		if (monitorService.removeEquipment(id)) {
			LogUtil.info(logger, session, "删除设备成功 " + id);
			return new ReturnBean(true, "删除设备成功");
		} else {
			LogUtil.info(logger, session, "删除设备成功 " + id);
			return new ReturnBean(false, "删除设备失败");
		}
	}
	
	@ResponseBody
	@PostMapping("/changeEquipment")
	public ReturnBean changeEquipment(Equipment equipment, HttpSession session) {
		if (monitorService.changeEquipment(equipment)) {
			LogUtil.info(logger, session, "修改设备成功 " + equipment);
			return new ReturnBean(true, "修改设备成功");
		} else {
			LogUtil.info(logger, session, "修改设备失败 " + equipment);
			return new ReturnBean(false, "修改设备失败");
		}
	}
	
	//////////////////////////////////////////////////////////////////////
	@ResponseBody
	@PostMapping("/addPlace")
	public ReturnBean addPlace(HttpSession session, Place place) {
		String loginPerson = ControlorUtil.getLoginPerson(session);
		if (!monitorService.checkMonitorRoot(loginPerson)) {
			return new ReturnBean(false, "你没有root权限，请用root账号登录");
		}
		if (monitorService.addPlace(place)) {
			LogUtil.info(logger, loginPerson, "添加地点成功 " + place);
			return new ReturnBean(true, "添加地点成功");
		} else {
			LogUtil.info(logger, loginPerson, "添加地点失败 " + place);
			return new ReturnBean(false, "添加地点失败");
		}
	}
	
	@ResponseBody
	@PostMapping("/removePlace")
	public ReturnBean removePlace(HttpSession session, Place place) {
		String loginPerson = ControlorUtil.getLoginPerson(session);
		if (!monitorService.checkMonitorRoot(loginPerson)) {
			return new ReturnBean(false, "你没有root权限，请用root账号登录");
		}
		if (monitorService.removePlace(place)) {
			LogUtil.info(logger, loginPerson, "删除地点成功 " + place);
			return new ReturnBean(true, "删除地点成功");
		} else {
			LogUtil.info(logger, loginPerson, "删除地点失败 " + place);
			return new ReturnBean(false, "删除地点失败");
		}
	}
	
	///////////////////////////////////////////////////////////////////
	@ResponseBody
	@PostMapping("/removeMalfunction")
	public ReturnBean removeMalfunction(Malfunction malfunction, HttpSession session) {
		if (monitorService.removeMalfunction(malfunction)) {
			LogUtil.info(logger, session, "删除故障记录成功 " + malfunction);
			return new ReturnBean(true, "删除故障记录成功");
		} else {
			LogUtil.info(logger, session, "删除故障记录失败 " + malfunction);
			return new ReturnBean(false, "删除故障记录失败");
		}
	}
}

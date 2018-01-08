package top.cellargalaxy.bean.monitor;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import top.cellargalaxy.bean.serviceBean.PingResult;

import java.util.Date;
import java.util.LinkedList;

/**
 * Created by cellargalaxy on 17-12-7.
 */
public class Equipment {
	public static final int NOT_WARM_NUM = 0;
	public static final int IS_WARM_NUM = 1;
	public static final int DEFAULT_CHECK_TIMES = 3;
	
	private String id;
	private String model;
	private String name;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	private Date buyDate;
	private String area;
	private String build;
	private String floor;
	private int number;
	private String ip;
	private int checkTimes;
	private int isWarn;
	private String remark;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	private Date installDate;
	
	private boolean status;
	@JsonFormat(timezone = "GMT+8", pattern = "MM-dd kk:mm:ss")
	private Date date;
	private final LinkedList<PingResult> pingResults;
	
	public Equipment() {
		status = true;
		date = new Date();
		pingResults = new LinkedList<>();
	}
	
	public Equipment(String id, String model, String name, Date buyDate, String area, String build, String floor, int number, String ip, int checkTimes, int isWarn, String remark, Date installDate) {
		this.id = id;
		this.model = model;
		this.name = name;
		this.buyDate = buyDate;
		this.area = area;
		this.build = build;
		this.floor = floor;
		this.number = number;
		this.ip = ip;
		this.checkTimes = checkTimes;
		this.isWarn = isWarn;
		this.remark = remark;
		this.installDate = installDate;
		status = true;
		date = new Date();
		pingResults = new LinkedList<>();
	}
	
	/**
	 * @param pingResult 如果状态改变返回True，否则False
	 * @return
	 */
	public boolean addPingResult(PingResult pingResult) {
		if (pingResult == null) {
			return false;
		}
		LinkedList<PingResult> pingResults = getPingResults();
		pingResults.removeFirst();
		pingResults.add(pingResult);
		if (!status) {//原本挂
			if (pingResult.getDelay() > PingResult.DEFAULT_DELAY_NUM) {//现在不挂了
				status = true;
				date = pingResult.getDate();
				return true;
			}
			return false;
		} else {//原本不挂
			for (PingResult result : pingResults) {
				if (result.getDelay() > PingResult.DEFAULT_DELAY_NUM) {//还是有不挂的
					return false;
				}
			}
			status = false;
			date = pingResult.getDate();
			return true;
		}
	}
	
	public void set(Equipment equipment) {
		id = equipment.getId();
		model = equipment.getModel();
		name = equipment.getName();
		buyDate = equipment.getBuyDate();
		area = equipment.getArea();
		build = equipment.getBuild();
		floor = equipment.getFloor();
		number = equipment.getNumber();
		ip = equipment.getIp();
		checkTimes = equipment.getCheckTimes();
		isWarn = equipment.getIsWarn();
		remark = equipment.getRemark();
		installDate = equipment.getInstallDate();
	}
	
	public String getFullName() {
		return build + "-" + floor + "-" + model + "-" + number;
	}
	
	public boolean isStatus() {
		return status;
	}
	
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public LinkedList<PingResult> getPingResults() {
		if (checkTimes != pingResults.size()) {
			pingResults.clear();
			for (int i = 0; i < checkTimes; i++) {
				pingResults.add(new PingResult(new Date(), 0));
			}
		}
		return pingResults;
	}
	////////////////////////////////////////////////////////////
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getModel() {
		return model;
	}
	
	public void setModel(String model) {
		this.model = model;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Date getBuyDate() {
		return buyDate;
	}
	
	public void setBuyDate(Date buyDate) {
		this.buyDate = buyDate;
	}
	
	public String getArea() {
		return area;
	}
	
	public void setArea(String area) {
		this.area = area;
	}
	
	public String getBuild() {
		return build;
	}
	
	public void setBuild(String build) {
		this.build = build;
	}
	
	public String getFloor() {
		return floor;
	}
	
	public void setFloor(String floor) {
		this.floor = floor;
	}
	
	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public int getCheckTimes() {
		return checkTimes;
	}
	
	public void setCheckTimes(int checkTimes) {
		this.checkTimes = checkTimes;
	}
	
	public int getIsWarn() {
		return isWarn;
	}
	
	public void setIsWarn(int isWarn) {
		this.isWarn = isWarn;
	}
	
	public String getRemark() {
		return remark;
	}
	
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public Date getInstallDate() {
		return installDate;
	}
	
	public void setInstallDate(Date installDate) {
		this.installDate = installDate;
	}
	
	@Override
	public String toString() {
		return "Equipment{" +
				"id='" + id + '\'' +
				", model='" + model + '\'' +
				", name='" + name + '\'' +
				", buyDate=" + buyDate +
				", area='" + area + '\'' +
				", build='" + build + '\'' +
				", floor='" + floor + '\'' +
				", number=" + number +
				", ip='" + ip + '\'' +
				", checkTimes=" + checkTimes +
				", isWarn=" + isWarn +
				", remark='" + remark + '\'' +
				", installDate=" + installDate +
				'}';
	}
}

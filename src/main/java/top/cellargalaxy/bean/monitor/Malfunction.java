package top.cellargalaxy.bean.monitor;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by cellargalaxy on 17-12-7.
 */
public class Malfunction {
	public static final int NOT_CON_STATUS =0;
	public static final int CON_STATUS =1;
	
	private String area;
	private String build;
	private String floor;
	private int number;
	private String equipmentId;
	@DateTimeFormat(pattern = "yyyy-MM-dd kk:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd kk:mm:ss")
	private Date malfunctionDatetime;
	private int status;
	
	public Malfunction() {
	}
	
	public Malfunction(Equipment equipment) {
		area=equipment.getArea();
		build=equipment.getBuild();
		floor=equipment.getFloor();
		number=equipment.getNumber();
		equipmentId=equipment.getId();
		malfunctionDatetime=equipment.getDate();
		if (equipment.isStatus()) {
			status=CON_STATUS;
		}else {
			status=NOT_CON_STATUS;
		}
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
	
	public String getEquipmentId() {
		return equipmentId;
	}
	
	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}
	
	public Date getMalfunctionDatetime() {
		return malfunctionDatetime;
	}
	
	public void setMalfunctionDatetime(Date malfunctionDatetime) {
		this.malfunctionDatetime = malfunctionDatetime;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "Malfunction{" +
				"area='" + area + '\'' +
				", build='" + build + '\'' +
				", floor='" + floor + '\'' +
				", number=" + number +
				", equipmentId='" + equipmentId + '\'' +
				", malfunctionDatetime=" + malfunctionDatetime +
				", status=" + status +
				'}';
	}
}

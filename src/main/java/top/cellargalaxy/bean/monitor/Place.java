package top.cellargalaxy.bean.monitor;

/**
 * Created by cellargalaxy on 17-12-7.
 */
public class Place {
	private String area;
	private String build;
	private String floor;
	private int number;
	
	public Place() {
	}
	
	public Place(String area, String build, String floor, int number) {
		this.area = area;
		this.build = build;
		this.floor = floor;
		this.number = number;
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
	
	@Override
	public String toString() {
		return "Place{" +
				"area='" + area + '\'' +
				", build='" + build + '\'' +
				", floor='" + floor + '\'' +
				", number=" + number +
				'}';
	}
}

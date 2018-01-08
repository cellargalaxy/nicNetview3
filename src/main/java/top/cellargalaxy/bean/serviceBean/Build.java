package top.cellargalaxy.bean.serviceBean;

import top.cellargalaxy.bean.monitor.Equipment;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by cellargalaxy on 17-12-28.
 */
public class Build implements Comparable<Build> {
	private final String name;
	private final LinkedList<Equipment> equipments;
	private boolean status;
	
	public Build(String name) {
		this.name = name;
		equipments = new LinkedList<>();
	}
	
	public Build(String name, LinkedList<Equipment> equipments) {
		this.name = name;
		this.equipments = equipments;
	}
	
	public synchronized void addEquipment(Equipment equipment) {
		equipments.add(equipment);
	}
	
	public synchronized void removeEquipment(String id) {
		if (id == null) {
			return;
		}
		Iterator<Equipment> iterator = equipments.iterator();
		while (iterator.hasNext()) {
			Equipment equipment = iterator.next();
			if (id.equals(equipment.getId())) {
				iterator.remove();
				return;
			}
		}
	}
	
	public String getName() {
		return name;
	}
	
	public LinkedList<Equipment> getEquipments() {
		return equipments;
	}
	
	public boolean isStatus() {
		status = true;
		for (Equipment equipment : equipments) {
			if (!equipment.isStatus()) {
				status = false;
				break;
			}
		}
		return status;
	}
	
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "Build{" +
				"name='" + name + '\'' +
				", status=" + status +
				'}';
	}
	
	@Override
	public int compareTo(Build o) {
		if (name == null && (o == null || o.getName() == null)) {
			return 0;
		}
		if (name == null) {
			return 1;
		}
		return name.compareTo(o.getName());
	}
}

package top.cellargalaxy.service;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.cellargalaxy.bean.monitor.Equipment;
import top.cellargalaxy.configuration.MonitorConfiguration;
import top.cellargalaxy.util.HttpRequestBaseDeal;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by cellargalaxy on 17-12-31.
 */
@Component
public class WxSdkImpl implements WxSdk {
	@Autowired
	private MonitorConfiguration monitorConfiguration;
	
	private final LinkedList<Build> builds;
	
	public WxSdkImpl() {
		builds = new LinkedList<>();
	}
	
	@Override
	public void addChangeStatusEquipment(Equipment equipment) {
		try {
			if (equipment == null || equipment.getIsWarn() != Equipment.IS_WARM_NUM) {
				return;
			}
			synchronized (builds) {
				for (Build build : builds) {
					if (build.getName().equals(equipment.getBuild())) {
						build.addEquipment(equipment);
						return;
					}
				}
				Build build = new Build(equipment.getBuild());
				build.addEquipment(equipment);
				builds.add(build);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String createInfo() {
		String info = "";
		synchronized (builds) {
			Collections.sort(builds);
			Iterator<Build> iterator = builds.iterator();
			while (iterator.hasNext()) {
				Build build = iterator.next();
				if (build.getCons().size() == 0 && build.getNotCons().size() == 0) {
					iterator.remove();
				}
				if (build.getCons().size() == 1) {
					Equipment equipment = null;
					for (Map.Entry<String, Equipment> equipmentEntry : build.getCons().entrySet()) {
						equipment = equipmentEntry.getValue();
					}
					info += "通 " + equipment.getFullName() + " " + equipment.getIp() + "\n";
				} else if (build.getCons().size() > 1) {
					info += "通 " + build.getName() + "：" + build.getCons().size() + "台\n";
				}
				if (build.getNotCons().size() == 1) {
					Equipment equipment = null;
					for (Map.Entry<String, Equipment> equipmentEntry : build.getNotCons().entrySet()) {
						equipment = equipmentEntry.getValue();
					}
					info += "挂 " + equipment.getFullName() + " " + equipment.getIp() + "\n";
				} else if (build.getNotCons().size() > 1) {
					info += "挂 " + build.getName() + "：" + build.getNotCons().size() + "台\n";
				}
			}
		}
		return info;
	}
	
	@Scheduled(fixedDelay = 1000 * 30)
	public void send() {
		try {
			if (!monitorConfiguration.isWxAble()) {
				return;
			}
			String info = createInfo();
			if (info == null || info.length() == 0) {
				return;
			}
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("token", monitorConfiguration.getWxToken()));
			params.add(new BasicNameValuePair("info", info));
			HttpPost httpPost = new HttpPost(monitorConfiguration.getWxUrl());
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(urlEncodedFormEntity);
			String result = HttpRequestBaseDeal.executeHttpRequestBase(httpPost, 10000);
			if (result == null) {
				return;
			}
			JSONObject jsonObject = new JSONObject(result);
			if (jsonObject.has("result") || jsonObject.getBoolean("result")) {
				synchronized (builds) {
					builds.clear();
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private class Build implements Comparable<Build> {
		private final String name;
		private final Map<String, Equipment> cons;
		private final Map<String, Equipment> notCons;
		
		public Build(String name) {
			this.name = name;
			cons = new HashMap<>();
			notCons = new HashMap<>();
		}
		
		public synchronized void addEquipment(Equipment equipment) {
			if (equipment.isStatus()) {
				Iterator<Map.Entry<String, Equipment>> iterator = notCons.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<String, Equipment> entry = iterator.next();
					if (entry.getKey().equals(equipment.getId())) {
						iterator.remove();
						return;
					}
				}
			}
			if (equipment.isStatus()) {
				cons.put(equipment.getId(), equipment);
			} else {
				notCons.put(equipment.getId(), equipment);
			}
		}
		
		public String getName() {
			return name;
		}
		
		public Map<String, Equipment> getCons() {
			return cons;
		}
		
		public Map<String, Equipment> getNotCons() {
			return notCons;
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
}

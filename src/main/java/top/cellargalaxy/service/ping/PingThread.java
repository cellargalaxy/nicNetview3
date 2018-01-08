package top.cellargalaxy.service.ping;

import org.apache.commons.exec.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.cellargalaxy.bean.monitor.Equipment;
import top.cellargalaxy.bean.serviceBean.Build;
import top.cellargalaxy.bean.serviceBean.PingResult;
import top.cellargalaxy.configuration.MonitorConfiguration;
import top.cellargalaxy.service.MonitorService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

/**
 * Created by cellargalaxy on 17-12-7.
 */
@Component
public class PingThread {
	@Autowired
	private MonitorConfiguration monitorConfiguration;
	@Autowired
	private PingDealFactory pingDealFactory;
	@Autowired
	private MonitorService monitorService;
	
	private PingDeal pingDeal;
	private final LinkedList<Result> results;
	private int resultsLength;
	
	public PingThread() {
		results = new LinkedList<>();
		resultsLength = -1;
	}
	
	private Result ping(Equipment equipment) {
		try {
			if (equipment == null || equipment.getIp() == null) {
				return null;
			}
			DefaultExecutor executor = new DefaultExecutor();
			ExecuteWatchdog watchdog = new ExecuteWatchdog(monitorConfiguration.getMaxDelay() * monitorConfiguration.getPingTimes());
			executor.setWatchdog(watchdog);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			executor.setStreamHandler(new PumpStreamHandler(baos, baos));
			
			CommandLine cmdLine = pingDeal.createPingCommandLine(equipment.getIp(), monitorConfiguration.getPingTimes());
			DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
			executor.execute(cmdLine, resultHandler);
			return new Result(equipment, resultHandler, baos);
		} catch (ExecuteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Scheduled(fixedDelay = 5 * 1000)
	public void run() {
		if (pingDeal == null) {
			pingDeal = pingDealFactory.getPingDeal();
		}
		if (resultsLength == -1) {
			resultsLength = (monitorConfiguration.getMaxDelay() * monitorConfiguration.getPingTimes() / monitorConfiguration.getPingWaitTime()) + 1;
		}
		LinkedList<Build> netview = monitorService.findNetview();
		for (Build build : netview) {
			for (Equipment equipment : build.getEquipments()) {
				results.add(ping(equipment));
				try {
					Thread.sleep(monitorConfiguration.getPingWaitTime());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (results.size() >= resultsLength) {
					PingResult pingResult = null;
					Result result = null;
					try {
						result = results.poll();
						if (result != null) {
							pingResult = new PingResult();
							result.getResultHandler().waitFor();
							String string = result.getByteArrayOutputStream().toString(monitorConfiguration.getCoding());
							pingResult.setDelay(pingDeal.analysisDelay(string));
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} finally {
						if (result != null && result.getEquipment().addPingResult(pingResult)) {
							monitorService.addChangeStatusEquipment(result.getEquipment());
						}
					}
				}
			}
		}
	}
	
	
	private class Result {
		private final Equipment equipment;
		private final DefaultExecuteResultHandler resultHandler;
		private final ByteArrayOutputStream byteArrayOutputStream;
		
		public Result(Equipment equipment, DefaultExecuteResultHandler resultHandler, ByteArrayOutputStream byteArrayOutputStream) {
			this.equipment = equipment;
			this.resultHandler = resultHandler;
			this.byteArrayOutputStream = byteArrayOutputStream;
		}
		
		public Equipment getEquipment() {
			return equipment;
		}
		
		public DefaultExecuteResultHandler getResultHandler() {
			return resultHandler;
		}
		
		public ByteArrayOutputStream getByteArrayOutputStream() {
			return byteArrayOutputStream;
		}
		
		@Override
		public String toString() {
			return "Result{" +
					"equipment=" + equipment.getIp() +
					'}';
		}
	}
}

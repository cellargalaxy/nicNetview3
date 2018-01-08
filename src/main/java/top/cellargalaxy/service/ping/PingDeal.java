package top.cellargalaxy.service.ping;

import org.apache.commons.exec.CommandLine;

/**
 * Created by cellargalaxy on 17-12-8.
 */
public interface PingDeal {
	CommandLine createPingCommandLine(String ip, int pingTimes);
	
	int analysisDelay(String string);
}

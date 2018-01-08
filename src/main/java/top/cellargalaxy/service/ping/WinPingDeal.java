package top.cellargalaxy.service.ping;

import org.apache.commons.exec.CommandLine;

/**
 * Created by cellargalaxy on 17-12-8.
 */
public class WinPingDeal implements PingDeal {
	
	@Override
	public CommandLine createPingCommandLine(String ip, int pingTimes) {
		CommandLine cmdLine = new CommandLine("ping");
		cmdLine.addArgument(ip);
		cmdLine.addArgument("-n");
		cmdLine.addArgument(pingTimes + "");
		return cmdLine;
	}
	
	@Override
	public int analysisDelay(String string) {
		try {
			int start = string.lastIndexOf(" = ");
			if (start > -1) {
				string = string.substring(start + 3);
				int end = string.indexOf("ms");
				if (end > -1) {
					string = string.substring(0, end);
					return new Integer(string);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
}

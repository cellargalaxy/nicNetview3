package top.cellargalaxy.service.ping;

import org.apache.commons.exec.CommandLine;

/**
 * Created by cellargalaxy on 17-12-8.
 */
public class LinuxPingDeal implements PingDeal {
	
	@Override
	public CommandLine createPingCommandLine(String ip, int pingTimes) {
		CommandLine cmdLine = new CommandLine("ping");
		cmdLine.addArgument(ip);
		cmdLine.addArgument("-c");
		cmdLine.addArgument(pingTimes + "");
		return cmdLine;
	}
	
	@Override
	public int analysisDelay(String string) {
		try {
			int start = string.indexOf("mdev = ");
			if (start > -1) {
				string = string.substring(start + 7);
				string = string.substring(0, string.length() - 4);
			}
			String[] strings = string.split("/");
			return new Double(strings[1]).intValue();
		} catch (Exception e) {
		}
		return -1;
	}
}

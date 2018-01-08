package top.cellargalaxy.service.ping;


import org.springframework.stereotype.Component;

/**
 * Created by cellargalaxy on 17-12-8.
 */
@Component
public class PingDealFactory {
	private PingDeal pingDeal;
	
	public final PingDeal getPingDeal() {
		if (pingDeal == null) {
			if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
				pingDeal = new WinPingDeal();
			} else {
				pingDeal = new LinuxPingDeal();
			}
		}
		return pingDeal;
	}
}

package top.cellargalaxy.service;


/**
 * Created by cellargalaxy on 17-12-31.
 */
public interface PersonelSdk {
	boolean checkPassword(String id, String password);
	
	boolean checkMonitorDisabled(String id);
	
	boolean checkMonitorAdmin(String id);
	
	boolean checkMonitorRoot(String id);
}

package top.cellargalaxy.util;

import org.slf4j.Logger;

import javax.servlet.http.HttpSession;

/**
 * Created by cellargalaxy on 18-1-2.
 */
public class LogUtil {
	
	public static final void info(Logger logger, HttpSession session, String string) {
		String person = ControlorUtil.getLoginPerson(session);
		info(logger, person, string);
	}
	
	public static final void info(Logger logger, Object person, String string) {
		try {
			logger.info(person.toString() + " " + string);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

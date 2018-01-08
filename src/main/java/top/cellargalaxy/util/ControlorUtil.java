package top.cellargalaxy.util;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by cellargalaxy on 17-12-13.
 */
public class ControlorUtil {
	private static final String loginPersonName = "loginPerson";
	private static final String infoName = "info";
	
	public static final File saveFile(MultipartFile multipartFile) {
		File file = new File("src/main/resources/static/upload/" + (int) (Math.random() * 10000) + multipartFile.getOriginalFilename());
		return saveFile(multipartFile, file);
	}
	
	public static final File saveFile(MultipartFile multipartFile, File file) {
		BufferedOutputStream out = null;
		try {
			if (multipartFile == null || multipartFile.isEmpty()) {
				return null;
			}
			file.getParentFile().mkdirs();
			out = new BufferedOutputStream(new FileOutputStream(file));
			out.write(multipartFile.getBytes());
			return file;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static final void setLoginPerson(HttpSession session, String id) {
		session.setAttribute(loginPersonName, id);
	}
	
	public static final String getLoginPerson(HttpSession session) {
		Object loginPerson = session.getAttribute(loginPersonName);
		if (loginPerson == null || !(loginPerson instanceof String)) {
			return null;
		}
		return (String) loginPerson;
	}
	
	public static final String getInfo(HttpSession session) {
		Object info = session.getAttribute(infoName);
		if (info != null) {
			session.setAttribute(infoName, null);
			return info.toString();
		} else {
			return null;
		}
	}
	
	public static final void setInfo(HttpSession session, String info) {
		session.setAttribute(infoName, info);
	}
}

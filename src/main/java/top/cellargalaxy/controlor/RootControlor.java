package top.cellargalaxy.controlor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.cellargalaxy.bean.controlorBean.ReturnBean;
import top.cellargalaxy.service.MonitorService;
import top.cellargalaxy.util.ControlorUtil;
import top.cellargalaxy.util.LogUtil;

import javax.servlet.http.HttpSession;

/**
 * Created by cellargalaxy on 18-1-7.
 */
@Controller
@RequestMapping(RootControlor.MONITOR_CONTROLOR_URL)
public class RootControlor {
	public static final String MONITOR_CONTROLOR_URL = "/";
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private MonitorService monitorService;
	
	@GetMapping("")
	public String home(Model model) {
		model.addAttribute("builds", monitorService.findWarmNetview());
		return "listSimpleNetview";
	}
	
	@GetMapping("login")
	public String login(Model model, HttpSession session) {
		model.addAttribute("info", ControlorUtil.getInfo(session));
		return "login";
	}
	
	@ResponseBody
	@PostMapping("login")
	public ReturnBean login(String id, String password, HttpSession session) {
		if (monitorService.checkMonitorDisabled(id)) {
			LogUtil.info(logger, id, "账号为禁用状态，请等待管理员激活");
			return new ReturnBean(false, "账号为禁用状态，请等待管理员激活");
		}
		if (monitorService.checkPassword(id, password)) {
			ControlorUtil.setLoginPerson(session, id);
			LogUtil.info(logger, id, "登录成功");
			return new ReturnBean(true, "登录成功");
		} else {
			LogUtil.info(logger, id, "登录失败");
			return new ReturnBean(false, "登录失败");
		}
	}
}

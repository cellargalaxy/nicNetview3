package top.cellargalaxy.controlor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by cellargalaxy on 17-12-28.
 */
@Controller
@RequestMapping(MonitorPageControlor.MONITOR_PAGE_CONTROLOR_URL)
public class MonitorPageControlor {
	public static final String MONITOR_PAGE_CONTROLOR_URL = "/page";
	
	@GetMapping("")
	public String monitor() {
		return "monitor";
	}
	
	@GetMapping("/listNetview")
	public String listNetview() {
		return "listNetview";
	}
	
	@GetMapping("/listEquipment")
	public String listEquipment() {
		return "listEquipment";
	}
	
	@GetMapping("/listPlace")
	public String listPlace() {
		return "listPlace";
	}
	
	@GetMapping("/listMalfunction")
	public String listMalfunction() {
		return "listMalfunction";
	}
	
	@GetMapping("/addEquipment")
	public String addEquipment() {
		return "addEquipment";
	}
	
	@GetMapping("/addEquipments")
	public String addEquipments() {
		return "addEquipments";
	}
	
	@GetMapping("/changeEquipment/{id}")
	public String changeEquipment(Model model, @PathVariable("id") String id) {
		model.addAttribute("id", id);
		return "changeEquipment";
	}
}

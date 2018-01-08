package top.cellargalaxy.bean.serviceBean;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Created by cellargalaxy on 17-12-7.
 */
public class PingResult {
	public static final int DEFAULT_DELAY_NUM = -1;
	
	@JsonFormat(timezone = "GMT+8", pattern = "MM-dd kk:mm:ss")
	private final Date date;
	private int delay;
	
	public PingResult() {
		date = new Date();
		delay = DEFAULT_DELAY_NUM;
	}
	
	public PingResult(Date date, int delay) {
		this.date = date;
		this.delay = delay;
	}
	
	public Date getDate() {
		return date;
	}
	
	public int getDelay() {
		return delay;
	}
	
	public void setDelay(int delay) {
		this.delay = delay;
	}
}

package top.cellargalaxy.service;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.cellargalaxy.configuration.MonitorConfiguration;
import top.cellargalaxy.util.HttpRequestBaseDeal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cellargalaxy on 17-12-31.
 */
@Component
public class PersonelSdkImpl implements PersonelSdk {
	@Autowired
	private MonitorConfiguration monitorConfiguration;
	
	@Override
	public boolean checkPassword(String id, String password) {
		try {
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("token", monitorConfiguration.getPersonelToken()));
			params.add(new BasicNameValuePair("id", id));
			params.add(new BasicNameValuePair("password", password));
			HttpGet httpGet = HttpRequestBaseDeal.createHttpGet(monitorConfiguration.getInquirePersonPasswordUrl(), params);
			String result = HttpRequestBaseDeal.executeHttpRequestBase(httpGet, monitorConfiguration.getPersonelTimeout());
			if (result == null) {
				return false;
			}
			JSONObject jsonObject = new JSONObject(result);
			return jsonObject.getBoolean("result");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean checkMonitorDisabled(String id) {
		try {
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("token", monitorConfiguration.getPersonelToken()));
			params.add(new BasicNameValuePair("personId", id));
			params.add(new BasicNameValuePair("permission", "-1"));
			HttpGet httpGet = HttpRequestBaseDeal.createHttpGet(monitorConfiguration.getInquireExistAuthorizedUrl(), params);
			String result = HttpRequestBaseDeal.executeHttpRequestBase(httpGet, monitorConfiguration.getPersonelTimeout());
			if (result == null) {
				return false;
			}
			JSONObject jsonObject = new JSONObject(result);
			return jsonObject.getBoolean("result");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean checkMonitorAdmin(String id) {
		try {
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("token", monitorConfiguration.getPersonelToken()));
			params.add(new BasicNameValuePair("personId", id));
			params.add(new BasicNameValuePair("permission", "11"));
			HttpGet httpGet = HttpRequestBaseDeal.createHttpGet(monitorConfiguration.getInquireExistAuthorizedUrl(), params);
			String result = HttpRequestBaseDeal.executeHttpRequestBase(httpGet, monitorConfiguration.getPersonelTimeout());
			if (result == null) {
				return false;
			}
			JSONObject jsonObject = new JSONObject(result);
			return jsonObject.getBoolean("result");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean checkMonitorRoot(String id) {
		try {
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("token", monitorConfiguration.getPersonelToken()));
			params.add(new BasicNameValuePair("personId", id));
			params.add(new BasicNameValuePair("permission", "10"));
			HttpGet httpGet = HttpRequestBaseDeal.createHttpGet(monitorConfiguration.getInquireExistAuthorizedUrl(), params);
			String result = HttpRequestBaseDeal.executeHttpRequestBase(httpGet, monitorConfiguration.getPersonelTimeout());
			if (result == null) {
				return false;
			}
			JSONObject jsonObject = new JSONObject(result);
			return jsonObject.getBoolean("result");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}

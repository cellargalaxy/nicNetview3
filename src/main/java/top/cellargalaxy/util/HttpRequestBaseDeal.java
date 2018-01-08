package top.cellargalaxy.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by cellargalaxy on 17-9-23.
 */
public class HttpRequestBaseDeal {
	private static final CloseableHttpClient httpClient = HttpClients.createDefault();
	
	public static final HttpGet createHttpGet(String url, List<NameValuePair> nameValuePairs) {
		try {
			if (url == null) {
				return null;
			}
			HttpGet httpGet = new HttpGet(url);
			if (nameValuePairs != null && nameValuePairs.size() > 0) {
				String paramsString = EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
				httpGet.setURI(new URI(httpGet.getURI().toString() + "?" + paramsString));
			}
			return httpGet;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static final HttpPost createHttpPost(String url, List<NameValuePair> nameValuePairs) {
		try {
			if (url == null) {
				return null;
			}
			HttpPost httpPost = new HttpPost(url);
			if (nameValuePairs != null && nameValuePairs.size() > 0) {
				String paramsString = EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
				httpPost.setURI(new URI(httpPost.getURI().toString() + "?" + paramsString));
			}
			return httpPost;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static final String executeHttpRequestBase(HttpRequestBase httpRequestBase,int timeout) {
		try {
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build();
			httpRequestBase.setConfig(requestConfig);
			HttpResponse httpResponse = httpClient.execute(httpRequestBase);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = httpResponse.getEntity();
				return EntityUtils.toString(entity, "utf-8");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

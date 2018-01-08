package top.cellargalaxy.controlor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import top.cellargalaxy.configuration.MonitorConfiguration;
import top.cellargalaxy.util.ControlorUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by cellargalaxy on 17-12-28.
 */
@WebFilter(filterName = "monitorPageFilter", urlPatterns = MonitorPageControlor.MONITOR_PAGE_CONTROLOR_URL + "/*")
public class MonitorPageFilter implements Filter {
	@Autowired
	private MonitorConfiguration monitorConfiguration;
	private FilterConfig filterConfig;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, filterConfig.getServletContext());
		
		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
		
		String loginPerson = ControlorUtil.getLoginPerson(httpServletRequest.getSession());
		if (loginPerson == null) {
			ControlorUtil.setInfo(httpServletRequest.getSession(), "请登录");
			httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/login");
			return;
		}
		httpServletRequest.setAttribute("token", monitorConfiguration.getToken());
		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}
	
	@Override
	public void destroy() {
	
	}
}

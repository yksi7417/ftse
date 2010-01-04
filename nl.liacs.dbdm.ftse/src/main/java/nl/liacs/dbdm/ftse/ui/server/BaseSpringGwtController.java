/*
 *
 * Created on Dec 27, 2009 | 3:28:18 PM
 *
 */
package nl.liacs.dbdm.ftse.ui.server;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class BaseSpringGwtController extends RemoteServiceServlet implements ApplicationContextAware,
		ServletContextAware, Controller {

	private static final long serialVersionUID = 1L;

	protected ApplicationContext applicationContext;
	protected ServletContext servletContext;

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		doPost(request, response);
		response.setHeader("Expires", "0");
		response.setHeader("Pragma", "cache");
		response.setHeader("Cache-Control", "private");
		return null;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	public ServletContext getServletContext() {
		return this.servletContext;
	}

}

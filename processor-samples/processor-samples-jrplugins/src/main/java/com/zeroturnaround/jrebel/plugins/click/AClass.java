package com.zeroturnaround.jrebel.plugins.click;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.click.ClickServlet;
import org.apache.click.ClickServlet_Mirror;
import org.apache.click.service.ConfigService;
import org.zeroturnaround.javassist.annotation.After;
import org.zeroturnaround.javassist.annotation.Before;
import org.zeroturnaround.javassist.annotation.Patches;

@Patches(ClickServlet.class)
public class AClass extends ClickServlet_Mirror {
	
	private void rebuildConfigService() {
		if (true)
			throw new RuntimeException("fubar!");
		ServletContext sc = getServletContext();
		configService = createConfigService(sc);
		initConfigService(sc);
	}

	@Before
	public void handleRequest(HttpServletRequest $1, HttpServletResponse $2, boolean $3) {
		rebuildConfigService();
		super.handleRequest($1, $2, $3); // -> copy, original calls companion, companion calls copy.
		init();
	}

	
	@Before
	public void init() {
		System.out.println("Calling init!");
		try {
			super.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		throw new RuntimeException("fubar2!");
		System.out.println("Finished calling init!");
	}
	
	
//
//	/* hmm.. should still do around? */
//	
//	@Override
//	public ConfigService createConfigService(ServletContext $1) {
//		doSomethingBefore();
//		// TODO Auto-generated method stub
//		ConfigService service = super.createConfigService($1);
//		
//		return doSomethingAfter(service);
//	}
//
//	private void doSomethingBefore() {
//		
//	}
//	
//	private ConfigService doSomethingAfter(ConfigService initial) {
//		return initial;
//	}
	
}

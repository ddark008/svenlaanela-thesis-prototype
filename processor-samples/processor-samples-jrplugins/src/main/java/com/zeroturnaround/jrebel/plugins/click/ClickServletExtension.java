package com.zeroturnaround.jrebel.plugins.click;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.click.ClickServlet;
import org.apache.click.ClickServlet_Mirror;
import org.zeroturnaround.javassist.annotation.Patches;

@Patches(ClickServlet.class)
public class ClickServletExtension extends ClickServlet_Mirror {
	
	private void rebuildConfigService() {
		ServletContext sc = getServletContext();
		configService = createConfigService(sc);
		initConfigService(sc);
	}

	@Override
	public void handleRequest(HttpServletRequest $1, HttpServletResponse $2, boolean $3) {
		rebuildConfigService();
		super.handleRequest($1, $2, $3); // -> copy, original calls companion, companion calls copy.
	}
}

package com.zeroturnaround.jrebel.plugins.click;

import org.apache.click.ClickServlet;
import org.apache.click.ClickServlet_Mirror;
import org.zeroturnaround.javassist.annotation.Patches;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Patches(ClickServlet.class)
public class ClickServletExtension extends ClickServlet_Mirror {
	
	private void rebuildConfigService() {
		ServletContext sc = getServletContext();
		try {
			configService = createConfigService(sc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		initConfigService(sc);
	}

	@Override
	public void handleRequest(HttpServletRequest $1, HttpServletResponse $2, boolean $3) {
		rebuildConfigService();
		try {
			super.handleRequest($1, $2, $3);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

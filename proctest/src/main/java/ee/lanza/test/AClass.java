package ee.lanza.test;

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
//		System.out.println("Rebuilding config service!");
		ServletContext sc = getServletContext();
		configService = createConfigService(sc);
		initConfigService(sc);
	}
	
	@Before
	public void init() {
		throw new RuntimeException("fubar2!");
	}

	/* hmm.. should still do around? */
	@Before
	public void handleRequest(HttpServletRequest $1, HttpServletResponse $2, boolean $3) {
		rebuildConfigService();
		// return explicit
		// return super.handleRequest?
		super.handleRequest($1, $2, $3);
	}
	
	@Override
	public ConfigService createConfigService(ServletContext $1) {
		doSomethingBefore();
		// TODO Auto-generated method stub
		ConfigService service = super.createConfigService($1);
		
		return doSomethingAfter(service);
	}

	private void doSomethingBefore() {
		
	}
	
	private ConfigService doSomethingAfter(ConfigService initial) {
		return initial;
	}
	
}

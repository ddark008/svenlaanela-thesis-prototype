package com.zeroturnaround.jrebel.plugins.wicket;

import java.util.Locale;

import org.apache.wicket.core.util.resource.locator.IResourceStreamLocator;
import org.apache.wicket.core.util.resource.locator.caching.CachingResourceStreamLocator;
import org.apache.wicket.core.util.resource.locator.caching.CachingResourceStreamLocator_Mirror;
import org.apache.wicket.util.resource.IResourceStream;
import org.zeroturnaround.javassist.annotation.Patches;

@Patches(CachingResourceStreamLocator.class)
public class CachingResourceStreamLocatorCBP extends CachingResourceStreamLocator_Mirror {
	
	public CachingResourceStreamLocatorCBP(IResourceStreamLocator $1) {
		super($1);
	}

	public void _jr_clearCache() {
		cache.clear();
	}

	@Override
	public IResourceStream locate(Class $1, String $2) {
//		StopWatch sw = LoggerFactory.getLogger("Wicket").createStopWatch("WicketLocate");
		IResourceStream result = super.locate($1, $2);
//		sw.stop();
		return result;
	}

	@Override
	public IResourceStream locate(Class $1, String $2, String $3, String $4, Locale $5, String $6, boolean $7) {
//		StopWatch sw = LoggerFactory.getLogger("Wicket").createStopWatch("WicketLocate");
		IResourceStream result = super.locate($1, $2, $3, $4, $5, $6, $7);
//		sw.stop();
		return result;
	}
	
	

}

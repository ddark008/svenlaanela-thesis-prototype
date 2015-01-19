package com.zeroturnaround.jrebel.plugins.click;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.WeakHashMap;
import javax.servlet.ServletContext;

import org.zeroturnaround.javarebel.Logger;
import org.zeroturnaround.javarebel.LoggerFactory;
import org.zeroturnaround.javarebel.Resource;
import org.zeroturnaround.javarebel.integration.monitor.MonitoredResource;
import org.zeroturnaround.javarebel.support.URLResource;


/**
 * Class that handles the monitoring of timestamps of the menu.xml and decides when 
 * a reload should happen.
 * 
 * @author Sander S6najalg (sander at zeroturnaround com) 
 */
public class ClickHelper {

  // WeakHashMap<ServletContext, String>
  private static Map instanceMap = new WeakHashMap(); 
  
  // Instance fields
  private MonitoredResource monitoredResource;
  
  
  public static boolean needsRefresh(ServletContext servletContext, ClassLoader webappClassLoader,
      Class menuFactoryClass, String fileName)
  {

    // We already have a cached instance of ClickHelper.. use it
    if (instanceMap.keySet().contains(servletContext)) {
      ClickHelper helperInstance = (ClickHelper) instanceMap.get(servletContext); 
      return helperInstance.checkIfRefreshRequired();
      
    // First request for this ServletContext.. create new ClickHelper and store it
    }
    else {
      ClickHelper helperInstance = new ClickHelper(servletContext, webappClassLoader, menuFactoryClass, fileName);
      instanceMap.put(servletContext, helperInstance);
      
      // It was the first request.. a new menu is being built anyway
      return false;
    }
  }
  
  
  
  /**
   * Create new instance of ClickHelper.. find the menu.xml URL and create a monitoredResource for it
   */
  public ClickHelper(ServletContext servletContext, ClassLoader webappClassLoader, Class menuFactoryClass, String fileName) {

    URL resourceUrl = findMenuXmlUrl(servletContext, webappClassLoader, menuFactoryClass, fileName);
    
    if (resourceUrl != null) {
//      Resource r = new URLResource(resourceUrl);
//      monitoredResource = new MonitoredResource(r);
    }
  }

  
  /**
   * Is the underlying menu.xml changed? If so, force menu rebuilding.
   */
  private boolean checkIfRefreshRequired() {
    // If the menu.xml wasn't found at all... let's not just do anything for safety. no reloading. 
    if (monitoredResource == null) {
      return false;
            
    }
    else {
      boolean hasChanged = monitoredResource.modified();
      
      if (hasChanged) {
//        log.info("menu.xml has changed -- emptying menu cache!");
      }
      return hasChanged;
    }
  }  

  

  
  private static URL findMenuXmlUrl(ServletContext servletContext, ClassLoader webappClassLoader, Class menuFactoryClass, String fileName) {
  
    // First Find out where the menu.xml actually is
    String webinfFileName = null;
    boolean absolute = fileName.startsWith("/");
    if (!absolute) {
        fileName = '/' + fileName;
        webinfFileName = "/WEB-INF" + fileName;
    }
    InputStream inputStream = null;

    
    boolean usedWebinfFileName = false;
    
    if (absolute) {
        inputStream = servletContext.getResourceAsStream(fileName);

        
    }
    else {
        inputStream = servletContext.getResourceAsStream(webinfFileName);
        
        if (inputStream != null) {
          usedWebinfFileName = true;
        }
    }
    
    // Two more tries ..
    if (inputStream == null) {
      inputStream = webappClassLoader.getResourceAsStream(fileName);
    }
    
    if (inputStream == null) {
      inputStream = menuFactoryClass.getResourceAsStream(fileName);
    }

    
    // Found the path for menu.xml .. no turn it into an URL 
    String usedResourcePath = (usedWebinfFileName ? webinfFileName : fileName);
    
    try {
      return servletContext.getResource(usedResourcePath);
      
    }
    catch (Exception e) {
      // Whatever happened, just return null and therefore turn off any further interception by the plugin
      return null;
    }
  }
}
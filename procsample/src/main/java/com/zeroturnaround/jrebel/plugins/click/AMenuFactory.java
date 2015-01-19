//package com.zeroturnaround.jrebel.plugins.click;
//
//import org.apache.click.Context;
//import org.apache.click.extras.control.Menu;
//import org.apache.click.extras.control.MenuFactory;
//import org.apache.click.extras.security.AccessController;
//import org.zeroturnaround.javassist.annotation.Patches;
//
//import javax.servlet.ServletContext;
//
///**
// * Created by lanza on 01/07/14.
// */
//@Patches(MenuFactory.class)
//public class AMenuFactory {
//  private Map
//
//  public Menu getRootMenu(String name, String fileName, AccessController accessController, boolean cached, Class<? extends Menu> menuClass) {
//    ServletContext servletContext = Context.getThreadLocalContext().getServletContext();
//    ClassLoader classLoader = MenuFactory.class.getClassLoader();
//
//    Class factoryClass = MenuFactory.class;
//      if (ClickHelper.needsRefresh(servletContext, classLoader, factoryClass, fileName)) {
//        // invalidate the old menu entry
//        if (menuCache != null) {
//          menuCache.put(name, null);
//        }
//      }
//
//      menuCache = new java.util.HashMap();
//  }
//
//}

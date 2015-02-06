//package com.zeroturnaround.jrebel.plugins.click;
//
////import com.sun.corba.se.impl.orbutil.ObjectWriter;
////import com.sun.org.glassfish.gmbal.DescriptorFields;
//import org.apache.click.ClickServlet;
//import org.apache.click.service.ConfigService;
//import org.zeroturnaround.javassist.annotation.*;
//
//import javax.servlet.ServletContext;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
//* Defining 4 virtual methods is stupid. Can we somehow extend from proper class and overcome private visibility?
//*
// *
// * TODO: Maybe we dont need to extend from OriginalClassBase, maybe the annotation preprocessor can add this for us?
// *
//* Created by lanza on 01/07/14.
//*/
//@Patches(value=ClickServlet.class)//, version=Version.ALL)
////@Version(from="1.2.2_02b", to="1.3.4")
//public abstract class AClickServletCBP extends ClickServletBase implements CBP {
//
//  @Existing
//  private ConfigService configService;
//
//  private void rebuildConfigService() {
//    // logging ??
//    // LoggerFactory.getLogger(\"Click\").echo(\"Rebuilding application configuration ..\"); " +
//    ServletContext sc = getServletContext();
//    configService = createConfigService(sc);
//    initConfigService(sc);
//  }
//
////  @Existing
////  abstract ServletContext getServletContext();
////
//  /**
//   * How is this type safe if we have to define it?? (error during compile-time with custom tooling/compiler?)
//   */
//  @Existing
//  abstract ConfigService createConfigService(ServletContext ctx);
////
////  /**
////   * How is this type safe if we have to define it?? (error during compile-time with custom tooling/compiler?)
////   */
////  @Existing
////  abstract void initConfigService(ServletContext ctx);
////
//  @Before
//  private void handleRequest(HttpServletRequest req, HttpServletResponse resp, boolean yesno) {
//    rebuildConfigService();
//    proceed(req, resp, yesno);
//  }
//
//  /**
//   * public HttpServletRequest handleRequest() {
//   *   return aClickServletCBP.handleRequest(...);
//   *   ...
//   * }
//   *
//   *
//   * @param req
//   * @param resp
//   * @param yesno
//   */
//  @Before
//  private HttpServletRequest handleRequest(HttpServletRequest req, HttpServletResponse resp, boolean yesno) {
//	  req.setAttribute("", "");
//	  return req;
//  }
//
//  /**
//   * public HttpServletRequest handleRequest(...) {
//   *   int myVar = 1;
//   *   ..
//   *
//   *   aClickServletCBP.handleRequest(..., myVar); // needs to handle returns
//   *  }
//   *
//   */
//
//  @After
//  private HttpServletRequest handleRequest(HttpServletRequest req, HttpServletResponse resp, boolean yesno, @LocalVars Object ... vars) {
//
//  }
//
//
//}

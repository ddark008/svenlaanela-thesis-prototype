package com.zeroturnaround.jrebel.plugins.scenarios;

import org.apache.click.ClickServlet;
import org.zeroturnaround.javassist.annotation.CBP;
import org.zeroturnaround.javassist.annotation.Method;
import org.zeroturnaround.javassist.annotation.Patches;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by lanza on 25/08/14.
 */
@Patches(ClickServlet.class)
public abstract class AfterMethodCBP implements CBP {

  public void myMethod(String s) {
    System.out.println("clearing cache for " + s);
  };

  @Method
  private void handleRequest(HttpServletRequest req, HttpServletResponse resp, boolean yesno) {
    String s = proceed(String.class, req, resp, yesno);
    myMethod(s);
  }

}

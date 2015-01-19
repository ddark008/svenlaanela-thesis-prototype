package com.zeroturnaround.jrebel.plugins.scenarios;

import org.zeroturnaround.javassist.annotation.CBP;
import org.zeroturnaround.javassist.annotation.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * Created by lanza on 25/08/14.
 */
public abstract class BeforeMethodCBP implements CBP {
  public String myMethod(String s) {
    return "clearing cache for " + s;
  };

  @Method
  private void handleRequest(HttpServletRequest req, HttpServletResponse resp, boolean yesno) {
    try {
      req.setCharacterEncoding(myMethod(req.getCharacterEncoding()));
    } catch (UnsupportedEncodingException e) {
      // wat
    }
    proceed(req, resp, yesno);
  }
}

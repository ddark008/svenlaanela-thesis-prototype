package sample;

import java.net.URL;
import java.net.URLClassLoader;

import javassist.CannotCompileException;
import javassist.CtConstructor;
import javassist.JavassistHelper;
import javassist.NotFoundException;

class MyParent {
  public MyParent(int i) {
    System.out.println("parent");
  }
}

public class BeforeSuperInConstr extends MyParent {

  public BeforeSuperInConstr() {
    this(5);
  }
  
  public BeforeSuperInConstr(int i) {
    super(call(i));
    System.out.println("after super");
  }

  public  static int call(int i) {
    System.out.println("inside call");
    return 1;
  }
  
  public static void main(String[] args) throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException {
    System.out.println("before");
    CtConstructor constructor = JavassistHelper.getConstructor(BeforeSuperInConstr.class, new Class[] {int.class});
    constructor.insertBefore("{$1 += 1; System.out.println(\"before super \" + $1);}");
    ClassLoader cl = new URLClassLoader(new URL[0]);
    JavassistHelper.getClass(MyParent.class).toClass(cl);
    JavassistHelper.getClass(BeforeSuperInConstr.class).toClass(cl).newInstance();
    System.out.println("after");
  }
}

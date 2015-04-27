package org.zeroturnaround.javassist.annotation.processor.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class IOUtil {

  public static void closeQuietly(Reader s) {
    if (s != null) try { s.close(); } catch (Exception ignored) {}
  }
  
  public static void closeQuietly(InputStream s) {
    if (s != null) try { s.close(); } catch (Exception ignored) {}
  }
  
  public static void closeQuietly(Writer s) {
    if (s != null) try { s.close(); } catch (Exception ignored) {}
  }
  
  public static void closeQuietly(OutputStream s) {
    if (s != null) try { s.close(); } catch (Exception ignored) {}
  }
  
}

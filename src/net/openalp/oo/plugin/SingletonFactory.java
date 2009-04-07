/**
 * @author Marcin Miłkowski
 *
 * This class is a factory that creates only a single instance,
 * or a singleton, of the Main class. Used for performance
 * reasons and to allow various parts of code to interact.
 *
 */

package net.openalp.oo.plugin;

import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.uno.XComponentContext;


public class SingletonFactory implements XSingleComponentFactory {

  private transient Plugin instance;

  public synchronized final Object createInstanceWithArgumentsAndContext(final Object[] arguments,
      final XComponentContext xContext) throws com.sun.star.uno.Exception {
    return createInstanceWithContext(xContext);
  }

  public synchronized final Object createInstanceWithContext(final XComponentContext xContext) throws com.sun.star.uno.Exception {
    if (instance == null) {
      instance = new Plugin(xContext);
    } else {
      instance.changeContext(xContext);
    }
    return instance;
  }
}
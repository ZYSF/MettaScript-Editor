/* No copyright, no warranty, only code. 
 * This file was created on 23 Nov 2014. It was a good day.
 */
package org.mettascript.library;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.mettascript.vm.ClosureValue;

/**
 *
 * @author Zak Fenton
 */
public class BindingContext {

	private ClosureValue closure;
	
	private HashMap<Class<? extends NativeLibrary>, NativeLibrary> libraries =
			new HashMap<Class<? extends NativeLibrary>, NativeLibrary>();
	
	public BindingContext(ClosureValue closure) {
		this.closure = closure;
	}
	
	public <T extends NativeLibrary> T load(Class<T> nativeLibraryClass, boolean global) {
		if (libraries.containsKey(nativeLibraryClass)) {
			return (T)libraries.get(nativeLibraryClass);
		}
		
		T result;
		
		try {
			result = (T)nativeLibraryClass.getConstructor(BindingContext.class).newInstance(this);
			result.initialise(global);
		} catch (Exception e) {
			throw new Error("Well that was unexpected!", e);
		}
		
		libraries.put(nativeLibraryClass, result);
		
		return result;
	}

	public ClosureValue getClosure() {
		return closure;
	}
}

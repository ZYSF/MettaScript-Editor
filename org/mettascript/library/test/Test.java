/* No copyright, no warranty, only code. 
 * This file was created on 30 Nov 2014. It was a good day.
 */
package org.mettascript.library.test;

import org.mettascript.library.NativeLibrary;
import org.mettascript.library.NativeObject;
import org.mettascript.runtime.Value;
import org.mettascript.runtime.Values;

/**
 *
 * @author Zak Fenton
 */
public class Test extends NativeObject {
	private String description;
	private Value testFunction;
	private Value desiredValue;

	public Test(NativeLibrary libraryInstance, Value constructorParameters) {
		super(libraryInstance, constructorParameters);
		
		
	}

}

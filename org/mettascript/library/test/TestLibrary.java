/* No copyright, no warranty, only code. 
 * This file was created on 30 Nov 2014. It was a good day.
 */
package org.mettascript.library.test;

import org.mettascript.library.BindingContext;
import org.mettascript.library.NativeLibrary;

/**
 *
 * @author Zak Fenton
 */
public class TestLibrary extends NativeLibrary {

	public TestLibrary(BindingContext bindingContext) {
		super(bindingContext);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void bindAll() {
		
	}

	@Override
	public String[] getNames() {
		return new String[]{"TestLibrary"};
	}

}

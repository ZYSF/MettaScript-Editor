/* No copyright, no warranty, only code. 
 * This file was created on 23 Nov 2014. It was a good day.
 */
package org.mettascript.editor.swing.backend.canvas;

import org.mettascript.library.BindingContext;
import org.mettascript.library.NativeLibrary;
import org.mettascript.runtime.Value;

/**
 *
 * @author Zak Fenton
 */
public class CanvasLibrary extends NativeLibrary {
	
	public static final int CanvasAPIVersion = 1;

	public CanvasLibrary(BindingContext bindingContext) {
		super(bindingContext);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void bindAll() {
		bind("CanvasAPIVersion").to(Value.of(CanvasAPIVersion));
		bind(Colour.class);
	}

	@Override
	public String[] getNames() {
		// TODO Auto-generated method stub
		return null;
	}

}

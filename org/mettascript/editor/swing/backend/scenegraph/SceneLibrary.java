/* No copyright, no warranty, only code. 
 * This file was created on 23 Nov 2014. It was a good day.
 */
package org.mettascript.editor.swing.backend.scenegraph;

import org.mettascript.library.BindingContext;
import org.mettascript.library.NativeLibrary;
import org.mettascript.runtime.Value;
import org.mettascript.vm.ClosureValue;

/**
 *
 * @author Zak Fenton
 */
public class SceneLibrary extends NativeLibrary {
	
	public static final int API_VERSION = 1;

	public SceneLibrary(BindingContext bindingContext) {
		super(bindingContext);
	}

	@Override
	protected void bindAll() {
		bind("SceneAPIVersion").to(Value.of(API_VERSION));
		bind(Element.class);
		bind(Scene.class);
	}

	@Override
	public String[] getNames() {
		return new String[]{"standard scene"};
	}
}

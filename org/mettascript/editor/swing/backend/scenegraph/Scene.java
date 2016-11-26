/* No copyright, no warranty, only code. 
 * This file was created on 23 Nov 2014. It was a good day.
 */
package org.mettascript.editor.swing.backend.scenegraph;

import org.mettascript.runtime.*;
import org.mettascript.library.*;

/**
 *
 * @author Zak Fenton
 */
@Exposed("Scene")
public class Scene extends Element {
	
	@Exposed
	public Scene(NativeLibrary library, Value constructorParameters) {
		super(library, constructorParameters);
	}


}

/* No copyright, no warranty, only code. 
 * This file was created on 23 Nov 2014. It was a good day.
 */
package org.mettascript.editor.swing.backend.canvas;

import org.mettascript.library.*;
import org.mettascript.runtime.Value;


/**
 *
 * @author Zak Fenton
 */
@Exposed
public class Colour extends NativeObject {
	private int rgb;
	
	@Exposed
	public Colour(NativeLibrary libraryInstance, Value parameters) {
		super(libraryInstance, parameters);
	}
	
	public Colour(NativeLibrary libraryInstance, int rgb) {
		super(libraryInstance, NOTHING);
		this.rgb = rgb;
	}
}

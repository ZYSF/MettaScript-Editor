/* No copyright, no warranty, only code. 
 * This file was created on 23 Nov 2014. It was a good day.
 */
package org.mettascript.editor.swing.backend.presentation;

import org.mettascript.editor.swing.backend.scenegraph.Element;
import org.mettascript.editor.swing.backend.scenegraph.Scene;
import org.mettascript.library.Exposed;
import org.mettascript.library.NativeLibrary;
import org.mettascript.runtime.TextValue;
import org.mettascript.runtime.Value;

/**
 *
 * @author Zak Fenton
 */
@Exposed
public class Presentation extends Scene {

	Element title = toElement(new TextValue("No title."));
	
	@Override
	protected void initialiseProperty(String binaryName, Value property) {
		switch (binaryName) {
		case "title":
		case "Title":
			initialiseTitle(toElement(property));
			break;
		default:
			super.initialiseProperty(binaryName, property);
		}
	}

	@Exposed
	public Presentation(NativeLibrary library, Value parameters) {
		super(library, parameters);
	}

	protected void initialiseTitle(Element title) {
		this.title = title;
	}
}

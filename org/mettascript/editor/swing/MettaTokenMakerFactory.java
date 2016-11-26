/* No copyright, no warranty, only code. 
 * This file was created on 9 Nov 2014. It was a good day.
 */
package org.mettascript.editor.swing;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenMaker;

/**
 *
 * @author Zak Fenton
 */
public class MettaTokenMakerFactory extends AbstractTokenMakerFactory {

	public MettaTokenMakerFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initTokenMakerMap() {
		putMapping("text/mettascript", "org.mettascript.editor.swing.MettaTokenMaker");
	}

	@Override
	protected TokenMaker getTokenMakerImpl(String key) {
		System.out.println("Syntax hilighting key is '" + key + "'");
		return super.getTokenMakerImpl(key);
	}
	
	

}

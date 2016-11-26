/* No copyright, no warranty, only code. 
 * This file was created on 19 Nov 2014. It was a good day.
 */
package org.mettascript.editor.swing;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Zak Fenton
 */
public class FormulaFileFilter extends FileFilter {

	public FormulaFileFilter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean accept(File file) {
		if (file.isDirectory() || file.getName().endsWith(".metta")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getDescription() {
		return "MettaScript Formulae";
	}

}

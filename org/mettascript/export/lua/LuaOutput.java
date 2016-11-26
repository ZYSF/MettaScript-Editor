/* No copyright, no warranty, only code. 
 * This file was created on 03/11/2014. It was a good day.
 */
package org.mettascript.export.lua;

import java.io.PrintStream;

import org.mettascript.export.TextOutput;
import org.mettascript.parser.*;

/**
 *
 * @author Zak Fenton
 */
public class LuaOutput extends TextOutput {
	private final FormulaParser parser;
	
	public LuaOutput(FormulaParser parser) {
		this.parser = parser;
	}

	public LuaOutput(FormulaParser parser, PrintStream out) {
		super(out);
		this.parser = parser;
	}
	
	
}

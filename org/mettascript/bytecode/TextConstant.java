/* No copyright, no warranty, only code. 
 * This file was created on 15 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode;

import org.mettascript.parser.ValueSyntax;
import org.mettascript.runtime.*;

/**
 *
 * @author Zak Fenton
 */
public class TextConstant extends Constant {
	
	private String value;

	public TextConstant(BytecodeFile constantPool, String value) {
		super(constantPool);
		this.value = value;
	}
	
	@Override
	public Value getValue() {
		return new TextValue(value);
	}

	@Override
	public Type getType() {
		return Type.TEXT;
	}

	@Override
	public String getValueString() {
		return ValueSyntax.getSourceForString(value);
	}
	
	public String getString() {
		return value;
	}
}

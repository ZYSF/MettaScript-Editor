/* No copyright, no warranty, only code. 
 * This file was created on 15 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode;

import java.math.BigInteger;

import org.mettascript.runtime.*;

/**
 *
 * @author Zak Fenton
 */
public class IntegerConstant extends Constant {

	private BigInteger value;
	
	private boolean fitsIn32Bits;
	
	private int intValue;
	
	private TextConstant textValue = null;
	
	IntegerConstant(BytecodeFile pool, BigInteger value) {
		super(pool);
		this.value = value;
		intValue = value.intValue();
		String stringValue = value.toString();
		fitsIn32Bits = stringValue.equals(Integer.toString(intValue));
		if (!fitsIn32Bits) {
			textValue = pool.getText(stringValue);
		}
	}
	
	@Override
	public Value getValue() {
		return new IntegerValue(value);
	}
	
	public boolean fitsIn32Bits() {
		return fitsIn32Bits;
	}

	@Override
	public Type getType() {
		return Type.INTEGER;
	}
	
	@Override
	public String getValueString() {
		return value.toString();
	}
	
	public int get32BitValue() {
		return intValue;
	}

	public TextConstant getAssociatedText() {
		return textValue;
	}
}

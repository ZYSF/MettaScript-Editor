/* No copyright, no warranty, only code. 
 * This file was created on 15 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode.instructions;

import java.math.BigInteger;

import org.mettascript.bytecode.CompilationException;

/**
 *
 * @author Zak Fenton
 */
public class PushIntegerInstruction extends Instruction {

	public static final int VALUE_MAX = 0x7FF;
	public static final int VALUE_MIN = -0x800;
	public static final BigInteger BIG_VALUE_MAX = new BigInteger(Integer.toString(VALUE_MAX));
	public static final BigInteger BIG_VALUE_MIN = new BigInteger(Integer.toString(VALUE_MIN));
	
	private int value;
	
	public PushIntegerInstruction(int value) throws CompilationException {
		if (value > VALUE_MAX) {
			throw new CompilationException("The value " + value + " is more than VALUE_MAX (" + VALUE_MAX + "!");
		} else if (value < VALUE_MIN) {
			throw new CompilationException("The value " + value + " is less than VALUE_MIN (" + VALUE_MIN + "!");
		}
		
		this.value = value;
	}
	
	@Override
	public int getStackImbalance() {
		return 1;
	}

	@Override
	public String getParameterString(boolean jsonFormat) {
		if (jsonFormat) {
			return ", " + value;
		} else {
			return Integer.toString(value);
		}
	}
	
	@Override
	public Type getType() {
		return Type.PUSH_INTEGER;
	}
	
	@Override
	public int encode() {
		return super.encode() | (value << 4);
	}
	
	public int getInteger() {
		return value;
	}
	
	public static boolean isWithinBounds(BigInteger value) {
		if (value.compareTo(BIG_VALUE_MAX) > 0 || value.compareTo(BIG_VALUE_MIN) < 0) {
			return false;
		} else {
			return true;
		}
	}
}

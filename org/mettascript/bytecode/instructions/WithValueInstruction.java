/* No copyright, no warranty, only code. 
 * This file was created on 22 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode.instructions;

/**
 *
 * @author Zak Fenton
 */
public class WithValueInstruction extends Instruction {
	
	private int numberOfValues;

	public WithValueInstruction(int numberOfValues) {
		this.numberOfValues = numberOfValues;
	}
	
	public WithValueInstruction() {
		this(0);
	}
	
	public int getNumberOfValues() {
		return numberOfValues;
	}

	@Override
	public Type getType() {
		return Type.WITH_VALUE;
	}
	
	@Override
	public int encode() {
		return super.encode() | (numberOfValues << 4);
	}

	@Override
	public String getParameterString(boolean jsonFormat) {
		return "";
	}

	@Override
	public int getStackImbalance() {
		return -1;
	}

}

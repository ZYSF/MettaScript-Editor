/* No copyright, no warranty, only code. 
 * This file was created on 15 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode.instructions;

/**
 *
 * @author Zak Fenton
 */
public class ReturnInstruction extends Instruction {
	private int numberOfValues;
	
	public ReturnInstruction(int numberOfReturnValues) {
		this.numberOfValues = numberOfReturnValues;
	}
	
	@Override
	public int getStackImbalance() {
		return -numberOfValues;
	}

	@Override
	public Type getType() {
		return Type.RETURN;
	}
	
	@Override
	public int encode() {
		return super.encode() | (numberOfValues << 4);
	}
	
	@Override
	public String getParameterString(boolean jsonValue) {
		if (jsonValue) {
			return ", " + numberOfValues;
		} else {
			return Integer.toString(numberOfValues);
		}
	}

	public int getNumberOfValues() {
		return numberOfValues;
	}
}

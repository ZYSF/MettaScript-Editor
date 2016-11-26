/* No copyright, no warranty, only code. 
 * This file was created on 15 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode.instructions;

/**
 *
 * @author Zak Fenton
 */
public class CombineInstruction extends Instruction {
	private int numberOfValues;
	
	public CombineInstruction(int numberOfValues) {
		this.numberOfValues = numberOfValues;
	}
	
	@Override
	public String getParameterString(boolean jsonFormat) {
		if (jsonFormat) {
			return ", " + numberOfValues;
		} else {
			return Integer.toString(numberOfValues);
		}
	}
	
	@Override
	public int getStackImbalance() {
		return numberOfValues - 1;
	}

	@Override
	public Type getType() {
		return Type.COMBINE;
	}
	
	@Override
	public int encode() {
		return super.encode() | (numberOfValues << 4);
	}
	
	public int getNumberOfValues() {
		return numberOfValues;
	}

}

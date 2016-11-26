/* No copyright, no warranty, only code. 
 * This file was created on 16 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode.instructions;

/**
 *
 * @author Zak Fenton
 */
public class CopyInstruction extends Instruction {

	int numberOfCopies;
	
	public CopyInstruction(int numberOfCopies) {
		this.numberOfCopies = numberOfCopies;
	}
	
	@Override
	public int getStackImbalance() {
		return numberOfCopies;
	}

	@Override
	public Type getType() {
		return Type.COPY;
	}
	
	@Override
	public int encode() {
		return super.encode() | (numberOfCopies << 4);
	}

	@Override
	public String getParameterString(boolean jsonFormat) {
		if (jsonFormat) {
			return ", " + numberOfCopies;
		} else {
			return Integer.toString(numberOfCopies);
		}
	}
	
	public int getNumberOfCopies() {
		return numberOfCopies;
	}
}

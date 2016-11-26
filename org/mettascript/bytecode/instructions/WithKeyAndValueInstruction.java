/* No copyright, no warranty, only code. 
 * This file was created on 22 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode.instructions;

import org.mettascript.bytecode.OperatorReference;

/**
 *
 * @author Zak Fenton
 */
public class WithKeyAndValueInstruction extends Instruction {
	
	private OperatorReference key;

	public WithKeyAndValueInstruction(OperatorReference key) {
		this.key = key;
	}

	@Override
	public Type getType() {
		return Type.WITH_KEY_AND_VALUE;
	}
	
	@Override
	public int encode() {
		return super.encode() | (key.getIndex() << 4);
	}

	@Override
	public String getParameterString(boolean jsonFormat) {
		if (jsonFormat) {
			return ", " + key.getIndex();
		} else {
			return key.getNameString();
		}
	}

	@Override
	public int getStackImbalance() {
		return -1;
	}
	
	public OperatorReference getKey() {
		return key;
	}

}

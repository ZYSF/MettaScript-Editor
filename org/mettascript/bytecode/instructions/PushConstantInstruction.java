/* No copyright, no warranty, only code. 
 * This file was created on 15 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode.instructions;

import org.mettascript.bytecode.Constant;

/**
 *
 * @author Zak Fenton
 */
public class PushConstantInstruction extends Instruction {
	private Constant constant;
	
	public PushConstantInstruction(Constant constant) {
		this.constant = constant;
	}
	
	@Override
	public int getStackImbalance() {
		return 1;
	}
	
	@Override
	public Type getType() {
		return Type.PUSH_CONSTANT;
	}
	
	@Override
	public int encode() {
		return super.encode() | (constant.getIndex() << 4);
	}

	@Override
	public String getParameterString(boolean jsonFormat) {
		if (jsonFormat) {
			return ", " + constant.getIndex();
		} else {
			return constant.toString();
		}
	}

	public Constant getConstant() {
		return constant;
	}
}

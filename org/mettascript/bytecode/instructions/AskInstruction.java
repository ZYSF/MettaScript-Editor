/* No copyright, no warranty, only code. 
 * This file was created on 15 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode.instructions;

import org.mettascript.bytecode.OperatorReference;

/**
 *
 * @author Zak Fenton
 */
public class AskInstruction extends Instruction {
	
	private boolean isTailCall = false;
	private OperatorReference operator;
	
	public AskInstruction(OperatorReference operator) {
		this.operator = operator;
	}
	@Override
	public int getStackImbalance() {
		return -1;
	}

	@Override
	public Type getType() {
		if (isTailCall) {
			return Type.ASK_TAIL;
		} else {
			return Type.ASK;
		}
	}

	@Override
	public String getParameterString(boolean jsonFormat) {
		if (jsonFormat) {
			return ", " + operator.getIndex();
		} else {
			return operator.toString();
		}
	}
	
	public String getOperatorString() {
		return operator.getNameString();
	}
	
	@Override
	public int encode() {
		return super.encode() | (operator.getIndex() << 4);
	}
	
	public boolean isTailCall() {
		return isTailCall;
	}
	
	public void setTailCall(boolean isTailCall) {
		this.isTailCall = isTailCall;
	}

}

/* No copyright, no warranty, only code. 
 * This file was created on 16 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode.instructions;

import org.mettascript.bytecode.Label;

/**
 *
 * @author Zak Fenton
 */
public class JumpIfInstruction extends Instruction {

	Label target;
	boolean valueToJumpOn;
	
	public JumpIfInstruction(Label target, boolean valueToJumpOn) {
		this.target = target;
		this.valueToJumpOn = valueToJumpOn;
	}
	
	@Override
	public int getStackImbalance() {
		return -1;
	}

	@Override
	public Type getType() {
		return Type.JUMP_IF;
	}
	
	@Override
	public int encode() {
		int i = getIndexWithoutCompression();
		int ti = target.getTargetInstruction().getIndexWithoutCompression();
		return super.encode() | ((ti - (i + 1)) << 5) | (valueToJumpOn ? 1 << 4 : 0);
	}

	@Override
	public String getParameterString(boolean jsonFormat) {
		if (jsonFormat) {
			return ", " + (valueToJumpOn ? "true" : "false") + ", " + target.getTargetInstruction().getIndexWithoutCompression();
		} else {
			return (valueToJumpOn ? "YES" : "NO") + ", " + target.toString();
		}
	}
	
	public Label getTarget() {
		return target;
	}

	public boolean getValueToJumpOn() {
		return valueToJumpOn;
	}
}

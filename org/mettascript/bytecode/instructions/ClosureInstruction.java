/* No copyright, no warranty, only code. 
 * This file was created on 16 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode.instructions;

import org.mettascript.bytecode.Block;

/**
 *
 * @author Zak Fenton
 */
public class ClosureInstruction extends Instruction {

	private Block block;
	
	public ClosureInstruction(Block block) {
		this.block = block;
	}
	
	@Override
	public int getStackImbalance() {
		return 1;
	}

	@Override
	public Type getType() {
		return Type.CLOSURE;
	}

	@Override
	public String getParameterString(boolean jsonFormat) {
		if (jsonFormat) {
			return ", " + block.getIndex();
		} else {
			return block.toString();
		}
	}
	
	@Override
	public int encode() {
		return super.encode() | (block.getIndex() << 4);
	}

	public Block getBlock() {
		return block;
	}
}

/* No copyright, no warranty, only code. 
 * This file was created on 15 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode.instructions;

import java.io.PrintStream;

import org.mettascript.bytecode.AbstractBytecodeObject;
import org.mettascript.bytecode.Block;
import org.mettascript.bytecode.Label;
import org.mettascript.parser.ValueSyntax;

/**
 *
 * @author Zak Fenton
 */
public abstract class Instruction extends AbstractBytecodeObject {

	public enum Type {
		ASK,
		ASK_TAIL,
		
		WITH_KEY_AND_VALUE,
		WITH_VALUE,
		
		GET,
		PUT,
		
		PUSH_CONSTANT,
		PUSH_INTEGER,
		PUSH_SPECIAL,
		
		POP,
		
		COMBINE,
		SPLIT,
		
		RETURN,
		
		CLOSURE,
		
		JUMP_IF,
		
		COPY
	}
	
	public Label label;
	
	Block enclosingBlock;

	public Instruction() {
		// TODO Auto-generated constructor stub
	}

	public void onInsert(Block enclosingBlock) {
		if (this.enclosingBlock != null) {
			throw new IllegalStateException("Enclosing block has already been set!");
		}
		this.enclosingBlock = enclosingBlock;
	}
	
	public abstract Type getType();
	
	public abstract String getParameterString(boolean jsonFormat);
	
	public abstract int getStackImbalance();
	
	public int getIndexWithoutCompression() {
		if (enclosingBlock == null) {
			throw new IllegalStateException("This instruction hasn't been inserted into a block yet!");
		}
		
		return enclosingBlock.getInstructions().indexOf(this);
	}
	
	/** Returns the assembly language definition of this instruction. */
	@Override
	public String toString() {
		return getType() + " " + getParameterString(false);
	}
	
	@Override
	public void print(PrintStream output, boolean jsonFormat) {
		if (jsonFormat) {
			output.print("        [" + getType().ordinal() + getParameterString(true) + "]");
		} else {
			if (label != null) {
				label.print(output, false);
			}
			output.println("    " + toString());
		}
	}
	
	public int encode() {
		return getType().ordinal();
	}
}

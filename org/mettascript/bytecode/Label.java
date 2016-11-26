/* No copyright, no warranty, only code. 
 * This file was created on 16 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode;

import java.io.PrintStream;

import org.mettascript.bytecode.instructions.Instruction;

/**
 *
 * @author Zak Fenton
 */
public class Label extends AbstractBytecodeObject {
	private Label synonymOf;
	private Block enclosingBlock;
	private Instruction target;
	private int abstractIdentifier;
	
	public Label(Block enclosingBlock) {
		this.enclosingBlock = enclosingBlock;
		abstractIdentifier = enclosingBlock.labels.size();
		enclosingBlock.labels.add(this);
	}
	
	public Label canonicalise() {
		if (synonymOf != null) {
			return synonymOf;
		} else {
			return this;
		}
	}
	
	public void setTargetInstruction(Instruction target) {
		if (this.target != null) {
			throw new IllegalStateException("The target of this label has already been set!");
		}
		this.target = target;
		if (target.label != null) {
			this.synonymOf = target.label;
		} else {
			target.label = this;
		}
	}

	@Override
	public void print(PrintStream output, boolean jsonFormat) {
		Label l = canonicalise();
		if (jsonFormat) {
			output.println("({[ NO JSON REPRESENTATION: ({[");
		}
		output.println("  " + toString() + ":");
	}

	@Override
	public String toString() {
		return "LABEL_" + abstractIdentifier;
	}

	public Instruction getTargetInstruction() {
		return target;
	}
}

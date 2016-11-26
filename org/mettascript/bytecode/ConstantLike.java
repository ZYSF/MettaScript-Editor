/* No copyright, no warranty, only code. 
 * This file was created on 15 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode;

/**
 *
 * @author Zak Fenton
 */
public abstract class ConstantLike extends AbstractBytecodeObject {
	private final BytecodeFile bytecodeFile;
	
	int index;
	
	ConstantLike(BytecodeFile bytecodeFile) {
		this.bytecodeFile = bytecodeFile;
		bytecodeFile.invalidateIndices();
	}

	public BytecodeFile getBytecodeFile() {
		return bytecodeFile;
	}
	
	public int getIndex() {
		bytecodeFile.calculateIndices();
		return index;
	}
}

/* No copyright, no warranty, only code. 
 * This file was created on 16 Nov 2014. It was a good day.
 */
package org.mettascript.vm;

import org.mettascript.runtime.*;

/**
 *
 * @author Zak Fenton
 */
public class BytecodeCallFrame extends CallFrame {

	public ClosureValue closure;
	Value[] stack;
	
	public int stackPointer;
	public int instructionPointer;
	
	public Value leftHandSide = Value.NOTHING;
	public String operator = "";
	public Value rightHandSide = Value.NOTHING;
	
	BytecodeCallFrame(ClosureValue closure) {
		setClosure(closure);
	}
	
	void setClosure(ClosureValue closure) {
		this.closure = closure;
		instructionPointer = 0;
		stack = new Value[closure.block.getStackSize()];
		stackPointer = closure.block.getReservedSlots().length;
		
		for (int i = 0; i < stackPointer; i++) {
			stack[i] = new Reference();
		}
	}

	void push(Value value) {
		//System.out.println("Pushing value (" + value + ") at position #" + stackPointer);
		stack[stackPointer++] = value;
	}
	
	Value pop() {
		//System.out.println("Popping value at position #" + (stackPointer - 1));
		return stack[--stackPointer];
	}
}

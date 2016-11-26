/* No copyright, no warranty, only code. 
 * This file was created on 16 Nov 2014. It was a good day.
 */
package org.mettascript.vm;

import java.util.Stack;

/**
 *
 * @author Zak Fenton
 */
public class Context {
	
	long iterationCount = 0;
	
	long maximumIterationCount = 100000000;
	
	public Stack<CallFrame> stack = new Stack<>();
	
	private static final ThreadLocal<Context> threadLocal = new ThreadLocal<Context>() {
		@Override
		public Context initialValue() {
			return new Context();
		}
	};

	public static Context getInstance() {
		return threadLocal.get();
	}
	
	public static void resetInstance() {
		threadLocal.remove();
	}

	public BytecodeCallFrame newBytecodeCallFrame(ClosureValue closureValue) {
		BytecodeCallFrame result = new BytecodeCallFrame(closureValue);
		stack.push(result);
		return result;
	}
	
	public void doDebugChecks() {
		iterationCount++;
		if (iterationCount > maximumIterationCount) {
			iterationCount = 0;
			throw new Error("Ran too long. Mercilessly terminated.");
		}
	}
}

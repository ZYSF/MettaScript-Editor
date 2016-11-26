/* No copyright, no warranty, only code. 
 * This file was created on 15 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode;

import org.mettascript.parser.Operation;

/**
 *
 * @author Zak Fenton
 */
public class CompilationException extends Exception {
	
	private Operation operation;

	public CompilationException() {
	}
	
	public CompilationException(Operation operation, String message) {
		super("Around " + operation.token + ": " + message);
		this.operation = operation;
	}

	public CompilationException(String message) {
		super(message);
	}

	public CompilationException(Throwable cause) {
		super(cause);
	}

	public CompilationException(String message, Throwable cause) {
		super(message, cause);
	}

	public CompilationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public Operation getOperation() {
		return operation;
	}

}

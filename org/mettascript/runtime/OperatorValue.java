/* No copyright, no warranty, only code. 
 * This file was created on 22 Nov 2014. It was a good day.
 */
package org.mettascript.runtime;

/**
 *
 * @author Zak Fenton
 */
public class OperatorValue extends Value {
	private String binaryName;
	
	public OperatorValue(String binaryName) {
		this.binaryName = binaryName;
	}
	
	public String getName(String locale) {
		return binaryName;
	}

	@Override
	public String toString() {
		return binaryName;
	}
}

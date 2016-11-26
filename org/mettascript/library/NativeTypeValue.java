/* No copyright, no warranty, only code. 
 * This file was created on 23 Nov 2014. It was a good day.
 */
package org.mettascript.library;

import java.util.Collection;

import org.mettascript.runtime.Value;

/**
 *
 * @author Zak Fenton
 */
public class NativeTypeValue extends TypeValue {
	
	private final NativeLibrary library;
	private final NativeTypeMapping mapping;

	NativeTypeValue(NativeLibrary library, NativeTypeMapping mapping) {
		this.library = library;
		this.mapping = mapping;
	}

	public Collection<String> getNames() {
		return mapping.getNames();
	}

	@Override
	public Value _invoke(Value leftHandSide, String op, Value rightHandSide) {
		if (op.equals("@")) {
			return mapping.instantiate(library, rightHandSide);
		}
		return super._invoke(leftHandSide, op, rightHandSide);
	}

}

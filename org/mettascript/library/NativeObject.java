/* No copyright, no warranty, only code. 
 * This file was created on 23 Nov 2014. It was a good day.
 */
package org.mettascript.library;

import org.mettascript.runtime.Value;

/**
 *
 * @author Zak Fenton
 */
public abstract class NativeObject extends Value {

	private final NativeLibrary libraryInstance;
	
	private final NativeTypeMapping typeMapping;
	
	public NativeObject(NativeLibrary libraryInstance, Value constructorParameters) {
		this.libraryInstance = libraryInstance;
		typeMapping = NativeTypeMapping.of(this.getClass());
	}

	@Override
	public Value _invoke(Value leftHandSide, String op, Value rightHandSide) {
		return super._invoke(leftHandSide, op, rightHandSide);
	}
	
	public final NativeLibrary getLibrary() {
		return libraryInstance;
	}
	
	public final NativeTypeMapping getTypeMapping() {
		return typeMapping;
	}
}

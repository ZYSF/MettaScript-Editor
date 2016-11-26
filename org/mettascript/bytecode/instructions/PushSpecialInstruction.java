/* No copyright, no warranty, only code. 
 * This file was created on 15 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode.instructions;

import org.mettascript.parser.ValueSyntax;

/**
 *
 * @author Zak Fenton
 */
public class PushSpecialInstruction extends Instruction {
	
	public enum ValueType {
		YES,
		NO,
		NOTHING,
		LEFT,
		OPERATOR,
		RIGHT, 
		EMPTY
	};
	
	private ValueType valueType;
	private int numberOfCopies;

	public PushSpecialInstruction(int numberOfNothings) {
		valueType = ValueType.NOTHING;
		numberOfCopies = numberOfNothings;
	}
	
	public PushSpecialInstruction(ValueType valueType, int numberOfCopies) {
		this.valueType = valueType;
		this.numberOfCopies = numberOfCopies;
	}
	
	public PushSpecialInstruction(ValueType valueType) {
		this(valueType, 1);
	}
	
	@Override
	public int getStackImbalance() {
		return 1;
	}
	
	@Override
	public String getParameterString(boolean jsonFormat) {
		if (jsonFormat) {
			return ", " + valueType.ordinal() + ", " + numberOfCopies;
		} else {
			return valueType.toString() + ", " + numberOfCopies;
		}
	}

	@Override
	public Type getType() {
		return Type.PUSH_SPECIAL;
	}
	
	@Override
	public int encode() {
		return super.encode() | (numberOfCopies << 7) | (valueType.ordinal() << 4);
	}

	public ValueType getValueType() {
		return valueType;
	}
	
	public int getNumberOfCopies() {
		return numberOfCopies;
	}
}

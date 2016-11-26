/* No copyright, no warranty, only code. 
 * This file was created on 4 Nov 2014. It was a good day.
 */
package org.mettascript.runtime;

/**
 *
 * @author Zak Fenton
 */
public class Unknown extends Value {
	
	public final Value handler;
	public final Value leftHandSide;
	public final String operator;
	public final Value rightHandSide;

	public Unknown(String operator) {
		this(NOTHING, NOTHING, operator, NOTHING);
	}
	
	public Unknown(Value handler, Value leftHandSide, String operator, Value rightHandSide) {
		this.handler = handler;
		this.leftHandSide = leftHandSide;
		this.operator = operator;
		this.rightHandSide = rightHandSide;
	}

	@Override
	public String toString() {
		if (handler == NOTHING && leftHandSide == NOTHING && rightHandSide == NOTHING) {
			return "Unknown(" + operator + ")";
		} else if (handler.equals(leftHandSide)) {
			return "Unknown(" + leftHandSide + " " + operator + " " + rightHandSide + ")";
		} else {
			return "Unknown{handler=" + handler
					+ ", leftHandSide=" + leftHandSide
					+ ", operator='" + operator + "'"
					+ ", rightHandSide=" + rightHandSide + "}";
		}
	}

	@Override
	public Value _invoke(Value leftHandSide, String op, Value rightHandSide) {
		if (op.equals("isUnknown")) {
			return Value.YES;
		} else if (op.equals("unknownOperator")) {
			return new OperatorValue(this.operator);
		} else if (op.equals("unknownLeftHandSide")) {
			return this.leftHandSide;
		} else if (op.equals("unknownRightHandSide")) {
			return this.rightHandSide;
		} else {
			return super._invoke(leftHandSide, op, rightHandSide);
		}
	}
}

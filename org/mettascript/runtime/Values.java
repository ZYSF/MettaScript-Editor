/* No copyright, no warranty, only code. 
 * This file was created on 8 Nov 2014. It was a good day.
 */
package org.mettascript.runtime;

/**
 *
 * @author Zak Fenton
 */
public class Values extends Value {

	private final Value[] values;
	
	public Values(Value... values) {
		this.values = values;
	}

	@Override
	public Value _invoke(Value leftHandSide, String operator, Value rightHandSide) {
		if (operator.equals("@")) {
			if (rightHandSide instanceof IntegerValue) {
				int i = ((IntegerValue)rightHandSide).integer.intValue();
				if (i >= 1 && i <= values.length) {
					return values[i-1];
				} else {
					return NOTHING;
				}
			}
		} else if (operator.equals("count")) {
			if (rightHandSide == NOTHING) {
				return new IntegerValue(values.length);
			}
		}
		
		return super._invoke(leftHandSide, operator, rightHandSide);
	}
	
	@Override
	public String toString() {
		String result = "Values(";
		
		for (int i = 0; i < values.length; i++) {
			if (i > 0) {
				result += ", ";
			}
			result += values[i].toString();
		}
		
		return result + ")";
	}
}

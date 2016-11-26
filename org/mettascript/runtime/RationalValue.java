/* No copyright, no warranty, only code. 
 * This file was created on 11 Nov 2014. It was a good day.
 */
package org.mettascript.runtime;

import java.math.BigInteger;

/**
 *
 * @author Zak Fenton
 */
public class RationalValue extends Value {

	public final Value numerator;
	
	public final Value denominator;
	
	public RationalValue(Value numerator, Value denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}

	@Override
	public Value simplify() {
		if (numerator instanceof IntegerValue && denominator instanceof IntegerValue) {
			IntegerValue n = (IntegerValue) numerator;
			IntegerValue d = (IntegerValue) denominator;
			if (!d.integer.equals(BigInteger.ZERO)) {
				BigInteger i = n.integer.divide(d.integer);
				if (i.multiply(d.integer).equals(n.integer)) {
					return new IntegerValue(i);
				}
			}
		}
		
		return this;
	}

	@Override
	public String toString() {
		return "Rational(" + " / " + denominator + ")";
	}
}

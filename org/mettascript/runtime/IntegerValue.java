/*
 * No copyright. No warranty. No liability accepted. Not tested.
 * Created 24/10/2014 by Zak Fenton.
 */
package org.mettascript.runtime;

import java.math.BigInteger;

/**
 *
 * @author zak
 */
public class IntegerValue extends Value implements Comparable<IntegerValue> {
    public final BigInteger integer;
    
    public IntegerValue(long longValue) {
    	integer = new BigInteger(Long.toString(longValue));
    }
    
    public IntegerValue(BigInteger integer) {
        this.integer = integer;
    }
    
    public IntegerValue(String stringRepresentation) {
        this.integer = new BigInteger(stringRepresentation);
    }
    
    @Override
    public Value _invoke(Value leftHandSide, String operator, Value rightHandSide) {
        rightHandSide = rightHandSide.simplify();
        
        if (rightHandSide instanceof IntegerValue) {
            IntegerValue rhs = (IntegerValue)rightHandSide;
            switch (operator) {
                case "+":
                    return new IntegerValue(integer.add(rhs.integer));
                case "-":
                    return new IntegerValue(integer.subtract(rhs.integer));
                case "*":
                    return new IntegerValue(integer.multiply(rhs.integer));
                case "/":
                    return new RationalValue(leftHandSide, rightHandSide);
                case ">":
                    return integer.compareTo(rhs.integer) > 0 ? YES : NO;
                case "<":
                    return integer.compareTo(rhs.integer) < 0 ? YES : NO;
                case "=":
                    return integer.compareTo(rhs.integer) == 0 ? YES : NO;
                case "<=":
                    return integer.compareTo(rhs.integer) <= 0 ? YES : NO;
                case ">=":
                    return integer.compareTo(rhs.integer) >= 0 ? YES : NO;
            }
        } else if (rightHandSide == NOTHING) {
        	switch (operator) {
        	case "negated":
        	case "negate":
        		return new IntegerValue(integer.negate());
        	}
        }
        
        return super._invoke(leftHandSide, operator, rightHandSide);
    }
    
    @Override
    public String toString() {
        return integer.toString();
    }

	@Override
	public int compareTo(IntegerValue o) {
		return integer.compareTo(o.integer);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o instanceof IntegerValue) {
			return this.compareTo((IntegerValue)o) == 0;
		} else {
			return false;
		}
	}
}

/* No copyright, no warranty, only code. 
 * This file was created on 22 Nov 2014. It was a good day.
 */
package org.mettascript.runtime;

import java.math.BigInteger;

/**
 *
 * @author Zak Fenton
 */
public class WithValue extends Value {
	
	private Value original;
	private Value key;
	private Value value;
	private boolean isHighest;

	/** XXX Only for use by value.with()! */
	WithValue(Value original, Value key, Value value, boolean isHighest) {
		this.original = original;
		this.key = key;
		this.value = value;
		this.isHighest = isHighest;
	}

    @Override
    public Value _invoke(Value left, String op, Value right) {
    	if (op.equals("@") && right.equals(key)) {
    		return value;
    	} else if (key instanceof OperatorValue && ((OperatorValue)key).getName("binary").equals(op)) {
    		return value;
    	} else {
    		return original._invoke(left, op, right);
    	}
    }
    
    public String toString(boolean recursive) {
    	String result;
    	if (original == NOTHING) {
    		result = "{";
    	} else if (original instanceof WithValue) {
    		result = ((WithValue)original).toString(true);
    	} else {
    		result = original.toString() + " with {";
    	}
    	
    	result += key.toString() + " = " + value.toString();
    	
    	if (recursive) {
    		result += ", ";
    	} else {
    		result += "}";
    	}
    	
    	return result;
    }
    
    @Override
    public String toString() {
    	return toString(false);
    }
    
    
    private BigInteger countElements(BigInteger otherwiseHighest) {
    	if (isHighest && otherwiseHighest.compareTo(((IntegerValue)key).integer) <= 0) {
    		return ((IntegerValue)key).integer;
    	} else if (key instanceof IntegerValue && original instanceof WithValue) {
    		BigInteger currentHighest;
    		if (otherwiseHighest.compareTo(((IntegerValue)key).integer) <= 0) {
    			currentHighest = ((IntegerValue)key).integer;
    		} else {
    			currentHighest = otherwiseHighest;
    		}
    		return ((WithValue)original).countElements(currentHighest);
    	} else if (original instanceof WithValue) {
    		return ((WithValue)original).countElements(otherwiseHighest);
    	} else if (key instanceof IntegerValue) {
    		return ((IntegerValue)key).integer;
    	} else {
    		return otherwiseHighest;
    	}
    }
    
    @Override
    public BigInteger countElements() {
    	return countElements(BigInteger.ZERO);
    }
    
    public Value getOriginal() {
    	return original;
    }
    
    public Value getKey() {
    	return key;
    }
    
    public Value getValue() {
    	return value;
    }
}

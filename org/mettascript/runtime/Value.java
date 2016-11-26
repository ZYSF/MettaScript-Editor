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
public class Value {
    
    public static final Value NOTHING = new Value();
    public static final Value YES = new Value();
    public static final Value NO = new Value();
    
    public static final Value toValueOrValues(Object... objects) {
    	if (objects.length == 0) {
    		return NOTHING;
    	} else if (objects.length == 1) {
    		return of(objects[0]);
    	} else {
    		Value[] values = new Value[objects.length];
    		for (int i = 0; i < values.length; i++) {
    			values[i] = of(objects[i]);
    		}
    		return new Values(values);
    	}
    }
    
    public static final Value of(Object o) {
    	if (o == null) {
    		return NOTHING;
    	} else if (o instanceof Value) {
    		return (Value) o;
    	} else if (o instanceof Boolean) {
    		return ((Boolean)o) ? YES : NO;
    	} else if (o instanceof Integer || o instanceof Long) {
    		return new IntegerValue(((Number)o).longValue());
    	} else if (o instanceof BigInteger) {
    		return new IntegerValue((BigInteger)o);
    	} else if (o instanceof String) {
    		return new TextValue((String)o);
    	} else {
    		return new Unknown(NOTHING, NOTHING, "toValue", new TextValue(o.toString()));
    	}
    }
    
    public final Value ask(Object left, String op, Object... right) {
    	return _invoke(of(left), op, toValueOrValues(right));
    }
    
    public final Value ask(String op, Object... right) {
    	return _invoke(this, op, toValueOrValues(right));
    }
    
    public final Value ask(String op) {
    	return _invoke(this, op, NOTHING);
    }
    
    public final Value _invoke(String op) {
    	return _invoke(this, op, NOTHING);
    }
    
    public final Value _invoke(String op, Value rightHandSide) {
        return _invoke(this, op, rightHandSide);
    }
    
    public Value _invoke(Value leftHandSide, String op, Value rightHandSide) {
        if (leftHandSide == NOTHING) {
        	switch (op) {
        	case "-":
        		return rightHandSide._invoke("negated");
        	}
        } else {
        	switch (op) {
        	case "=":
        		if (leftHandSide == rightHandSide || leftHandSide.equals(rightHandSide)) {
        			return YES;
        		} else if (leftHandSide == YES && rightHandSide != YES) {
        			return NO;
        		} else if (leftHandSide != YES && rightHandSide == YES) {
        			return NO;
        		} else if (leftHandSide == NO && rightHandSide != NO) {
        			return NO;
        		} else if (leftHandSide != NO && rightHandSide == NO) {
        			return NO;
        		} else if (leftHandSide == NOTHING && rightHandSide != NOTHING) {
        			return NO;
        		} else if (leftHandSide != NOTHING && rightHandSide == NOTHING) {
        			return NO;
        		} else {
        			break;
        		}
        	}
        }
        
        return new Unknown(this, leftHandSide, op, rightHandSide);
    }
    
    public Value simplify() {
        return this;
    }
    
    public final boolean toBoolean() {
        return this != NO && this != NOTHING;
    }
    
    @Override
    public String toString() {
        if (this == NOTHING) {
            return "Nothing";
        } else if (this == NO) {
        	return "No";
        } else if (this == YES) {
            return "Yes";
        } else {
            return "Value(" + super.toString() + ")";
        }
    }
    
    public BigInteger countElements() {
    	return BigInteger.ZERO;
    }
    
    public final Value with(String binaryKey, Value value) {
    	return new WithValue(this, new OperatorValue(binaryKey), value, false);
    }
    
    public final Value with(Value value) {
    	return new WithValue(this, new IntegerValue(countElements().add(BigInteger.ONE)), value, true);
    }
}

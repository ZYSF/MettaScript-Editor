/*
 * No copyright. No warranty. No liability accepted. Not tested.
 * Created 24/10/2014 by Zak Fenton.
 */
package org.mettascript.runtime;

/**
 *
 * @author zak
 */
public final class Reference extends Value {
    private Value value;
    
    public void setValue(Value value) {
        if (value == null) {
            throw new IllegalArgumentException("Value is null!");
        } else if (this.value != null) {
            throw new IllegalStateException("The value has already been set, it can't be set again!");
        } else {
            this.value = value;
        }
    }
    
    public Value getValue() {
        if (value == null) {
            throw new IllegalStateException("The value hasn't been set yet, so you can't have it!");
        } else {
            return value;
        }
    }
    
    @Override
    public Value _invoke(Value leftHandSide, String operator, Value rightHandSide) {
        return getValue()._invoke(leftHandSide, operator, rightHandSide);
    }
    
    @Override
    public Value simplify() {
        if (value != null) {
            return value.simplify();
        } else {
            return this;
        }
    }
    
    @Override
    public String toString() {
        if (value != null) {
            return("Reference(" + value + ")");
        } else {
            return("Reference(?)");
        }
    }
}

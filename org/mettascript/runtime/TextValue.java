/*
 * No copyright. No warranty. No liability accepted. Not tested.
 * Created 24/10/2014 by Zak Fenton.
 */
package org.mettascript.runtime;

import org.mettascript.parser.ValueSyntax;

/**
 *
 * @author zak
 */
public class TextValue extends Value {
    public final String string;
    
    public TextValue(String string) {
        this.string = string;
    }
    
    @Override
    public String toString() {
        return ValueSyntax.getSourceForString(string);
    }
    
    @Override
    public Value _invoke(Value leftHandSide, String operator, Value rightHandSide) {
        rightHandSide = rightHandSide.simplify();
        
        if (rightHandSide instanceof TextValue) {
            TextValue rhs = (TextValue)rightHandSide;
            switch (operator) {
                case "~":
					return new TextValue(string + rhs.string);
				case "=":
					if (string.equals(rhs.string)) {
						return Value.YES;
					} else {
						return Value.NO;
					}
            }
        } else if (rightHandSide instanceof IntegerValue) {
			IntegerValue rhs = (IntegerValue)rightHandSide;
			switch (operator) {
				case "unicodeAtIndex":
				{
					int index = rhs.integer.intValue();
					if (index > 0 && index <= string.length()) {
						// Subtract 1 because user indices are 1-based.
						return new IntegerValue(string.codePointAt(rhs.integer.intValue() - 1));
					} else {
						return Value.NO;
					}
				}
				case "unicodeIndexFollowing":
				{
					int index = rhs.integer.intValue();
					// Exit early if we know it will be out-of-bounds.
					if (index < 0 || index > string.length()) {
						return Value.NO;
					}
					// Exit early if looking for first character.
					if (index == 0) {
						if (string.length() > 0) {
							return new IntegerValue(1);
						} else {
							return Value.NO;
						}
					}
					// Subtract 1 because user indices are 1-based.
					int indexFollowing = index + Character.charCount(string.codePointAt(rhs.integer.intValue() - 1));
					if (indexFollowing > 0 && indexFollowing <= string.length()) {
						return new IntegerValue(indexFollowing);
					} else {
						return Value.NO;
					}
				}
			}
		} else {
			switch (operator) {
				case "isUnicodeCompatible":
					return Value.YES;
				case "internalEncoding":
					return new TextValue("UTF-16");
			}
		}
        
        return super._invoke(leftHandSide, operator, rightHandSide);
    }
}

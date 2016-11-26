/*
 * No copyright. No warranty. No liability accepted. Not tested.
 * Created 21/10/2014 by Zak Fenton.
 */
package org.mettascript.parser;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author zak
 */
public class Operation {
    
	/** The left-hand-side of the operation. This will only be null for an
	 * empty/"nothing" operation, otherwise a missing operation is replaced
	 * with such.
	 */
    public Operation leftHandSide;
    
    public Token token;
    public String operator = "<NOT-AN-OPERATOR>";
    
    /** The left-hand-side of the operation. This will only be null for an
	 * empty/"nothing" operation, otherwise a missing operation is replaced
	 * with such.
	 */
    public Operation rightHandSide;
    
    /** The operator enclosing this one (or null for the outer operation). */
    public Operation enclosing;

    /** Only used to construct empty/nothing operations, which replace nulls
     * as parameters to real operations.
     */
    private Operation() {
        
    }
    
    private Operation(Operation leftHandSide, Token token, Operation rightHandSide) {
        if (token.type == Token.Type.NAME
                || token.type == Token.Type.OPERATOR
                || token.type == Token.Type.BRACKET) {
            if (leftHandSide == null) {
                leftHandSide = new Operation();
            }
            if (rightHandSide == null) {
                rightHandSide = new Operation();
            }
            
            leftHandSide.enclosing = rightHandSide.enclosing = this;
            operator = token.toString();
        }
        
        this.leftHandSide = leftHandSide;
        this.token = token;
        this.rightHandSide = rightHandSide;
    }
    
    /** Only used by {@link FormulaParser}. */
    static Operation parse(TokenOrGroup tokenOrGroup) {
        if (tokenOrGroup.isToken) {
            return new Operation(null, tokenOrGroup.token, null);
        } else if ((tokenOrGroup.openingBracket == null || tokenOrGroup.openingBracket.firstSymbol.character == '(')
                    && tokenOrGroup.members.size() == 0) {
            return new Operation();
        } else {
            Operation leftHandSide = parse(tokenOrGroup.members.get(0));
            
            int i;
            
            for (i = 2; i < tokenOrGroup.members.size(); i += 2) {
                Token operator = tokenOrGroup.members.get(i-1).token;
                Operation rightHandSide = parse(tokenOrGroup.members.get(i));
                leftHandSide = new Operation(leftHandSide, operator, rightHandSide);
            }
            
            if (i != tokenOrGroup.members.size() + 1) {
                throw new Error("Something's not right. i=" + i + " size=" + tokenOrGroup.members.size() + "!");
            }
            
            if (tokenOrGroup.openingBracket != null
            		&& (tokenOrGroup.openingBracket.firstSymbol.character == '['
            			|| tokenOrGroup.openingBracket.firstSymbol.character == '{')) {
                return new Operation(new Operation(), tokenOrGroup.openingBracket, leftHandSide);
            } else {
                return leftHandSide;
            }
        }
    }
    
    public boolean isNothing() {
        return token == null;
    }
    
    public boolean isBinaryOperation() {
        if (token == null) {
            return false;
        } else {
            switch (token.type) {
                case NAME:
                case OPERATOR:
                case BRACKET:
                    assert leftHandSide != null;
                    assert rightHandSide != null;
                    return true;
                default:
                    return false;
            }
        }
    }
    
    public boolean isOperation(String...operatorMatches) {
        if (isBinaryOperation()) {
            for (String m: operatorMatches) {
                if (m.equals(operator)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public boolean isNormalOperation() {
        return isBinaryOperation() && !isSpecialOperation();
    }
    
    public boolean isSpecialOperation() {
        return isOperation("[", "{", "&", "|", "=", ",", ";", ".", "..", "...") && !isNormalEquals();
    }
    
    public boolean isBlock() {
        return isOperation("[");
    }
    
    public boolean isEmptyBlock() {
        return isBlock() && rightHandSide.isNothing();
    }
    
    public boolean isBlockBody() {
        return enclosing == null || enclosing.isBlock();
    }
    
    public Operation findBlockBody() {
        if (isBlockBody()) {
            return this;
        } else {
            return enclosing.findBlockBody();
        }
    }
    
    public boolean isStructure() {
        return isOperation("{");
    }
    
    public boolean isAnd() {
        return isOperation("&");
    }
    
    public boolean isOr() {
        return isOperation("|");
    }
    
    public boolean isNot() {
        return isOperation("~");
    }
    
    public boolean isEquals() {
        return isOperation("=");
    }
    
    public boolean isComma() {
        return isOperation(",");
    }
    
    public boolean isSequence() {
        return isOperation(";");
    }
    
    public boolean isDot() {
        return isOperation(".");
    }
    
    public boolean isExclamationMark() {
        return isOperation("!");
    }
    
    public boolean isQuestionMark() {
        return isOperation("?");
    }
    
    public boolean isDotDot() {
        return isOperation("..");
    }
    
    public int getCommaMemberCount() {
        if (isComma()) {
            return leftHandSide.getCommaMemberCount() + 1;
        } else {
            return 1;
        }
    }
    
    public Collection<Operation> getCommaMembers(boolean fromLeft) {
        return getAnyMembers(",", fromLeft);
    }
    
    public Collection<Operation> getSequenceMembers(boolean fromLeft) {
        return getAnyMembers(";", fromLeft);
    }
    
    private Collection<Operation> getAnyMembers(String op, boolean fromLeft) {
        ArrayList<Operation> result = new ArrayList<Operation>();
        
        if (isOperation(op)) {
            if (!fromLeft) {
                result.add(this.rightHandSide);
            }

            result.addAll(leftHandSide.getAnyMembers(op, fromLeft));

            if (fromLeft) {
                result.add(this.rightHandSide);
            }
        } else {
            result.add(this);
        }
        
        return result;
    }
    
    public int getSequenceMemberCount() {
        if (isSequence()) {
            return leftHandSide.getSequenceMemberCount() + 1;
        } else {
            return 1;
        }
    }
    
    public boolean isNameAlone() {
        return !isNothing() && leftHandSide.isNothing() && token.type == Token.Type.NAME && rightHandSide.isNothing();
    }
    
    public boolean isNormalEquals() {
        return isEquals() && !isSpecialEquals();
    }
    
    public boolean isSpecialEquals() {
        return isEquals() && enclosing != null && enclosing.isSequence();
    }
    
    public boolean isNameEquals() {
        return isSpecialEquals() && leftHandSide.isNameAlone();
    } 
    
    public boolean isConstantText() {
        return token != null && token.type == Token.Type.TEXT;
    }
    
    public boolean isConstantNumber() {
        return token != null && token.type == Token.Type.NUMBER;
    }
    
    public boolean isConstant() {
        return isConstantText() || isConstantNumber();
    }
    
    public boolean isLeftHandSide() {
        return enclosing != null && enclosing.leftHandSide == this;
    }
    
    public boolean isRightHandSide() {
        return enclosing != null && enclosing.rightHandSide == this;
    }
    
    public String toString() {
        if (isNothing()) {
            return "()";
        } else if (isConstant() || isNameAlone()) {
            return token.toString();
        } else if (isBinaryOperation()) {
            return "(" + leftHandSide + " " + operator + " " + rightHandSide + ")";
        } else {
            throw new Error("Unrecognised!");
        }
    }
}

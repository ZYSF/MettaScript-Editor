/*
 * No copyright. No warranty. No liability accepted. Not tested.
 * Created 21/10/2014 by Zak Fenton.
 */
package org.mettascript.parser;

/**
 * 
 * @author Zak Fenton
 */
public class Token {
    public final Symbol firstSymbol;
    public final Symbol lastSymbol;
    public final Type type;
    
    public enum Type {
        NAME,
        OPERATOR,
        BRACKET,
        NUMBER,
        TEXT,
        COMMENT,
        END_OF_DOCUMENT,
        UNRECOGNISED
    }
    
    /** Only to be used directly by { @ link FormulaParser#getFirstToken()} and
     * { @ link org.mettascript.parser.Token#getNextToken()}, arguments are not checked!
     */
    Token(Symbol firstSymbol, boolean ignoreComments) {
        Symbol currentSymbol = firstSymbol;
        Symbol tmp = null;
        
        while (currentSymbol.isBlank()
                || (ignoreComments && currentSymbol.character == '#')) {
            if (ignoreComments && currentSymbol.character == '#') {
                while (!currentSymbol.isEndOfDocument()
                        && !currentSymbol.isNewLine()) {
                    currentSymbol = currentSymbol.getNextSymbol();
                }
            }
            currentSymbol = currentSymbol.getNextSymbol();
        }
        
        this.firstSymbol = currentSymbol;
        
        if (currentSymbol.isEndOfDocument()) {
            type = Type.END_OF_DOCUMENT;
        } else if (currentSymbol.isBracket()) {
            type = Type.BRACKET;
        } else if (currentSymbol.canBeginName()) {
            type = Type.NAME;
            
            tmp = currentSymbol.getNextSymbol();
            
            while (tmp.canContinueName()) {
                currentSymbol = tmp;
                tmp = tmp.getNextSymbol();
            }
        } else if (currentSymbol.isDigit(10)) {
            type = Type.NUMBER;
            
            boolean hasDecimalPoint = false;
            boolean hasE = false;
            boolean hasBase = false;
            int base = 10;
            
            tmp = currentSymbol.getNextSymbol();
            
            while (tmp.isDigit(base)
                    || (tmp.character == '#' && !hasBase)
                    || (tmp.character == '.' && !hasDecimalPoint && base == 10)
                    || (tmp.character == 'E' && !hasE && hasDecimalPoint)) {
                
                if (tmp.character == '#') {
                    hasBase = true;
                    base = Integer.parseInt(firstSymbol.getStringUntil(tmp));
                    if (base != 2 && base != 8 && base != 10 && base != 16) {
                        break; /* Not a valid base, treat '#' as comment. */
                    }
                } else if (tmp.character == '.') {
                    hasDecimalPoint = true;
                } else if (tmp.character == 'E') {
                    hasE = true;
                }
                
                currentSymbol = tmp;
                
                tmp = tmp.getNextSymbol();
            }
        } else if (currentSymbol.character == '"') {
            currentSymbol = currentSymbol.getNextSymbol();
            
            while (!currentSymbol.isEndOfDocument() && currentSymbol.character != '"') {
                if (currentSymbol.character == '\\') {
                    currentSymbol = currentSymbol.getNextSymbol();
                }
                currentSymbol = currentSymbol.getNextSymbol();
            }
            
            if (currentSymbol.isEndOfDocument()) {
                type = Type.UNRECOGNISED;
            } else {
                type = Type.TEXT;
            }
        } else if (currentSymbol.isOperator()) {
            type = Type.OPERATOR;
            
            tmp = currentSymbol.getNextSymbol();
            Symbol tmp2 = tmp.getNextSymbol();
            Symbol tmp3 = tmp2.getNextSymbol();
            
            String op1 = currentSymbol.getStringUntil(tmp);
            String op2 = currentSymbol.getStringUntil(tmp2);
            String op3 = currentSymbol.getStringUntil(tmp3);
            
            if (op3.equals("...")) {
                currentSymbol = tmp2;
            } else if (op2.equals("..") || op2.equals(">=") || op2.equals("<=")
                    || op2.equals("!=")) {
                currentSymbol = tmp;
            }
        } else if (!ignoreComments && currentSymbol.character == '#') {
            type = Type.COMMENT;
            while (!currentSymbol.getNextSymbol().isEndOfDocument()
                    && !currentSymbol.getNextSymbol().isNewLine()) {
                currentSymbol = currentSymbol.getNextSymbol();
            }
        } else {
            type = Type.UNRECOGNISED;
        }
        
        lastSymbol = currentSymbol;
    }
    
    public Token getNextToken(boolean ignoreComments) {
        if (type == Type.END_OF_DOCUMENT) {
            return this;
        } else {
            return new Token(lastSymbol.getNextSymbol(), ignoreComments);
        }
    }
    
    /** Returns the exact source representation of this Token as a String. */
    @Override
    public String toString() {
        if (type == Type.END_OF_DOCUMENT) {
            return "";
        } else {
            return firstSymbol.getStringUntil(lastSymbol.getNextSymbol());
        }
    }
}

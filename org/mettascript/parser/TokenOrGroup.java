/*
 * No copyright. No warranty. No liability accepted. Not tested.
 * Created 21/10/2014 by Zak Fenton.
 */
package org.mettascript.parser;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author zak
 */
public class TokenOrGroup {

    public static final int MAXIMUM_IMPLICIT_BRACKETS = 10;

    public final boolean isGroup;

    public final boolean isToken;

    public final Token token;

    public final Token openingBracket;
    public Token closingBracket;

    final ArrayList<TokenOrGroup> members;

    private TokenOrGroup(Token token) {
        if (token == null || token.type == Token.Type.BRACKET) {
            this.token = null;

            isToken = false;
            isGroup = true;

            openingBracket = token;
            closingBracket = null;

            members = new ArrayList<TokenOrGroup>();
        } else {
            this.token = token;

            isToken = true;
            isGroup = false;

            openingBracket = closingBracket = null;
            members = null;
        }
    }

    private static void open(Stack<TokenOrGroup> stack, Token token, int times) {
        for (int i = 0; i < times; i++) {
            TokenOrGroup member = new TokenOrGroup(token);
            if (stack.size() > 0) {
                stack.peek().members.add(member);
            }
            stack.push(member);
        }
    }

    private static void close(Stack<TokenOrGroup> stack, Token token, int times) {
        for (int i = 0; i < times; i++) {
            stack.pop().closingBracket = token;
        }
    }

    static TokenOrGroup parse(Token token) {
        Stack<TokenOrGroup> stack = new Stack<TokenOrGroup>();

        open(stack, null, MAXIMUM_IMPLICIT_BRACKETS);

        while (token.type != Token.Type.END_OF_DOCUMENT) {

            int nClose = 0;
            int nOpen = 0;

            switch (token.type) {
                case BRACKET:
                    switch (token.firstSymbol.character) {
                        case '(':
                        case '[':
                        case '{':
                            nOpen = MAXIMUM_IMPLICIT_BRACKETS;
                            break;
                        default:
                            nClose = MAXIMUM_IMPLICIT_BRACKETS;
                    }
                    break;
                case NAME:
                    nOpen = nClose = 1;
                    break;
                case OPERATOR:
                    switch (token.toString()) {
                        case "*":
                        case "/":
                            nClose = nOpen = 3;
                            break;
                        case "+":
                        case "-":
                            nClose = nOpen = 4;
                            break;
                        case "!":
                            nClose = nOpen = 5;
                            break;
                        case "==":
                        case ">":
                        case ">=":
                        case "<":
                        case "<=":
                            nClose = nOpen = 6;
                            break;
                        case "&":
                        case "|":
                            nClose = nOpen = 7;
                            break;
                        case "=":
                            nClose = nOpen = 8;
                            break;
                        case ",":
                        case ";":
                            nClose = nOpen = 9;
                            break;
                        default:
                            nClose = nOpen = 1;
                            break;
                    }
            }
            
            close(stack, null, nClose);
            
            if (token.type == Token.Type.BRACKET) {
                switch(token.firstSymbol.character) {
                    case '(':
                    case '[':
                    case '{':
                        open(stack, token, 1);
                        break;
                    default:
                        close(stack, token, 1);
                }
            } else {
                stack.peek().members.add(new TokenOrGroup(token));
            }
            
            open(stack, null, nOpen);
            
            token = token.getNextToken(true);
        }

        close(stack, null, MAXIMUM_IMPLICIT_BRACKETS - 1);

        return stack.pop();
    }
    
    public boolean isImplicitGroup() {
        return isGroup && openingBracket == null;
    }
    
    public boolean isSimpleGroup() {
        return isGroup && (isImplicitGroup() || openingBracket.firstSymbol.character == '(');
    }
    
    TokenOrGroup simplify() {
        if (isSimpleGroup() && members.size() == 1) {
            return members.get(0).simplify();
        } else {
            if (isGroup) {
                for (int i = 0; i < members.size(); i++) {
                    members.set(i, members.get(i).simplify());
                }
            }
            return this;
        }
    }
    
    public String toString() {
        if (isToken) {
            return token.toString();
        } else {
            String result = openingBracket == null ? "(" : openingBracket.toString();
            boolean first = true;
            
            for (TokenOrGroup m: members) {
                if (first) {
                    first = false;
                } else {
                    result += " ";
                }
                
                result += m.toString();
            }
            
            result += closingBracket == null ? ")" : closingBracket.toString();
            
            return result;
        }
    }
}

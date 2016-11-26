/*
 * No copyright. No warranty. No liability accepted. Not tested.
 * Created 21/10/2014 by Zak Fenton.
 */
package org.mettascript.parser;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a MettaScript formula stored in the standard text format.
 * 
 * @author Zak Fenton
 */
public final class FormulaParser {
    /** The name of the document (including it's path). */
    public final String name;

    /** The contents of the document as a sequence of characters.
     * 
     * <p/>The {@link org.mettascript.parser.Symbol} class splits the contents into Unicode characters.
     */
    public final String contents;
    
    /** The number of Unicode characters in {@link org.mettascript.parser.FormulaParser#contents}, or the
     * number of {@link org.mettascript.parser.Symbol} objects in the document (excluding the end of
     * document symbol).
     */
    public final int numberOfSymbols;

    /** The number of spaces consumed by each tab character ({@code '\t'}).
     * 
     * <p/>This doesn't really matter (a value of 1 is fine) but it can be used
     * to provide more accurate values for {@link org.mettascript.parser.Symbol#column} if the setting
     * aligns to the one used in your text editor. Common settings are 4 and 8.
     */
    public final int spacesPerTab;

    /** Creates a Document.
     * 
     * @param name The name of the document (including it's path). A null value will be
     *             replaced by "Untitled". See {@link org.mettascript.parser.FormulaParser#name}.
     * @param contents The contents of the document (cannot be null). See
     *                 {@link org.mettascript.parser.FormulaParser#contents}.
     * @param spacesPerTab The number of spaces per tab (only used for debugging info).
     *                     See {@link org.mettascript.parser.FormulaParser#spacesPerTab}, or just use the value 1.
     */
    public FormulaParser(String name, String contents, int spacesPerTab) {
        if (contents == null) {
            throw new IllegalArgumentException("The contents argument is null.");
        }
        if (name == null) {
            name = "Untitled";
        }
        if (spacesPerTab < 0 || spacesPerTab > 12) {
            spacesPerTab = 1;
        }
        
        this.name = name;
        this.contents = contents;
        this.spacesPerTab = spacesPerTab;
        
        numberOfSymbols = contents.codePointCount(0, contents.length());
    }
    
    /** Returns the first symbol (character) of the formula.
     * 
     * @return The first symbol.
     */
    public Symbol getFirstSymbol() {
        return new Symbol(this, 0, 1, 1);
    }
    
    /** Returns an ordered collection of symbols.
     * 
     * @return An ordered collection of symbols.
     */
    public Collection<Symbol> getSymbols() {
        ArrayList<Symbol> symbols = new ArrayList<Symbol>();
        
        Symbol currentSymbol = getFirstSymbol();
        while (!currentSymbol.isEndOfDocument()) {
            symbols.add(currentSymbol);
            currentSymbol = currentSymbol.getNextSymbol();
        }
        
        return symbols;
    }
    
    /** Returns the first token of the formula.
     * 
     * @param ignoreComments If true, comments will not be considered tokens.
     *                       Otherwise, they will be treated like any other
     *                       token.
     * @return The first token.
     */
    public Token getFirstToken(boolean ignoreComments) {
        return new Token(getFirstSymbol(), ignoreComments);
    }
    
    /** Returns an ordered collection of tokens.
     * 
     * @param ignoreComments If true, comments will not be considered tokens.
     *                       Otherwise, they will be treated like any other
     *                       token.
     * @return An ordered collection of tokens.
     */
    public Collection<Token> getTokens(boolean ignoreComments) {
        ArrayList<Token> tokens = new ArrayList<Token>();
        
        Token currentToken = getFirstToken(ignoreComments);
        while (currentToken.type != Token.Type.END_OF_DOCUMENT) {
            tokens.add(currentToken);
            currentToken = currentToken.getNextToken(ignoreComments);
        }
        
        return tokens;
    }
    
    /** Returns a grouped view of the tokens.
     * 
     * @return The outer grouping (or sole token).
     */
    public TokenOrGroup getGroupedTokens() {
        return TokenOrGroup.parse(getFirstToken(true)).simplify();
    }
    
    /** Returns the operation this formula represents. This is the final stage
     * of parsing.
     * 
     * @return The (outermost, all encompassing) operation of the formula.
     */
    public Operation getOperation() {
        return Operation.parse(getGroupedTokens());
    }
    
    
}

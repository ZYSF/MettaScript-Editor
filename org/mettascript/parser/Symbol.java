/*
 * No copyright. No warranty. No liability accepted. Not tested.
 * Created 21/10/2014 by Zak Fenton.
 */
package org.mettascript.parser;

/**
 * Represents a single Unicode character inside a {@link FormulaParser}.
 *
 * @author Zak Fenton
 */
public final class Symbol {

    /**
     * The {@link FormulaParser} this symbol belongs to.
     */
    public final FormulaParser document;

    /**
     * The index into {@link FormulaParser#contents} at which this symbol is located.
     */
    public final int index;

    /**
     * The line number (counting from 1).
     */
    public final int line;

    /**
     * The column number, or number of spaces from the left hand side (counting
     * from 1).
     */
    public final int column;

    /**
     * The Unicode character (code-point) this symbol represents.
     */
    public final int character;

    /**
     * Only to be used directly by {@link FormulaParser#getFirstSymbol()} and
     * {@link org.mettascript.parser.Symbol#getNextSymbol()}, arguments are not checked!
     */
    Symbol(FormulaParser document, int index, int line, int column) {
        this.document = document;
        this.index = index;
        this.line = line;
        this.column = column;

        if (index >= document.contents.length()) {
            character = 0;
        } else {
            character = document.contents.codePointAt(index);
        }
    }

    /**
     * Gets the length of the Unicode character in units of Java 'char' values
     * (or string indices).
     *
     * @returns 1 or 2 for a valid symbol representing a Unicode character, 0
     * for an end-of-document symbol.
     */
    public int getLengthInJavaChars() {
        if (isEndOfDocument()) {
            return 0;
        } else if (Character.isSupplementaryCodePoint(character)) {
            return 2;
        } else {
            return 1;
        }
    }

    /** Returns true if this symbol represents a digit in the given base. Only
     * bases 2, 8, 10 and 16 are presently supported.
     * 
     * @param base The base (or radix) with which to interpret the number
     *             (either 2, 8, 10 or 16).
     * @return True if it's a digit, otherwise false.
     */
    public boolean isDigit(int base) {
        if (base != 2 && base != 8 && base != 10 && base != 16) {
            throw new IllegalArgumentException("Only bases 2, 8, 10 and 16 are accepted - not " + base);
        }

        switch (character) {
            case '0':
            case '1':
                return true;
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
                return base >= 8;
            case '8':
            case '9':
                return base >= 10;
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
                return base == 16;
            default:
                return false;
        }
    }

    /** Returns true if the symbol represents a blank (whitespace) character.
     * 
     * @return True if it's blank, otherwise false.
     */
    public boolean isBlank() {
        return Character.isWhitespace(character);
    }

    /** Returns true if the symbol can be used at the beginning of a name.
     * 
     * @return True if it can be used at the beginning of a name, otherwise
     *         false.
     */
    public boolean canBeginName() {
        return Character.isAlphabetic(character) || character == '_';
    }

    /** Returns true if the symbol can be used within a name (after the first
     * symbol).
     * 
     * @return True if it can be used after the beginning of a name, otherwise
     *         false.
     */
    public boolean canContinueName() {
        return canBeginName() || isDigit(10);
    }

    /** Returns true if the symbol is some kind of bracket.
     * 
     * @return True if it's a bracket, otherwise false.
     */
    public boolean isBracket() {
        switch (character) {
            case '(':
            case ')':
            case '[':
            case ']':
            case '{':
            case '}':
                return true;
            default:
                return false;
        }
    }
    
    /** Returns true if this symbol represents (part of) an operator.
     * 
     * @return True if it represents (part of) an operator, otherwise false.
     */
    public boolean isOperator() {
        switch (character) {
            case '+':
            case '-':
            case '*':
            case '/':
            case '&':
            case '|':
            case '!':
            case '?':
            case '@':
            case '>':
            case '<':
            case '=':
            case ';':
            case ',':
            case '.':
            case '~':
            case '$':
                return true;
            default:
                return false;
        }
    }

    /** Returns true if this symbol represents the end of the document.
     * 
     * @return True if it's the end of the document, otherwise false.
     */
    public boolean isEndOfDocument() {
        return index < 0 || index >= document.contents.length();
    }
    
    /** Returns true if this symbol represents a Unicode character (ie, not an
     * end of document symbol).
     * 
     * @return True if it's a character, otherwise false.
     */
    public boolean isUnicodeCharacter() {
        if (isEndOfDocument()) {
            return false;
        } else {
            return Character.isValidCodePoint(character);
        }
    }

    /** Returns true if this symbol represents a new-line character.
     * 
     * @return True if it's a new line, otherwise false.
     */
    public boolean isNewLine() {
        return character == '\n';
    }

    /** Returns the symbol following the present symbol (in writing order, so
     * left-to-right for English text and numbers, right-to-left for Arabic
     * text, etc.).
     * 
     * @return The next symbol.
     */
    public Symbol getNextSymbol() {
        if (isEndOfDocument()) {
            return this;
        } else {
            int nextIndex = index + getLengthInJavaChars();
            int nextLine;
            int nextColumn;
            
            if (isNewLine()) {
                nextLine = line + 1;
                nextColumn = 1;
            } else if (character == '\t') {
                nextLine = line;
                nextColumn = document.spacesPerTab - (column % document.spacesPerTab);
            } else {
                nextLine = line;
                nextColumn = column + 1;
            }
            
            return new Symbol(document, nextIndex, nextLine, nextColumn);
        }
    }
    
    /** Returns the string consisting of the symbols up to (not including) the
     * parameter.
     * 
     * @param laterSymbol The symbol immediately following the last symbol of
     *                    the desired string.
     * 
     * @return The string of symbols up to laterSymbol.
     */
    public String getStringUntil(Symbol laterSymbol) {
        return document.contents.substring(index, laterSymbol.index);
    }
    
}

/* No copyright, no warranty, only code. 
 * This file was created on 19 Nov 2014. It was a good day.
 */
package org.mettascript.parser;

import java.math.BigInteger;

/**
 * This class operates separately to the parser, and is used to convert between
 * literal value representations used in source code and representations which
 * are more convenient to deal with.
 * 
 * @author Zak Fenton
 */
public final class ValueSyntax {
	public static final String DIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String digits = "0123456789abcdefghijklmnopqrstuvwxyz";
	
	/** Returns the base (or radix) of the integer value.
	 * 
	 * <p/>For the unenlightened, this means the number of unique digits which
	 * can be used to to represent a number (e.g. decimal uses ten, binary uses
	 * two).
	 * 
	 * @param source Source code representing an integer (e.g. 100, 16#FFC0).
	 * @return The base (or radix) of the integer value (e.g. 10, 16).
	 */
	public static int getIntegerBase(String source) {
		int hashPosition = source.indexOf('#');
		if (hashPosition == -1) {
			return 10;
		} else {
			String baseString = source.substring(0, hashPosition);
			int baseInt = Integer.parseInt(baseString);
			if (baseInt > 1 && baseInt < DIGITS.length()) {
				return baseInt;
			} else {
				throw new NumberFormatException("Not a valid integer (base is out of range): " + source);
			}
		}
	}
	
	/** Returns the digits (without the explicit base, if any) of the integer
	 * value.
	 * 
	 * @param source Source code representing an integer (e.g. 100, 16#FFC0).
	 * @return The part of the parameter which represent the number's digits
	 *         alone (e.g. 100 or FFC0 - without the "16#").
	 */
	public static String getIntegerDigits(String source) {
		int hashPosition = source.indexOf('#');
		if (hashPosition == -1) {
			return source;
		} else {
			return source.substring(hashPosition + 1);
		}
	}

	/** Returns true if the source string is a valid integer.
	 * 
	 * @param string The source string (potentially an integer value).
	 * @return True if it's an integer, false otherwise.
	 */
	public static boolean isInteger(String string) {
		try {
			int base = getIntegerBase(string);
		
			new BigInteger(getIntegerDigits(string), base);
		} catch (NumberFormatException e) {
			return false;
		}
		
		return true;
	}
	
	public static String getStringValue(String source) {
		// TODO!!!!!!
		// XXX NOT PROPER AT ALL
		return source.substring(1, source.length()-1);
	}
	
	public static String getSourceForString(String value) {
		String source = "\"";
		// TODO ALSO NEEDS FIXING
		for (char c: value.toCharArray()) {
			if (c == '\n') {
				source += "\\n";
			} else if (c == '\t') {
				source += "\\t";
			} else if (c == '\0') {
				source += "\\0";
			} else if (c == '\\') {
				source += "\\\\";
			} else if (c == '\"') {
				source += "\\\"";
			} else {
				source += new String(new char[]{c});
			}
		}
		
		return source + "\"";
	}
}

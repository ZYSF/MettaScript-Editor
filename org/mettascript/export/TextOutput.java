/* No copyright, no warranty, only code. 
 * This file was created on 03/11/2014. It was a good day.
 */
package org.mettascript.export;

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

/**
 *
 * @author Zak Fenton
 */
public abstract class TextOutput {
    private PrintStream out;
    private int indent = 0;
    private ByteArrayOutputStream byteArrayOutputStream = null;
    
    private int line = 1;
    private int column = 1;
    private int columnsPerTab = 4;

	public TextOutput() {
		byteArrayOutputStream = new ByteArrayOutputStream();
		out = new PrintStream(byteArrayOutputStream);
	}
	
	public TextOutput(PrintStream out) {
		this.out = out;
	}
	
	protected void write(String s) {
		write(s, true, line, -1);
	}
	
	protected void write(String s, boolean indent, int targetLine, int targetColumn) {
		while (line < targetLine) {
			out.println();
		}
		while (line == targetLine && column < targetColumn) {
			out.print(' ');
		}
		
		if (indent) {
			s = s.replace("\n", "\n" + getCurrentIndentString());
		}
		
		out.print(s);
		
		for (char c: s.toCharArray()) {
			if (c == '\n') {
				column = 1;
				line++;
			} else if (c == '\t') {
				column += getColumnsPerTab();
			} else {
				column++;
			}
		}
	}
	
	private final String getCurrentIndentString() {
		String s = "";
		for (int i = 0; i < indent; i++) {
			s += getIndentString();
		}
		return s;
	}
	
	public void indentMore() {
		indent++;
	}
	
	public void indentLess() {
		if (indent < 1) {
			throw new Error("How do I decrease the indent from level " + indent + "?");
		}
	}
	
	protected String getIndentString() {
		return "    ";
	}
	
	protected int getColumnsPerTab() {
		return columnsPerTab;
	}
	
	public int getCurrentLine() {
		return line;
	}
	
	public int getCurrentColumn() {
		return column;
	}
	
	@Override
	public String toString() {
		if (byteArrayOutputStream != null) {
			return new String(byteArrayOutputStream.toString());
		} else {
			return super.toString();
		}
	}
}

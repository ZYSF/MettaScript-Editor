/* No copyright, no warranty, only code. 
 * This file was created on 30/10/2014. It was a good day.
 */
package org.mettascript.editor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.mettascript.parser.FormulaParser;

/**
 *
 * @author Zak Fenton
 */
public class Document {
	
	private File file = null;
	
	private String fullSource = null;
	
	public enum Template {
		INTRODUCTION,
		EMPTY
	}
	
	public Document() {
		this(Template.INTRODUCTION);
	}

	public Document(Template template) {
		String src = "";
		switch (template) {
		case INTRODUCTION:
			src += "# Welcome To MettaScript!\n";
			src += "# MettaScript is a general purpose calculator.\n";
			src += "# It could theoretically be used for anything (web development, movie rendering, games)\n";
			src += "# but this is a PRERELEASE ALPHA VERSION with many limitations and bugs. Refer to the Help\n";
			src += "# menu for more information.\n\n";
			src += "# Uncomment the following examples one by one to try them. Use the side views (bottom right)\n";
			src += "# to see how they work.\n\n";
			src += "# 1 + 1\n\n";
			src += "# x = {2,3}; x 2\n\n";
			src += "# x = {a=2,b=3}; x b\n\n";
			src += "# x = [? + 1]; x 2\n\n";
			src += "# (min, max) = (0, 100); isValid = [? >= min & ? <= max]; isValid(99)\n\n";
			src += "# (min, max) = (0, 100); isNotValid = [? < min | ? > max]; isNotValid(99)\n\n";
			src += "# addMul = [(x,y,z)=?; x + y * z]; addMul(1,2,3)\n\n";
			src += "# Fibonacci = [? < 2 & ? | Fibonacci(?-1) + Fibonacci(?-2)]; Fibonacci 20\n\n";
			src += "# 1000000000 * 3       # Try typing this in Java and you'll see why I made a calculator!\n\n";
			break;
		case EMPTY:
			src += "# Page intentionally left blank.";
			break;
		default:
			src += "# Unknown template: " + template;
		}
		
		setSource(src);
	}
	
	public Document(File file) throws IOException {
		this.file = file;
		/* TODO MAKE ASYNCHRONOUS VERSION */
		if (file.exists()) {
			InputStream input = new FileInputStream(file);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			int nread;
			byte[] bytes = new byte[1000];
			while ((nread = input.read(bytes)) > 0) {
				System.out.println("Read " + nread + " bytes.");
				output.write(bytes, 0, nread);
			}
			input.close();
			output.close();
			setSource(output.toString());
		} else {
			setSource("# New file (was the file you were trying to open deleted?)\n");
		}
	}

	public void setSource(String source) {
		fullSource = source;
	}
	
	public String getSource() {
		return fullSource;
	}
	
	public String getName() {
		if (file == null) {
			return "Untitled.metta";
		} else {
			return file.getName();
		}
	}
	
	public FormulaParser getParser() {
		return new FormulaParser(getName(), getSource(), 4);
	}
	
	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public void save() throws IOException {
		PrintStream output = new PrintStream(new FileOutputStream(file));
		output.print(getSource());
		output.close();
	}
}

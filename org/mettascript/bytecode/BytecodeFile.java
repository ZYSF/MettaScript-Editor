/* No copyright, no warranty, only code. 
 * This file was created on 15 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode;

import java.io.IOException;
import java.io.PrintStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import org.mettascript.bytecode.instructions.Instruction;
import org.mettascript.parser.FormulaParser;

/**
 *
 * @author Zak Fenton
 */
public class BytecodeFile extends AbstractBytecodeObject {
	
	public static final String HEADER = "#!/usr/bin/env metta-v1 li\nZYSF\0";
	public static final byte[] HEADER_BYTES = HEADER.getBytes();
	public static final int HEADER_SIZE = 32;
	
	private HashMap<String,TextConstant> texts = new HashMap<String, TextConstant>();
	private HashMap<BigInteger,IntegerConstant> integers = new HashMap<BigInteger, IntegerConstant>();
	private HashMap<String,OperatorReference> operators = new HashMap<String, OperatorReference>();
	private ArrayList<OperatorReference> operatorsByIndex = null;
	private ArrayList<Constant> constantsByIndex = null;
	private ArrayList<Block> blocks = new ArrayList<Block>();
	private ArrayList<UnknownFactor> unknownFactors = new ArrayList<UnknownFactor>();
	private long totalDataLength = -1;

	public BytecodeFile(FormulaParser parser) throws CompilationException {
		/* XXX Temporary: Add standard operators at known indices. */
		/* This isn't enabled yet (it gets garbled anyway).
		getOperator("@");			// 0
		getOperator("+");
		getOperator("-");
		getOperator("*");
		getOperator("/");
		getOperator("~");			// 5
		getOperator("=");
		getOperator("<>");
		getOperator("<");
		getOperator("<=");
		getOperator(">");			// 10
		getOperator(">=");
		getOperator("^");
		getOperator("shiftLeft");
		getOperator("rotateLeft");
		getOperator("shiftRight");		// 15
		getOperator("extendRight");
		getOperator("rotateRight");
		getOperator("andBits");
		getOperator("orBits");
		getOperator("opposite");		// 20
		getOperator("unicodeAtIndex");
		getOperator("unicodeIndexFollowing");
		getOperator("asText");
		getOperator("unicodeAsText");
		getOperator("count");			// 25
		getOperator("unicodeCount");
		getOperator("unused27");
		getOperator("unused28");
		getOperator("unused29");
		getOperator("unused30");		// 30
		getOperator("unused31");
		*/
		
		new Block(this, parser);
	}

	/* XXX Only for use in Block's constructors. */
	void addBlock(Block block) {
		blocks.add(block);
	}
	
	UnknownFactor declareUnknownFactor(String name, int referenceIndex) {
		UnknownFactor result = new UnknownFactor(this, getText(name), referenceIndex);
		if (result.index != unknownFactors.size()) {
			throw new IllegalStateException("Attempted to declare an UnknownFactor (" + result.index + ") out-of-order!");
		}
		unknownFactors.add(result);
		return result;
	}
	
	IntegerConstant getInteger(BigInteger value) {
		if (integers.containsKey(value)) {
			return integers.get(value);
		} else {
			IntegerConstant result = new IntegerConstant(this, value);
			integers.put(value, result);
			return result;
		}
	}
	
	TextConstant getText(String value) {
		if (texts.containsKey(value)) {
			return texts.get(value);
		} else {
			TextConstant result = new TextConstant(this, value);
			texts.put(value, result);
			invalidateIndices();
			return result;
		}
	}
	
	OperatorReference getOperator(String value) {
		if (operators.containsKey(value)) {
			return operators.get(value);
		} else {
			OperatorReference result = new OperatorReference(this, value);
			operators.put(value, result);
			return result;
		}
	}
	
	private boolean indicesUpToDate = false;
	
	public void invalidateIndices() {
		indicesUpToDate = false;
	}
	
	public void calculateIndices() {
		if (indicesUpToDate) {
			return;
		}
		
		constantsByIndex = new ArrayList<Constant>();
		
		totalDataLength = 0;
		
		for (IntegerConstant ic: integers.values()) {
			ic.index = constantsByIndex.size();
			constantsByIndex.add(ic);
		}
		
		for (TextConstant tc: texts.values()) {
			tc.index = constantsByIndex.size();
			constantsByIndex.add(tc);
			totalDataLength += tc.getValueString().getBytes().length;
		}
		
		operatorsByIndex = new ArrayList<OperatorReference>();
		
		for (OperatorReference or: operators.values()) {
			or.index = operatorsByIndex.size();
			operatorsByIndex.add(or);
			totalDataLength += or.getNameString().getBytes().length;
		}
		
		int i = 0;
		for (Block b: blocks) {
			b.index = i;
			i++;
			totalDataLength += b.getInstructions().size() * 2;
		}
		
		indicesUpToDate = true;
	}

	@Override
	public void print(PrintStream output, boolean jsonFormat) {
		calculateIndices();
		
		boolean printedSomething = false;
		
		if (jsonFormat) {
			output.println("{");
			
			output.print("  constants: [");
			for (Constant c: constantsByIndex) {
				if (printedSomething) {
					output.println(",");
				} else {
					output.println();
				}
				c.print(output, true);
				printedSomething = true;
			}
			output.println("],");
			printedSomething = false;
			
			output.print("  operators: [");
			for (OperatorReference or: operators.values()) {
				if (printedSomething) {
					output.println(",");
				} else {
					output.println();
				}
				or.print(output, true);
				printedSomething = true;
			}
			output.println("],");
			printedSomething = false;
			
			output.print("  unknown: [");
			for (UnknownFactor uf: unknownFactors) {
				if (printedSomething) {
					output.println(",");
				} else {
					output.println();
				}
				uf.print(output, true);
				printedSomething = true;
			}
			output.println("],");
			printedSomething = false;
			
			output.print("  blocks: [");
			for (Block b: blocks) {
				if (printedSomething) {
					output.println(",");
				} else {
					output.println();
				}
				b.print(output, true);
				printedSomething = true;
			}
			output.println("]}");
		} else {
			for (IntegerConstant ic: integers.values()) {
				ic.print(output, false);
				printedSomething = true;
			}
			
			if (printedSomething) {
				output.println();
				printedSomething = false;
			}
			
			for (TextConstant tc: texts.values()) {
				tc.print(output, false);
				printedSomething = true;
			}
			
			if (printedSomething) {
				output.println();
				printedSomething = false;
			}
			
			for (OperatorReference or: operators.values()) {
				or.print(output, false);
				printedSomething = true;
			}
			
			if (printedSomething) {
				output.println();
				printedSomething = false;
			}
			
			for (UnknownFactor uf: unknownFactors) {
				uf.print(output, false);
				printedSomething = true;
			}
			
			if (printedSomething) {
				output.println();
				printedSomething = false;
			}
			
			for (Block b: blocks) {
				b.print(output, false);
				output.println();
			}
		}
	}
	
	// Just for consistency.
	private static void write8(OutputStream out, int value) throws IOException {
		out.write(value);
	}
	
	private static void write16(OutputStream out, int value) throws IOException {
		out.write(value);
		out.write(value >> 8);
	}
	
	private static void write32(OutputStream out, int value) throws IOException {
		write16(out, value);
		write16(out, value>>16);
	}
	
	public byte[] encode() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			encode(baos);
		} catch (IOException e) {
			throw new Error("This shouldn't happen. There wasn't any IO.");
		}
		
		return baos.toByteArray();
	}
	
	public String printBytesToString(boolean useSignedValues, boolean useHex, boolean useLeadingZeroes, int perLine) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		printBytes(ps, useSignedValues, useHex, useLeadingZeroes, perLine);
		return baos.toString();
	}
	
	public void printBytes(PrintStream out, boolean useSignedValues, boolean useHex, boolean leadingZeroes, int perLine) {
		byte[] bytes = encode();
		for (int i = 0; i < bytes.length; i++) {
			
			if (i > 0) {
				if (i % perLine == 0) {
					out.println(",");
				} else {
					out.print(", ");
				}
			}
			
			int n = bytes[i];
			if (!useSignedValues) {
				n &= 0xff;
			}
			
			String s;
			
			if (useHex) {
				s = Integer.toString(n, 16);
				while (s.length() < 2) {
					s = "0" + s;
				}
				s = "0x" + s;
			} else {
				s = Integer.toString(n);
				while (s.length() < 3) {
					s = " " + s;
				}
			}
			
			out.print(s);
		}
	}
	
	public void encode(OutputStream out) throws IOException {
		if (HEADER_BYTES.length != HEADER_SIZE) {
			throw new Error("Internal error! The header constant is the wrong size.\n");
		}
		
		out.write(HEADER_BYTES);
		
		calculateIndices();
		
		long size = 0; // HEADER_SIZE not included, the header may be removed
		size += 12; // Number of bytes used in the index headers
		size += 8 * unknownFactors.size();
		size += 12 * blocks.size();
		size += 8 * constantsByIndex.size();
		size += 8 * operators.size();
		
		long dataPointer = size;	// Updated iteratively as data is added to
		ByteArrayOutputStream data;	// <-- this handy little fellow.
		data = new ByteArrayOutputStream();
		
		size += totalDataLength; // Texts, operator names, etc. calculated prior
		
		write32(out, (int)size);
		
		write16(out, unknownFactors.size());
		write16(out, blocks.size());
		write16(out, constantsByIndex.size());
		write16(out, operatorsByIndex.size());
		
		for (UnknownFactor uf: unknownFactors) {
			write32(out, uf.getTextIndex());
		}
		
		for (Block b: blocks) {
			write16(out, b.getStackSize());
			write16(out, b.getReferencedSlots().length);
			write16(out, b.getReservedSlots().length);
			write16(out, b.getInstructions().size());
			write32(out, (int)dataPointer);
			dataPointer += b.getInstructions().size() * 2;
			
			for (Instruction i: b.getInstructions()) {
				write16(data, i.encode());
			}
		}
		
		for (Constant c: constantsByIndex) {
			if (c instanceof TextConstant) {
				TextConstant tc = (TextConstant)c;
				byte[] value = tc.getValueString().getBytes();
				write8(out, 1);
				write8(out, 0);		// Reserved for future use
				write16(out, value.length);
				write32(out, (int)dataPointer);
				dataPointer += value.length;
				data.write(value);
			} else if (c instanceof IntegerConstant) {
				IntegerConstant ic = (IntegerConstant)c;
				write8(out, ic.fitsIn32Bits() ? 2 : 3);
				write8(out, 0);		// Reserved for future use
				write16(out, 0);	// Reserved for future use
				write32(out, ic.fitsIn32Bits() ? ic.get32BitValue() : ic.getAssociatedText().getIndex());
			} else {
				throw new IllegalArgumentException("WTF is " + c + "?");
			}
		}
		
		for (OperatorReference or: operatorsByIndex) {
			byte[] value = or.getNameString().getBytes();
			write16(out, 0);		// Reserved for future use
			write16(out, value.length);
			write32(out, (int)dataPointer);
			dataPointer += value.length;
			data.write(value);
		}
		
		data.close(); // No effect, just for clarity.
		
		out.write(data.toByteArray());
		out.close();
	}
	
	public Block getMainBlock() {
		calculateIndices();
		
		if (blocks.size() == 0) {
			throw new IllegalStateException("No blocks are present yet!");
		}
		
		return blocks.get(0);
	}
}

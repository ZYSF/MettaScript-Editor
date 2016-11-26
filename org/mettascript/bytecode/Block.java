/* No copyright, no warranty, only code. 
 * This file was created on 15 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode;

import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mettascript.bytecode.instructions.*;
import org.mettascript.bytecode.instructions.PushSpecialInstruction.ValueType;
import org.mettascript.parser.FormulaParser;
import org.mettascript.parser.Operation;
import org.mettascript.parser.ValueSyntax;

/**
 *
 * @author Zak Fenton
 */
public class Block extends ConstantLike {
	private Block enclosingBlock;
	
	private HashMap<String,Integer> reservedSlotsByName = new HashMap<String,Integer>();
	
	private HashMap<String,Integer> referencedSlotsByName = new HashMap<String,Integer>();
	
	private ArrayList<Instruction> instructions = new ArrayList<Instruction>();
	
	ArrayList<Label> labels = new ArrayList<Label>();
	private Label hangingLabel = null;
	
	int stackSize = -1;

	private Block(BytecodeFile constantPool) {
		super(constantPool);
		constantPool.addBlock(this);
	}
	
	public Block(Block enclosingBlock) {
		this(enclosingBlock.getBytecodeFile());
		this.enclosingBlock = enclosingBlock;
	}
	
	Block(Block enclosingBlock, Operation blockOperation) throws CompilationException {
		this(enclosingBlock);
		
		compileBody(blockOperation.rightHandSide);
	}
	
	Block(BytecodeFile bytecodeFile, FormulaParser parser) throws CompilationException {
		this(bytecodeFile);
		
		compileBody(parser.getOperation());
		
		for (int i = 0; i < referencedSlotsByName.size(); i++) {
			int index = -1 - i;
			for (Entry<String, Integer> e: referencedSlotsByName.entrySet()) {
				if (e.getValue().equals(index)) {
					System.out.println("i = " + i + " index = " + index + " name = " + e.getKey());
					getBytecodeFile().declareUnknownFactor(e.getKey(), index);
					break;
				}
			}
		}
	}

	private void compileBody(Operation operation) throws CompilationException {
		int lastMember = operation.getSequenceMemberCount();
		int thisMember = 1;
		
		for (Operation member: operation.getSequenceMembers(true)) {
			if (thisMember == lastMember) {
				int numberOfReturnValues = compileOperation(member);
				insert(new ReturnInstruction(numberOfReturnValues));
			} else if (member.isEquals()) {
				int numberOfNames = member.leftHandSide.getCommaMemberCount();
				
				/* Reserve names (iterating left-to-right). */
				for (Operation name: member.leftHandSide.getCommaMembers(true)) {
					if (!name.isNameAlone()) {
						throw new CompilationException(name, "Expected name alone on left-hand-side of special equals form!");
					} else if (reservedSlotsByName.containsKey(name.operator)) {
						throw new CompilationException(name, "This name has already been given a value within this scope!");
					}
					reservedSlotsByName.put(name.operator, reservedSlotsByName.size());
				}
				
				/* Compile code to produce the value(s). */
				compileOperation(member.rightHandSide, numberOfNames, true);
				
				/* Associate values with names (iterating right-to-left). */
				for (Operation name: member.leftHandSide.getCommaMembers(false)) {
					insert(new PutInstruction(name.operator));
				}
			} else {
				/* The member is assumed to be commentary. */
			}
			
			thisMember++;
		}
	}
	
	private void compileOperation(Operation operation, int desiredValues,
			boolean splitOrCombine) throws CompilationException {
		int actualValues = compileOperation(operation);
		
		if (splitOrCombine && actualValues == 1 && desiredValues > 1) {
			insert(new SplitInstruction(desiredValues));
		} else if (splitOrCombine && actualValues > 1 && desiredValues == 1) {
			insert(new CombineInstruction(actualValues));
		} else if (actualValues > desiredValues) {
			insert(new PopInstruction(actualValues - desiredValues));
		} else if (actualValues < desiredValues) {
			insert(new PushSpecialInstruction(desiredValues - actualValues));
		}
	}
	
	private String parseString(String source) {
		// TODO: Replace with (and finish work on) org.mettascript.parser.ValueSyntax
		source = source.substring(1, source.length() - 1);
		source = source.replace("\\n", "\n");
		source = source.replace("\\t", "\t");
		source = source.replace("\\0", "\0");
		source = source.replace("\\\\", "\\");
		return source;
	}
	
	private int compileOperation(Operation operation) throws CompilationException {
		/* Some instructions can be simplified when handling values which are
		 * known to be Nothing, so instead of compiling the code to produce a
		 * value in these cases, no value is produced. If you need a Nothing
		 * value, insert a ValNInstruction.
		 */
		if (operation.isNothing()) {
			return 0;
		} else if (operation.isConstantNumber()) {
			BigInteger value = new BigInteger(operation.token.toString());
			if (PushIntegerInstruction.isWithinBounds(value)) {
				insert(new PushIntegerInstruction(value.intValue()));
			} else {
				Constant constant = getBytecodeFile().getInteger(value);
				insert(new PushConstantInstruction(constant));
			}
			return 1;
		} else if (operation.isConstantText()) {
			insert(new PushConstantInstruction(getBytecodeFile().getText(
					parseString(operation.token.toString()))));
			return 1;
		} else if (operation.isAnd() || operation.isOr()) {
			Label end = new Label(this);
			compileOperation(operation.leftHandSide, 1, true);
			insert(new CopyInstruction(1));
			insert(new JumpIfInstruction(end, operation.isOr()));
			insert(new PopInstruction(1));
			compileOperation(operation.rightHandSide, 1, true);
			hangLabel(end);
			return 1;
		} else if (operation.isOperation("{")) {
			insert(new PushSpecialInstruction(PushSpecialInstruction.ValueType.EMPTY));
			if (operation.rightHandSide.isNothing()) {
				return 1;
			}
			for (Operation member: operation.rightHandSide.getCommaMembers(true)) {
				if (member.isEquals() && member.leftHandSide.isNameAlone()) {
					compileOperation(member.rightHandSide, 1, true);
					insert(new WithKeyAndValueInstruction(getBytecodeFile().getOperator(member.leftHandSide.operator)));
				} else {
					compileOperation(member, 1, true);
					insert(new WithValueInstruction());
				}
			}
			return 1;
		} else if (operation.isComma()) {
			for (Operation member: operation.getCommaMembers(true)) {
				compileOperation(member, 1, true);
			}
			return operation.getCommaMemberCount(); 
		} else if (operation.isBlock()) {
			Block block = new Block(this, operation);
			insert(new ClosureInstruction(block));
			int numberOfReferences = block.referencedSlotsByName.size();
			for (int i = 0; i < numberOfReferences; i++) {
				int referenceIndex = -1 - i;
				String referenceName = null;
				for (Entry<String, Integer> e: block.referencedSlotsByName.entrySet()) {
					if (e.getValue().equals(referenceIndex)) {
						referenceName = e.getKey();
						break;
					}
				}
				insert(new GetInstruction(referenceName, true));
			}
			return 1;
		} else if (operation.leftHandSide.isNothing()) {
			if (operation.isDot()) {
				insert(new PushSpecialInstruction(ValueType.LEFT));
			} else if (operation.isExclamationMark()) {
				insert(new PushSpecialInstruction(ValueType.OPERATOR));
			} else if (operation.isQuestionMark()) {
				insert(new PushSpecialInstruction(ValueType.RIGHT));
			} else {
				insert(new GetInstruction(operation.operator, false));
			}
			
			if (!operation.rightHandSide.isNothing()) {
				compileOperation(operation.rightHandSide, 1, true);
				insert(new AskInstruction(getBytecodeFile().getOperator("@")));
			}
			return 1;
		} else {
			compileOperation(operation.leftHandSide, 1, true);
			compileOperation(operation.rightHandSide, 1, true);
			insert(new AskInstruction(getBytecodeFile().getOperator(operation.operator)));
			return 1;
		}
	}
	
	public int lookupSlot(String name) {
		if (reservedSlotsByName.containsKey(name)) {
			return reservedSlotsByName.get(name);
		} else if (referencedSlotsByName.containsKey(name)) {
			return referencedSlotsByName.get(name);
		} else {
			int result = -(referencedSlotsByName.size() + 1);
			referencedSlotsByName.put(name, result);
			return result;
		}
	}
	
	public void hangLabel(Label label) {
		/* TODO: Check if already hanging?? */
		hangingLabel = label;
	}

	public Instruction insert(Instruction instruction) {
		if (instruction instanceof ReturnInstruction && instructions.size() > 0) {
			ReturnInstruction ri = (ReturnInstruction) instruction;
			Instruction i = instructions.get(instructions.size()-1);
			if (ri.getNumberOfValues() == 1 && i instanceof AskInstruction) {
				((AskInstruction)i).setTailCall(true);
				if (hangingLabel == null) {
					return i;
				}
			}
		}
		
		stackSize = -1; /* Causes recalculation. */
		
		instructions.add(instruction);
		instruction.onInsert(this);
		
		if (hangingLabel != null) {
			hangingLabel.setTargetInstruction(instruction);
			hangingLabel = null;
		}
		
		return instruction;
	}

	@Override
	public void print(PrintStream output, boolean jsonFormat) {
		boolean printedSomething = false;
		
		if (jsonFormat) {
			output.println("    {");
			output.println("      max_stack: " + getStackSize() + ",");
			if (referencedSlotsByName.size() > 0) {
				output.println("      references: [");
				for (String s: getReferencedSlots()) {
					if (printedSomething) {
						output.println(",");
					}
					output.print("        " + ValueSyntax.getSourceForString(s));
					printedSomething = true;
				}
				output.println("],");
				printedSomething = false;
			}
			if (reservedSlotsByName.size() > 0) {
				output.println("      reserved: [");
				for (String s: getReservedSlots()) {
					if (printedSomething) {
						output.println(",");
					}
					output.print("        " + ValueSyntax.getSourceForString(s));
					printedSomething = true;
				}
				output.println("],");
				printedSomething = false;
			}
			output.println("      instructions: [");
			for (Instruction i: instructions) {
				if (printedSomething) {
					output.println(",");
				}
				
				i.print(output, true);
				
				printedSomething = true;
			}
			output.print("]}");
		} else {
			output.println("BLOCK " + getIndex() + ":");
			
			output.println("    MAXIMUM STACK SIZE: " + getStackSize() + "\n");
			
	
			for (Entry<String, Integer> r: referencedSlotsByName.entrySet()) {
				output.println("    REFERENCE " + r.getValue() + ":" + r.getKey());
				printedSomething = true;
			}
			
			if (printedSomething) {
				printedSomething = false;
				output.println();
			}
			
			for (Entry<String, Integer> r: reservedSlotsByName.entrySet()) {
				output.println("    RESERVED " + r.getValue() + ":" + r.getKey());
				printedSomething = true;
			}
			
			if (printedSomething) {
				printedSomething = false;
				output.println();
			}
			
			for (Instruction i: instructions) {
				i.print(output, false);
			}
			
			output.println("END");
		}
	}
	
	public String[] getReservedSlots() {
		String[] result = new String[reservedSlotsByName.size()];
		
		for (int i = 0; i < result.length; i++) {
			for (Entry<String,Integer> e: reservedSlotsByName.entrySet()) {
				System.err.println("DEBUG " + e.getKey() + " = " + e.getValue().intValue());
				if (e.getValue().intValue() == i) {
					result[i] = e.getKey();
					break;
				}
			}
			
			if (result[i] == null) {
				throw new Error("Unexpected! No reserved slot with index " + i + "!");
			}
		}
		
		return result;
	}
	
	public String[] getReferencedSlots() {
		String[] result = new String[referencedSlotsByName.size()];
		
		for (int i = 0; i < result.length; i++) {
			int index = -1 - i;
			
			for (Entry<String,Integer> e: referencedSlotsByName.entrySet()) {
				if (e.getValue().equals(index)) {
					result[i] = e.getKey();
					break;
				}
			}
			
			if (result[i] == null) {
				throw new Error("Unexpected! No referenced slot with index " + i + " (referenced as " + index + ")!");
			}
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return Integer.toString(getIndex());
	}
	
	private void resetStackSize(int i) {
		if (i > stackSize) {
			stackSize = i;
		}
	}
	
	private void calculateStackSize() {
		int currentSize = reservedSlotsByName.size();
		
		for (Instruction i: instructions) {
			currentSize += i.getStackImbalance();
			resetStackSize(currentSize);
		}
	}
	
	public int getStackSize() {
		if (stackSize < 0) {
			calculateStackSize();
		}
		
		return stackSize;
	}
	
	public List<Instruction> getInstructions() {
		return instructions;
	}
}

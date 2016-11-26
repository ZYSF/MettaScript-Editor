/* No copyright, no warranty, only code. 
 * This file was created on 16 Nov 2014. It was a good day.
 */
package org.mettascript.vm;

import org.mettascript.runtime.*;
import org.mettascript.bytecode.*;
import org.mettascript.bytecode.instructions.*;
import org.mettascript.bytecode.instructions.Instruction.Type.*;
import org.mettascript.bytecode.instructions.PushSpecialInstruction.ValueType;

/**
 * A closure value represents a block which has been instantiated, "closing in"
 * any external factors present at the time of it's creation.
 * 
 * <p/>All blocks within the bytecode are instantiated in this manner, but the
 * developer will generally want to deal with the outermost ("main") block as a
 * closure. In this case, 
 *
 * @author Zak Fenton
 */
public class ClosureValue extends Value {
	public final Block block;
	private Value[] references;

	public ClosureValue(Block block, Value... references) {
		this.block = block;
		
		String[] slotNames = block.getReferencedSlots();
		
		if (references.length == slotNames.length) {
			this.references = references;
		} else {
			this.references = new Value[slotNames.length];
		}
		
		for (int i = 0; i < this.references.length; i++) {
			if (i < references.length && references[i] != null) {
				this.references[i] = references[i].simplify();
			} else {
				switch(slotNames[i]) {
				case "Yes":
				case "yes":
				case "true":
				case "True":
					this.references[i] = YES;
					break;
				case "No":
				case "no":
				case "false":
				case "False":
					this.references[i] = NO;
					break;
				case "nothing":
				case "Nothing":
				case "nil":
				case "Nil":
				case "null":
				case "Null":
				case "NULL":
					this.references[i] = NOTHING;
					break;
				case "-":
					this.references[i] = new Value() {
						
						@Override
						public Value _invoke(Value leftHandSide, String op,
								Value rightHandSide) {
							return new IntegerValue(0)._invoke(leftHandSide, op, rightHandSide);
						}
						
					};
					break;
				case "~":
				case "Not":
				case "not":
					this.references[i] = new Value() {
						@Override
						public Value _invoke(Value leftHandSide, String op, Value rightHandSide) {
							return rightHandSide.toBoolean() ? NO : YES;
						}
					};
					break;
				default:
					this.references[i] = new Unknown(slotNames[i]);
				}
			}
		}
	}
	
	public boolean setFactor(String name, Value value) {
		String[] slots = block.getReferencedSlots();
		
		for (int i = 0; i <  slots.length; i++) {
			if (slots[i].equals(name)) {
				references[i] = value;
				return true;
			}
		}
		
		return false;
	}

	@Override
	public Value _invoke(Value leftHandSide, String operator, Value rightHandSide) {
		Context context = Context.getInstance();
		
		BytecodeCallFrame callFrame = context.newBytecodeCallFrame(this);
		
		callFrame.leftHandSide = leftHandSide;
		callFrame.operator = operator;
		callFrame.rightHandSide = rightHandSide;
		
		while (true) {
			context.doDebugChecks();
			
			callFrame = (BytecodeCallFrame)context.stack.peek();
			Block block = callFrame.closure.block;
			
			/* Decode the next instruction. */
			Instruction i = block.getInstructions().get(callFrame.instructionPointer++);
			
			//System.out.println("Performing " + i.getType() + " instruction...");
			
			Instruction.Type it = i.getType();
			switch (it) {
			case PUSH_INTEGER:
				callFrame.push(new IntegerValue(((PushIntegerInstruction)i).getInteger()));
				break;
				
			case PUSH_CONSTANT:
				callFrame.push(((PushConstantInstruction)i).getConstant().getValue());
				break;
				
			case GET: {
				int index = ((GetInstruction)i).getSlotIndex();
				
				if (index < 0) {
					index = -1 - index;
					callFrame.push(callFrame.closure.references[index].simplify());
				} else {
					callFrame.push(callFrame.stack[index].simplify());
				}
				break;
			}
			
			case PUT:
				((Reference)callFrame.stack[((PutInstruction)i).getSlotIndex()]).setValue(callFrame.pop());
				break;
				
			case POP: {
				int nv = ((PopInstruction)i).getNumberOfValues();
				
				while (nv > 0) {
					callFrame.pop();
					nv--;
				}
				
				break;
			}
			
			case COPY: {
				int nv = ((CopyInstruction)i).getNumberOfCopies();
				
				Value v = callFrame.pop();
				
				while (nv >= 0) {
					callFrame.push(v);
					nv--;
				}
				
				break;
			}
				
			case ASK:
			case ASK_TAIL: {
				Value r = callFrame.pop();
				Value l = callFrame.pop();
				String op = ((AskInstruction)i).getOperatorString().intern();
				boolean isTail = it == Instruction.Type.ASK_TAIL;
				
				if (l instanceof ClosureValue) {
					BytecodeCallFrame cf;
					if (isTail) {
						cf = callFrame;
						cf.setClosure((ClosureValue)l);
					} else {
						cf = context.newBytecodeCallFrame((ClosureValue)l);
					}
					cf.leftHandSide = l;
					cf.operator = op;
					cf.rightHandSide = r;
				} else {
					//System.out.println("Invoking (" + l + " " + op + " " + r + ")...");
					Value rv = l._invoke(op, r);
					if (isTail) {
						context.stack.pop();
						if (context.stack.isEmpty()) {
							return rv;
						} else {
							((BytecodeCallFrame)context.stack.peek()).push(rv);
						}
					} else {
						callFrame.push(rv);
					}
				}
				
				break;
			}
			
			case WITH_VALUE: {
				Value v = callFrame.pop();
				Value o = callFrame.pop();
				callFrame.push(o.with(v));
				break;
			}
			
			case WITH_KEY_AND_VALUE: {
				Value v = callFrame.pop();
				Value o = callFrame.pop();
				String k = ((WithKeyAndValueInstruction)i).getKey().getNameString();
				callFrame.push(o.with(k, v));
				break;
			}
			
			case PUSH_SPECIAL: {
				PushSpecialInstruction vni = (PushSpecialInstruction)i;
				Value v;
				switch (vni.getValueType()) {
				case EMPTY: // TODO: Not sure whether to differentiate from NOTHING.
				case NOTHING:
					v = Value.NOTHING;
					break;
				case NO:
					v = Value.NO;
					break;
				case YES:
					v = Value.YES;
					break;
				case LEFT:
					v = callFrame.leftHandSide;
					break;
				case OPERATOR:
					v = new OperatorValue(callFrame.operator);
					break;
				case RIGHT:
					v = callFrame.rightHandSide;
					break;
				default:
					throw new Error("Unknown value type " + vni.getValueType());
				}
				
				for (int j= 0; j < vni.getNumberOfCopies(); j++) {
					callFrame.push(v);
				}
				
				break;
			}
			
			case SPLIT: {
				int nv = ((SplitInstruction)i).getNumberOfValues();
				Value v = callFrame.pop();
				for (int j = 0; j < nv; j++) {
					callFrame.push(v._invoke("@", new IntegerValue(j+1)));
				}
				
				break;
			}
			
			case COMBINE: {
				int nv = ((CombineInstruction)i).getNumberOfValues();
				Value[] vs = new Value[nv];
				
				for (int j = vs.length - 1; j >= 0; j--) {
					vs[j] = callFrame.pop();
				}
				
				callFrame.push(new Values(vs));
				
				break;
			}
			
			case JUMP_IF: {
				JumpIfInstruction jii = (JumpIfInstruction)i;
				
				Value predicate = callFrame.pop();
				
				if (predicate.toBoolean() == jii.getValueToJumpOn()) {
					callFrame.instructionPointer = jii.getTarget().
							getTargetInstruction().getIndexWithoutCompression();
				}
				
				break;
			}
			
			case CLOSURE: {
				ClosureInstruction ci = (ClosureInstruction)i;
				int nr = ci.getBlock().getReferencedSlots().length;
				Value[] refs = new Value[nr];
				for (int j = 0; j < nr; j++) {
					GetInstruction gi =
							(GetInstruction)block.getInstructions()
							.get(callFrame.instructionPointer++);
					int index = gi.getSlotIndex();
					if (index < 0) {
						index = -1 - index;
						refs[j] = callFrame.closure.references[index].simplify();
					} else {
						refs[j] = callFrame.stack[index].simplify();
					}
				}
				callFrame.push(new ClosureValue(ci.getBlock(), refs));
				break;
			}
			
			case RETURN: {
				int nv = ((ReturnInstruction)i).getNumberOfValues();
				
				Value rv;
				
				if (nv == 0) {
					rv = NOTHING;
				} else if (nv == 1) {
					rv = callFrame.pop();
				} else {
					Value[] values = new Value[nv];
					while (nv > 0) {
						values[--nv] = callFrame.pop();
					}
					rv = new Values(values);
				}
				
				context.stack.pop(); /* Remove nested call frame. */
				
				if (context.stack.isEmpty()) {
					return rv;
				} else {
					((BytecodeCallFrame)context.stack.peek()).push(rv);
				}
				break;
			}
			
			default:
				throw new Error("TODO: Instruction " + i.getType());
			}
		}
	}
}

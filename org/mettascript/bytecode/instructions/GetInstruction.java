/* No copyright, no warranty, only code. 
 * This file was created on 15 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode.instructions;

import java.io.PrintStream;

import org.mettascript.bytecode.Block;

/**
 *
 * @author Zak Fenton
 */
public class GetInstruction extends Instruction {

	private String locationName;
	private boolean isForClosure;
	
	public GetInstruction(String locationName, boolean isForClosure) {
		this.locationName = locationName;
		this.isForClosure = isForClosure;
	}
	
	int parameterIndex = -999999;
	
	@Override
	public int getStackImbalance() {
		return 1;
	}
	
	@Override
	public void onInsert(Block enclosingBlock) {
		parameterIndex = enclosingBlock.lookupSlot(locationName);
		super.onInsert(enclosingBlock);
	}

	@Override
	public String getParameterString(boolean jsonFormat) {
		if (jsonFormat) {
			return ", " + parameterIndex;
		} else {
			if (locationName != null) {
				return parameterIndex + " [" + locationName + "]";
			} else {
				return Integer.toString(parameterIndex);
			}
		}
	}

	@Override
	public Type getType() {
		return Type.GET;
	}
	
	@Override
	public int encode() {
		return super.encode() | (parameterIndex << 4);
	}

	@Override
	public void print(PrintStream output, boolean jsonFormat) {
		if (!jsonFormat && isForClosure) {
			output.print("  ");
		}
		super.print(output, jsonFormat);
	}
	
	public int getSlotIndex() {
		return parameterIndex;
	}
}

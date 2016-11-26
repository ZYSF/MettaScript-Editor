/* No copyright, no warranty, only code. 
 * This file was created on 30/10/2014. It was a good day.
 */
package org.mettascript;

import java.util.Timer;
import java.util.TimerTask;

import org.mettascript.bytecode.BytecodeFile;
import org.mettascript.editor.Document;
import org.mettascript.runtime.*;
import org.mettascript.vm.*;

/**
 *
 * @author Zak Fenton
 */
public abstract class DocumentInterpreter  {
	private Document document;
	private Thread thread;
	
	private long timeout = 10000;
	
	private Timer timer;
	
	
	public DocumentInterpreter(Document document, boolean useThread) {
		this.document = document;
		
		if (useThread) {
			thread = new Thread() {
				@Override
				public void run() {
					actuallyInterpret();
				}
			};
			
			timer = new Timer("Document timer " + toString(), true);
			timer.schedule(new TimerTask(){

				@Override
				public void run() {
					if (thread.isAlive()) {
						thread.stop(new Error("The calculation took too long so I killed it. This may cause unexpected problems, restart the editor if anything goes awry."));
					}
				}}, timeout);
		}
	}
	
	public void interpret() {
		if (thread != null) {
			thread.start();
		} else {
			actuallyInterpret();
		}
	}
	
	private void actuallyInterpret() {
		try {
			BytecodeFile file = new BytecodeFile(document.getParser());
			ClosureValue closure = new ClosureValue(file.getMainBlock());
			Value value = closure._invoke("getResult");
			value = value.simplify();
			onResult(value);
		} catch (Throwable e) {
			onError(e);
		}
	}
	
	protected abstract void onResult (Value value);
	
	protected abstract void onError (Object e);
}

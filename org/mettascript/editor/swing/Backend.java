/* No copyright, no warranty, only code. 
 * This file was created on 22 Nov 2014. It was a good day.
 */
package org.mettascript.editor.swing;

import javax.swing.JComponent;

import org.mettascript.vm.ClosureValue;

/**
 *
 * @author Zak Fenton
 */
public abstract class Backend {
	
	protected OutputPanel outputPanel;
	protected ClosureValue closure;

	public Backend(OutputPanel outputPanel, ClosureValue closure) {
		this.outputPanel = outputPanel;
		this.closure = closure;
	}
	
	public boolean autoScrollVertically() {
		return true;
	}
	
	public boolean autoScrollHorizontally() {
		return true;
	}

	public abstract JComponent createOutputComponent();
	
	public abstract JComponent createConfigurationComponent();
}

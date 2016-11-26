/* No copyright, no warranty, only code. 
 * This file was created on 30 Nov 2014. It was a good day.
 */
package org.mettascript.editor.swing.backend;

import javax.swing.JComponent;

import org.mettascript.editor.swing.Backend;
import org.mettascript.editor.swing.OutputPanel;
import org.mettascript.vm.ClosureValue;

/**
 *
 * @author Zak Fenton
 */
public class TestBackend extends Backend {
	public TestBackend(OutputPanel outputPanel, ClosureValue closure) {
		super(outputPanel, closure);
	}

	@Override
	public JComponent createOutputComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JComponent createConfigurationComponent() {
		// TODO Auto-generated method stub
		return null;
	}

}

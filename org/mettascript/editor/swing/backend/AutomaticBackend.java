/* No copyright, no warranty, only code. 
 * This file was created on 22 Nov 2014. It was a good day.
 */
package org.mettascript.editor.swing.backend;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.mettascript.editor.swing.*;
import org.mettascript.vm.ClosureValue;


/**
 *
 * @author Zak Fenton
 */
public class AutomaticBackend extends Backend {
	OutputPanel.BackendType selectedType;
	Backend actualBackend;

	public AutomaticBackend(OutputPanel outputPanel, ClosureValue closure) {
		super(outputPanel, closure);
		selectedType = OutputPanel.BackendType.Calculation;
		actualBackend = new CalculationBackend(outputPanel, closure);
	}

	@Override
	public JComponent createOutputComponent() {
		return actualBackend.createOutputComponent();
	}

	@Override
	public JComponent createConfigurationComponent() {
		return new JLabel("Selected Backend: " + selectedType);
	}

}

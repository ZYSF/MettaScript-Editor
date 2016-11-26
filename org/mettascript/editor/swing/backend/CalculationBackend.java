/* No copyright, no warranty, only code. 
 * This file was created on 22 Nov 2014. It was a good day.
 */
package org.mettascript.editor.swing.backend;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.mettascript.editor.swing.Backend;
import org.mettascript.editor.swing.OutputPanel;
import org.mettascript.runtime.Value;
import org.mettascript.vm.ClosureValue;

/**
 *
 * @author Zak Fenton
 */
public class CalculationBackend extends Backend {
	
	Value result;

	public CalculationBackend(OutputPanel outputPanel, ClosureValue closure) {
		super(outputPanel, closure);
		
		result = closure._invoke("getResult").simplify();
	}

	@Override
	public JComponent createOutputComponent() {
		JTextArea ta = new JTextArea();
		ta.setEditable(false);
		ta.setBorder(BorderFactory.createEmptyBorder());
		ta.setFocusable(false);
		ta.setText(result.toString());
		ta.setBackground(Color.GREEN.darker().darker().darker().darker().darker());
		ta.setForeground(Color.YELLOW.brighter().brighter().brighter());
		ta.setCaretPosition(0);
		return ta;
	}

	@Override
	public JComponent createConfigurationComponent() {
		return new JLabel("Not much to configure here.");
	}

}

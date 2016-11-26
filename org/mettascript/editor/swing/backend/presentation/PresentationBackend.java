/* No copyright, no warranty, only code. 
 * This file was created on 22 Nov 2014. It was a good day.
 */
package org.mettascript.editor.swing.backend.presentation;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mettascript.editor.swing.Backend;
import org.mettascript.editor.swing.OutputPanel;
import org.mettascript.editor.swing.backend.canvas.CanvasLibrary;
import org.mettascript.editor.swing.backend.scenegraph.SceneComponent;
import org.mettascript.editor.swing.backend.scenegraph.SceneLibrary;
import org.mettascript.library.BindingContext;
import org.mettascript.runtime.IntegerValue;
import org.mettascript.runtime.Value;
import org.mettascript.vm.ClosureValue;

/**
 *
 * @author Zak Fenton
 */
public class PresentationBackend extends Backend {
	
	Presentation presentation;
	
	public PresentationBackend(OutputPanel outputPanel, ClosureValue closure) {
		super(outputPanel, closure);
		
		setWidth(outputPanel.getContentWidth());
		setHeight(outputPanel.getContentHeight());
		
		BindingContext bindingContext = new BindingContext(closure);
		
		bindingContext.load(CanvasLibrary.class, true);
		bindingContext.load(SceneLibrary.class, true);
		bindingContext.load(PresentationLibrary.class, true);
		
		Value result = closure.ask("present").simplify();
		
		if (!(result instanceof Presentation)) {
			throw new Error("The result isn't a valid presentation! Type was " + result.getClass() + " instead!");
		}
		
		presentation = (Presentation)result;
	}
	
	private void setWidth(int width) {
		closure.setFactor("width", new IntegerValue(width));
	}
	
	private void setHeight(int height) {
		closure.setFactor("height", new IntegerValue(height));
	}
	
	@Override
	public boolean autoScrollHorizontally() {
		return false;
	}
	
	@Override
	public boolean autoScrollVertically() {
		return false;
	}

	@Override
	public JComponent createOutputComponent() {
		Value result = closure._invoke("present").simplify();
		try {
			Presentation presentation = (Presentation)result;
			return new SceneComponent(presentation);
		} catch (ClassCastException cce) {
			throw new Error("The result doesn't appear to be a valid presentation!", cce);
		}
	}

	@Override
	public JComponent createConfigurationComponent() {
		BorderLayout borderLayout = new BorderLayout();
		//borderLayout.setHgap(0);
		//borderLayout.setVgap(0);
		JPanel panel = new JPanel(borderLayout);
		panel.add(new JLabel("TODO: Configuration"), BorderLayout.CENTER);
		panel.add(new JLabel(presentation.toString()), BorderLayout.EAST);
		panel.setVisible(true);
		return panel;
	}

	
}

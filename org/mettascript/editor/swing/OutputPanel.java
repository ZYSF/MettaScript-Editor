/* No copyright, no warranty, only code. 
 * This file was created on 22 Nov 2014. It was a good day.
 */
package org.mettascript.editor.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.*;

import com.alee.extended.statusbar.WebMemoryBar;
import org.mettascript.bytecode.BytecodeFile;
import org.mettascript.editor.swing.backend.AutomaticBackend;
import org.mettascript.editor.swing.backend.CalculationBackend;
import org.mettascript.editor.swing.backend.presentation.PresentationBackend;
import org.mettascript.parser.FormulaParser;
import org.mettascript.vm.ClosureValue;

/**
 *
 * @author Zak Fenton
 */
public class OutputPanel extends JPanel {

    EditorPanel editorPanel;
	
	public enum BackendType {
		Automatic,
		Calculation,
		Presentation
	}
	
	JComboBox<BackendType> runtimeComboBox;
	JPanel outerControlPanel;
	JPanel innerControlPanel;
	JPanel contentPanel;
	
	Backend backend;
	
	public OutputPanel(EditorPanel editorPanel) {
		super(new BorderLayout());
        this.editorPanel = editorPanel;
		
		BorderLayout contentLayout = new BorderLayout();
		contentLayout.setHgap(0);
		contentLayout.setVgap(0);
		contentPanel = new JPanel(contentLayout);
		add(contentPanel, BorderLayout.CENTER);
		
		outerControlPanel = new JPanel(new BorderLayout());
		runtimeComboBox = new JComboBox<>();
		runtimeComboBox.addItem(BackendType.Calculation);
		runtimeComboBox.addItem(BackendType.Presentation);
		runtimeComboBox.addItem(BackendType.Automatic);
		runtimeComboBox.setSelectedItem(BackendType.Automatic);
		runtimeComboBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refresh(false);
			}
		});
		outerControlPanel.add(runtimeComboBox, BorderLayout.WEST);
		innerControlPanel = new JPanel(new BorderLayout());
		outerControlPanel.add(innerControlPanel, BorderLayout.CENTER);

        outerControlPanel.add(new WebMemoryBar(), BorderLayout.EAST);
		add(outerControlPanel, BorderLayout.SOUTH);
		
		setContent(new JLabel("Loading Content..."), false, false);
		setConfiguration(new JLabel("Loading Configuration..."));
		
		refresh(false);
	}
	
	private JComponent currentContent;
	private JComponent currentConfiguration;
	
	public void setContent(JComponent component, boolean autoScrollHorizontally, boolean autoScrollVertically) {
		System.out.println("Setting content to " + component);
		
		if (autoScrollHorizontally || autoScrollVertically) {
			JScrollPane scrollPane = new JScrollPane(component);
			scrollPane.setHorizontalScrollBarPolicy(autoScrollHorizontally ? JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED : JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setVerticalScrollBarPolicy(autoScrollVertically ? JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED : JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			if (scrollPane.getVerticalScrollBar() != null) {
				scrollPane.getVerticalScrollBar().setValue(0);
			}
			scrollPane.setBorder(BorderFactory.createEmptyBorder());
			//scrollPane.add(component);
			component = scrollPane;
		}
		
		if (currentContent != null) {
			contentPanel.remove(currentContent);
		}
		currentContent = component;
		contentPanel.add(component, BorderLayout.CENTER);
		component.setVisible(true);
		contentPanel.setVisible(true);
		component.doLayout();
		contentPanel.doLayout();
		contentPanel.invalidate();
		contentPanel.validate();
		contentPanel.repaint();
		doLayout();
        if (getRootPane() != null) {
            getRootPane().doLayout();
        }
	}
	
	private void setContent(Throwable error) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		error.printStackTrace(ps);
		ps.close();
		
		JTextArea ta = new JTextArea();
		ta.setForeground(Color.RED);
		ta.setText(baos.toString());
		ta.setEditable(false);
		ta.setFocusable(false);
		ta.setCaretPosition(0);
		setContent(ta, true, true);
	}
	
	private void setConfiguration(JComponent component) {
		System.out.println("Setting configuration to " + component);
		if (currentConfiguration != null) {
			innerControlPanel.remove(currentConfiguration);
		}
		currentConfiguration = component;
		innerControlPanel.add(component, BorderLayout.CENTER);
		component.setVisible(true);
		//innerControlPanel.setVisible(true);
		//component.doLayout();
		innerControlPanel.doLayout();
		outerControlPanel.doLayout();
		//doLayout();
		outerControlPanel.invalidate();
		outerControlPanel.validate();
		outerControlPanel.repaint();
        if (getRootPane() != null) {
            getRootPane().doLayout();
        }
	}
	
	public void setBackend(Backend backend) {
		this.backend = backend;
		
		setConfiguration(backend.createConfigurationComponent());
		setContent(backend.createOutputComponent(), backend.autoScrollHorizontally(), backend.autoScrollVertically());
	}
	
	public int getContentWidth() {
		return contentPanel.getWidth();
	}
	
	public int getContentHeight() {
		return contentPanel.getHeight();
	}
	
	public void refresh(boolean explicit) {
		try {
			FormulaParser parser = editorPanel.getDocument().getParser();
			BytecodeFile bytecode = new BytecodeFile(parser);
			ClosureValue closure = new ClosureValue(bytecode.getMainBlock());
			switch((BackendType)runtimeComboBox.getSelectedItem()) {
			case Automatic:
				setBackend(new AutomaticBackend(this, closure));
				break;
			case Calculation:
				setBackend(new CalculationBackend(this, closure));
				break;
			case Presentation:
				setBackend(new PresentationBackend(this, closure));
				break;
			default:
				throw new Error("Unexpected backend type!");
			}
		} catch (Throwable error) {
			error.printStackTrace();
			setContent(error);
		}
	}
}

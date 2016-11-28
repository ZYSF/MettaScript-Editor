/* No copyright, no warranty, only code. 
 * This file was created on 8 Nov 2014. It was a good day.
 */
package org.mettascript.editor.swing;

import org.mettascript.editor.*;

import org.mettascript.parser.FormulaParser;
import org.mettascript.bytecode.BytecodeFile;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 *
 * @author Zak Fenton
 */
public class MainWindow extends JFrame {
	private static final long serialVersionUID = -3647121287061933797L;

    private TabShell tabShell;
	
	private Timer updateTimer;
	
	static private ArrayList<Image> icons = new ArrayList<Image>();
	
	/** A static constructor exists to load resources from the jar file. */
	static {
		ClassLoader cl = MainWindow.class.getClassLoader();
		try {
			icons.add(ImageIO.read(cl.getResource("org/mettascript/editor/swing/icon-20x20.png")));
			icons.add(ImageIO.read(cl.getResource("org/mettascript/editor/swing/icon-24x24.png")));
			icons.add(ImageIO.read(cl.getResource("org/mettascript/editor/swing/icon-32x32.png")));
			icons.add(ImageIO.read(cl.getResource("org/mettascript/editor/swing/icon-48x48.png")));
			icons.add(ImageIO.read(cl.getResource("org/mettascript/editor/swing/icon-64x64.png")));
		} catch (Exception e) {
			System.err.println("WARNING: Error while loading image resources!");
			e.printStackTrace();
		}
	}
	
	public MainWindow() {
		this(new Document());
	}

	public MainWindow(Document document) {

        tabShell = new TabShell(this, document);
        getActivePanel().updateTitle();

		createInterface();
		
		setIconImages(icons);
		
		updateTimer = new Timer(200, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				getActivePanel().onTimerTick();
			}
			
		});
		updateTimer.start();
	}
	
	void doOpen() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FormulaFileFilter());
		int result = fileChooser.showDialog(MainWindow.this, "Open (in New Window)");
		if (result == JFileChooser.APPROVE_OPTION) {
			try {
                getTabShell().openDocument(new Document(fileChooser.getSelectedFile()));
				/* Old behaviour:
				new MainWindow(new Document(fileChooser.getSelectedFile())).setVisible(true);
				 */
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {
			System.out.println("Open operation terminated by user.");
		}
	}
	
	void doSave() {
		if (getDocument().getFile() == null) {
			doSaveAs();
		} else {
			try {
				getDocument().save();
				getActivePanel().documentChangedSinceSave = false;
				getActivePanel().updateTitle();
			} catch (IOException e) {
				// TODO
				e.printStackTrace();
			}
		}
	}
	
	void doSaveAs() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FormulaFileFilter());
		int result = fileChooser.showSaveDialog(this);// fileChooser.showDialog(this, "Save As");
		if (result == JFileChooser.APPROVE_OPTION) {
			getDocument().setFile(fileChooser.getSelectedFile());
			getActivePanel().updateTitle();
			doSave();
		} else {
			System.out.println("Save As operation terminated by user.");
		}
	}
	
	void doQuickBuild() {
		if (getDocument() == null || getDocument().getFile() == null) {
			JOptionPane.showMessageDialog(null, "You need to save your formula somewhere before you can use Quick Build.", "Not Saved!", JOptionPane.ERROR_MESSAGE);
			doSaveAs();
		}
		try {
			FormulaParser parser = getDocument().getParser();
			BytecodeFile bytecode = new BytecodeFile(parser);
			FileOutputStream output = new FileOutputStream(getDocument().getFile().getPath().replaceFirst("[.][^.]+$", ".mbc"));
			bytecode.encode(output);
			output.close();
		} catch (Throwable t) {
			JOptionPane.showMessageDialog(null, t.toString(), "Internal Error During Build!", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void createInterface() {
		setLayout(new BorderLayout());
        setJMenuBar(new MenuBar(this));

        setContentPane(tabShell);

		setLocationByPlatform(true);
		setMinimumSize(new Dimension(770,650));

		addWindowListener(new WindowListener(){

			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				close();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				System.out.println("Closed.");
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowActivated(WindowEvent e) {
				System.out.println("Activated.");
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				System.out.println("Deactivated.");
			}});
	}
	
	void close() {
		System.out.println("Closing.");
		updateTimer.stop();
		dispose();
	}

    public EditorPanel getActivePanel() {
        return (EditorPanel) tabShell.getSelectedDocument().getComponent();
    }

    public TabShell getTabShell() {
        return tabShell;
    }

	public Document getDocument() {
        return getActivePanel().getDocument();
	}
}


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
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.prefs.*;
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
	
	private static ArrayList<Image> icons = new ArrayList<Image>();
	
	public static final Preferences preferences;
	
	/**
	 * A static constructor exists to load resources from the jar file.
	 * It also loads the preferences.
	 */
	static {
		preferences = Preferences.userNodeForPackage(MainWindow.class);
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
		this(new Document(), false);
	}

	public MainWindow(Document document, boolean changedSinceSave) {

		tabShell = new TabShell(this, document);
		getActivePanel().updateTitle();
		getActivePanel().documentChangedSinceSave = changedSinceSave;

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
	
	public static String[] getRecentFiles(String exceptName) {
		int max = preferences.getInt("RECENTFILES_MAX", 10);
		
		if (max < 0 || max > 100) {
			max = 10;
		}
		
		String[] result = new String[max];
		
		int j = 0;
		for (int i = 0; i < max; i++) {
			String thisValue = preferences.get("RECENTFILES_" + i, "");
			
			if (!thisValue.equals("") && !thisValue.equals(exceptName)) {
				result[j] = thisValue;
				j++;
			}
		}
		
		for (; j < max; j++) {
			result[j] = "";
		}
		
		return result;
	}
	
	public static String[] getRecentFiles() {
		return getRecentFiles("");
	}
	
	public static void rememberRecentFile(String name) {
		String[] oldRecents = getRecentFiles(name);
		
		preferences.put("RECENTFILES_0", name);
		
		for (int i = 1; i < oldRecents.length; i++) {
			preferences.put("RECENTFILES_" + i, oldRecents[i - 1]);
		}
	}
	
	public boolean doOpen(String filename) {
		return doOpen(new File(filename));
	}
	
	public boolean doOpen(File file) {
		try {	
			getTabShell().openDocument(new Document(file));
			rememberRecentFile(file.getPath());
			return true;
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "Unable to open file!", "I/O error.", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
			return false;
		}
	}
	
	public boolean doOpen() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FormulaFileFilter());
		int result = fileChooser.showDialog(MainWindow.this, "Open (in New Window)");
		if (result == JFileChooser.APPROVE_OPTION) {
			return doOpen(fileChooser.getSelectedFile());
		} else {
			System.out.println("Open operation terminated by user.");
			return false;
		}
	}
	
	public boolean doSave() {
		if (getDocument().getFile() == null) {
			return doSaveAs();
		} else {
			try {
				getDocument().save();
				getActivePanel().documentChangedSinceSave = false;
				getActivePanel().updateTitle();
				
				rememberRecentFile(getDocument().getFile().getPath());
				
				return true;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Unable to save file!", "I/O error.", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				
				return false;
			}
		}
	}
	
	boolean doSaveAs() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FormulaFileFilter());
		int result = fileChooser.showSaveDialog(this);// fileChooser.showDialog(this, "Save As");
		if (result == JFileChooser.APPROVE_OPTION) {
			getDocument().setFile(fileChooser.getSelectedFile());
			getActivePanel().updateTitle();
			return doSave();
		} else {
			System.out.println("Save As operation terminated by user.");
			return false;
		}
	}
	
	boolean doBuildBytecode() {
		if (getDocument() == null || getDocument().getFile() == null) {
			JOptionPane.showMessageDialog(null, "You need to save your formula somewhere before you can use Quick Build.", "Not Saved!", JOptionPane.ERROR_MESSAGE);
			if (!doSaveAs()) {
				return false;
			}
		}
		try {
			FormulaParser parser = getDocument().getParser();
			BytecodeFile bytecode = new BytecodeFile(parser);
			FileOutputStream output = new FileOutputStream(getDocument().getFile().getPath().replaceFirst("[.][^.]+$", ".mbc"));
			bytecode.encode(output);
			output.close();
			return true;
		} catch (Throwable t) {
			JOptionPane.showMessageDialog(null, t.toString(), "Internal Error During Build!", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	boolean doBuildJSON() {
		if (getDocument() == null || getDocument().getFile() == null) {
			JOptionPane.showMessageDialog(null, "You need to save your formula somewhere before you can use Quick Build.", "Not Saved!", JOptionPane.ERROR_MESSAGE);
			if (!doSaveAs()) {
				return false;
			}
		}
		try {
			FormulaParser parser = getDocument().getParser();
			BytecodeFile bytecode = new BytecodeFile(parser);
			FileOutputStream output = new FileOutputStream(getDocument().getFile().getPath().replaceFirst("[.][^.]+$", ".mbc.json"));
			bytecode.print(new PrintStream(output), true);
			output.close();
			return true;
		} catch (Throwable t) {
			JOptionPane.showMessageDialog(null, t.toString(), "Internal Error During Build!", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	boolean doBuildCSV() {
		if (getDocument() == null || getDocument().getFile() == null) {
			JOptionPane.showMessageDialog(null, "You need to save your formula somewhere before you can use Quick Build.", "Not Saved!", JOptionPane.ERROR_MESSAGE);
			if (!doSaveAs()) {
				return false;
			}
		}
		try {
			FormulaParser parser = getDocument().getParser();
			BytecodeFile bytecode = new BytecodeFile(parser);
			FileOutputStream output = new FileOutputStream(getDocument().getFile().getPath().replaceFirst("[.][^.]+$", ".mbc.csv"));
			bytecode.printBytes(new PrintStream(output), false, false, false, 10);
			output.close();
			return true;
		} catch (Throwable t) {
			JOptionPane.showMessageDialog(null, t.toString(), "Internal Error During Build!", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	boolean doSaveAll() {
		// TODO: Save other tabs.
		return doSave();
	}
	
	boolean hasUnsavedChanges() {
		for (EditorPanel p: getPanels()) {
			if (p.documentChangedSinceSave) {
				return true;
			}
		}
		
		return false;
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
				if (hasUnsavedChanges()) {
					// Old behaviour:
					// switch (JOptionPane.showConfirmDialog(null, "Would you like to save them before closing?", "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION)) {
					// Temporary behaviour (awaiting saveAll):
					switch (JOptionPane.showConfirmDialog(null, "Some open files are unsaved. Are you sure you want to close this window?", "Unsaved Changes", JOptionPane.YES_NO_OPTION)) {
					case JOptionPane.YES_OPTION:
						//if (doSaveAll()) {
						close();
						//}
						break;
					
					//case JOptionPane.CANCEL_OPTION:
					//	/* No action (not closing). */
					//	break;
					
					case JOptionPane.NO_OPTION:
						// Don't close.
						break;
					}
				} else {
					close();
				}
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
    
    public List<EditorPanel> getPanels() {
		ArrayList<EditorPanel> l = new ArrayList<EditorPanel>();
		
		for (TabShell.Tab t: tabShell.getDocuments()) {
			l.add((EditorPanel) t.getComponent());
		}
		
		return l;
	}

    public TabShell getTabShell() {
        return tabShell;
    }

	public Document getDocument() {
        return getActivePanel().getDocument();
	}
}


/* No copyright. No warranty. No code. USE AT YOUR OWN RISK.
 * Created by zak on 17/03/15. It was a good day.
 */

package org.mettascript.editor.swing;

import org.mettascript.editor.Document;

import javax.swing.*;
import javax.swing.event.MenuListener;
import javax.swing.event.MenuEvent;
import javax.swing.text.DefaultEditorKit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class MenuBar extends JMenuBar {
    private MainWindow mainWindow;

    public MenuBar (MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        createMenus();
    }

    private void createMenus() {
        JMenu formula = new JMenu("Formula");
        formula.setMnemonic('f');
        JMenuItem formulaNew = new JMenuItem("New");
        formulaNew.setMnemonic('n');
        formulaNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        formulaNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindow.getTabShell().openDocument(new Document(Document.Template.EMPTY));
                /* Old behaviour:
                new MainWindow(new Document(Document.Template.EMPTY)).setVisible(true);
                 */
            }
        });
        formula.add(formulaNew);
        JMenuItem formulaOpen = new JMenuItem("Open...");
        formulaOpen.setMnemonic('o');
        formulaOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        formulaOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindow.doOpen();
            }
        });
        formula.add(formulaOpen);
        JMenu  formulaOpenRecent = new JMenu("Open Recent");
        formulaOpenRecent.setMnemonic('r');
        formulaOpenRecent.addMenuListener(new MenuListener(){
			public void menuCanceled(MenuEvent e) {}
			public void menuDeselected(MenuEvent e) {}
			public void menuSelected(MenuEvent e) {
				JMenu menu = (JMenu) e.getSource();
				menu.removeAll();
				
				String[] recentFiles = mainWindow.getRecentFiles();
				for (int i = 0; i < recentFiles.length; i++) {
					String f = recentFiles[i];
					if (f != null && !f.equals("")) {
						JMenuItem mi = new JMenuItem(f);
						mi.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								mainWindow.doOpen(f);
							}
						});
						menu.add(mi);
					}
				}
			}
		});
		formula.add(formulaOpenRecent);
        
        formula.addSeparator();
        
        JMenuItem formulaSave = new JMenuItem("Save");
        formulaSave.setMnemonic('s');
        formulaSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        formulaSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindow.doSave();
            }

        });
        formula.add(formulaSave);
        JMenuItem formulaSaveAs = new JMenuItem("Save As");
        formulaSaveAs.setMnemonic('a');
        formulaSaveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindow.doSaveAs();
            }

        });
        formula.add(formulaSaveAs);
        
        formula.addSeparator();
        
        JMenuItem formulaBuildBytecode = new JMenuItem("Build Bytecode (-> .mbc)");
        formulaBuildBytecode.setMnemonic('b');
        formulaBuildBytecode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK));
        formulaBuildBytecode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindow.doBuildBytecode();
            }

        });
        formula.add(formulaBuildBytecode);
        
        JMenuItem formulaBuildJSON = new JMenuItem("Build JSON (-> .mbc.json)");
        formulaBuildJSON.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindow.doBuildJSON();
            }

        });
        formula.add(formulaBuildJSON);
        
        JMenuItem formulaBuildCSV = new JMenuItem("Build CSV (-> .mbc.csv)");
        formulaBuildCSV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindow.doBuildCSV();
            }

        });
        formula.add(formulaBuildCSV);
        
        formula.addSeparator();
        
        JMenuItem formulaClose = new JMenuItem("Close Window");
        formulaClose.setMnemonic('c');
        formulaClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
        formulaClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindow.close();
            }

        });
        formula.add(formulaClose);

        JMenu edit = new JMenu("Edit");
        
        JMenuItem editCut = new JMenuItem(new DefaultEditorKit.CutAction());
        editCut.setText("Cut");
        editCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        edit.add(editCut);
        JMenuItem editCopy = new JMenuItem(new DefaultEditorKit.CopyAction());
        editCopy.setText("Copy");
        editCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        edit.add(editCopy);
        JMenuItem editPaste = new JMenuItem(new DefaultEditorKit.PasteAction());
        editPaste.setText("Paste");
        editPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        edit.add(editPaste);

		/*
        JMenu language = new JMenu("Language");

        JMenu view = new JMenu("_View");
        view.setMnemonic('v');
        */
		/*
		JMenu viewLookAndFeel = new JMenu("Switch Theme");
		view.add(viewLookAndFeel);
		//viewLookAndFeel.add(createLookAndFeelMenuItem("SeaGlass", "com.seaglasslookandfeel.SeaGlassLookAndFeel", false));
		JMenu viewLookAndFeelJava = viewLookAndFeel;// = new JMenu("Java Themes");
		//viewLookAndFeel.add(viewLookAndFeelJava);
		viewLookAndFeelJava.add(createLookAndFeelMenuItem("Nimbus", "javax.swing.plaf.nimbus.NimbusLookAndFeel", false));
		viewLookAndFeelJava.add(createLookAndFeelMenuItem("Metal", "javax.swing.plaf.metal.MetalLookAndFeel", false));
		viewLookAndFeelJava.add(createLookAndFeelMenuItem("CDE/Motif", "com.sun.java.swing.plaf.motif.MotifLookAndFeel", false));
		viewLookAndFeelJava.add(createLookAndFeelMenuItem("GTK+", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel", false));
		*/
        JMenu help = new JMenu("Help");
        help.setMnemonic('h');
        JMenuItem helpFeedback = new JMenuItem("Feedback");
        helpFeedback.setMnemonic('f');
        helpFeedback.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AboutDialog about = new AboutDialog(mainWindow);
                about.select("Feedback");
                about.setVisible(true);
            }
        });
        help.add(helpFeedback);
        JMenuItem helpBugs = new JMenuItem("Known Bugs & Limitations");
        helpBugs.setMnemonic('b');
        helpBugs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AboutDialog about = new AboutDialog(mainWindow);
                about.select("Known Bugs & Limitations");
                about.setVisible(true);
            }
        });
        help.add(helpBugs);
        JMenuItem helpAbout = new JMenuItem("About " + Main.APP_NAME_SHORT);
        helpAbout.setMnemonic('a');
        helpAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AboutDialog(mainWindow).setVisible(true);
            }
        });
        help.add(helpAbout);

        add(formula);
        add(edit);
        //add(language);
        //add(view);
        add(Box.createHorizontalGlue());
        add(help);
    }
}

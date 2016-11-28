/* No copyright. No warranty. No code. USE AT YOUR OWN RISK.
 * Created by zak on 17/03/15. It was a good day.
 */

package org.mettascript.editor.swing;

import org.mettascript.editor.Document;

import javax.swing.*;
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
        JMenu formula = new JMenu("_Formula");
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
        JMenuItem formulaOpen = new JMenuItem("Open");
        formulaOpen.setMnemonic('o');
        formulaOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        formulaOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindow.doOpen();
            }
        });
        formula.add(formulaOpen);
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
        JMenuItem formulaQuickBuild = new JMenuItem("Quick Build (-> .mbc)");
        formulaQuickBuild.setMnemonic('q');
        formulaQuickBuild.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindow.doQuickBuild();
            }

        });
        formula.add(formulaQuickBuild);

        JMenu edit = new JMenu("_Edit");

        JMenu language = new JMenu("Language");

        JMenu view = new JMenu("_View");
        view.setMnemonic('v');
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
        JMenu help = new JMenu("_Help");
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
        add(language);
        add(view);
        add(Box.createHorizontalGlue());
        add(help);
    }
}

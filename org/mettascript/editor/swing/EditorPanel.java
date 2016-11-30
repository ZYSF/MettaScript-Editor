/* No copyright, no warranty, only code.
 * This file was created on 17/03/15. It was a good day.
 */

package org.mettascript.editor.swing;

import com.alee.extended.progress.WebProgressOverlay;
import com.alee.extended.statusbar.WebStatusBar;
import com.alee.laf.button.WebButton;
import org.fife.ui.rsyntaxtextarea.*;
import org.mettascript.bytecode.BytecodeFile;
import org.mettascript.bytecode.CompilationException;
import org.mettascript.editor.Document;
import org.mettascript.export.java.SimpleJavaOutput;
import org.mettascript.parser.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class EditorPanel extends JPanel {

    public enum SideView {
        NONE,
        TOKENS,
        GROUPS,
        BINOPS,
        BYTECODE,
        BYTECODE_JSON,
        BYTECODE_CSV,
        BYTECODE_CSV_SIGNED,
        BYTECODE_CSV_HEX,
        JAVA_EXPRESSION,
        JAVA_CLASS
    }

    private TabShell.Tab tab;
    private Document document;
    private String previousTitle = "";

    private JSplitPane innerSplitPane;
    private JSplitPane outerSplitPane;
    private int innerDividerSize = 8;
    private double innerDividerLocation = 0.66;
    private int outerDividerSize = 8;
    private double outerDividerLocation = 0.25;

    private RSyntaxTextArea textPane;
    private JScrollPane textPaneScroller;
    private boolean documentChangedSinceUpdate;
    boolean documentChangedSinceSave;

    private RSyntaxTextArea sideViewPane;
    private JScrollPane sideViewScroller;
    private SideView sideViewType = SideView.NONE;

    private OutputPanel outputPanel;

    private WebStatusBar controlPanel;
    private boolean calculateOnEdit = true;
    private boolean awaitingExplicitCalculation = false;

    private WebProgressOverlay calculationProgressOverlay;

    public EditorPanel(TabShell.Tab tab) {
        super(new BorderLayout());
        this.tab = tab;
        this.document = tab.getDocument();
        createInterface();
    }

    private void createInterface() {
        createContent();
        createOutputPanel();
        createSideView();
        createControlPanel();
        createLayoutComponents();
    }

    private void createContent() {
        textPane = new RSyntaxTextArea();
        textPane.setEditable(true);
        textPane.setDocument(new RSyntaxDocument(new MettaTokenMakerFactory(), "text/mettascript"));
        textPane.setText(getDocument().getSource());
        textPane.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                documentChangedSinceUpdate = documentChangedSinceSave = true;
                updateTitle();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentChangedSinceUpdate = documentChangedSinceSave = true;
                updateTitle();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentChangedSinceUpdate = documentChangedSinceSave = true;
                updateTitle();
            }
        });
        textPane.setBorder(BorderFactory.createEmptyBorder());
        updateTitle();
    }

    private void createOutputPanel() {
        outputPanel = new OutputPanel(this);
    }

    private void createSideView() {
        sideViewPane = new RSyntaxTextArea();
        sideViewPane.setEditable(false);
    }

    private void createControlPanel() {
        controlPanel = new WebStatusBar();
        JPanel controlPanelLeft = new JPanel();
        JPanel controlPanelMiddle = new JPanel();
        JPanel controlPanelRight = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanelLeft.setLayout(new BorderLayout());
        //controlPanelMiddle.setLayout(new BorderLayout());
        controlPanelRight.setLayout(new BorderLayout());

        controlPanelLeft.add(new JLabel(Main.APP_NAME_SHORT + " " + Main.APP_VERSION), BorderLayout.CENTER);

        calculationProgressOverlay = new WebProgressOverlay();
        calculationProgressOverlay.setConsumeEvents(false);

        final JCheckBox showConsoleCheckBox = new JCheckBox("Show Calculator",true);
        final WebButton refreshNowButton = new WebButton("Calculate Now");
        refreshNowButton.setRound(9);
        calculationProgressOverlay.setComponent(refreshNowButton);
        final JCheckBox refreshOnEditCheckBox = new JCheckBox("Calculate On Edit",true);
        showConsoleCheckBox.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (showConsoleCheckBox.isSelected()) {
                    outputPanel.setVisible(true);
                    outerSplitPane.setDividerSize(outerDividerSize);
                    outerSplitPane.setDividerLocation(outerDividerLocation);
                    if (!refreshOnEditCheckBox.isSelected()) {
                        refreshNowButton.setEnabled(true);
                    }
                    refreshOnEditCheckBox.setEnabled(true);
                } else {
                    outputPanel.setVisible(false);
                    outerDividerSize = outerSplitPane.getDividerSize();
                    outerDividerLocation = ((double)outerSplitPane.getDividerLocation())/((double)outerSplitPane.getHeight());
                    outerSplitPane.setDividerSize(0);
                    refreshNowButton.setEnabled(false);
                    refreshOnEditCheckBox.setEnabled(false);
                }
            }});
        refreshNowButton.setEnabled(!calculateOnEdit);
        refreshNowButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                awaitingExplicitCalculation = true;
                documentChangedSinceUpdate = true;
            }
        });
        refreshOnEditCheckBox.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if (refreshOnEditCheckBox.isSelected()) {
                    refreshNowButton.setEnabled(false);
                    calculateOnEdit = true;
                } else {
                    refreshNowButton.setEnabled(true);
                    calculateOnEdit = false;
                }
            }
        });
        refreshOnEditCheckBox.setSelected(calculateOnEdit);
        controlPanelMiddle.add(showConsoleCheckBox, BorderLayout.WEST);
        controlPanelMiddle.add(refreshNowButton, BorderLayout.CENTER);
        controlPanelMiddle.add(refreshOnEditCheckBox, BorderLayout.EAST);

        final JComboBox<String> sideViewComboBox = new JComboBox<String>();
        sideViewComboBox.addItem("Tokens");
        sideViewComboBox.addItem("Tokens (Grouped)");
        sideViewComboBox.addItem("Binary Operators");
        sideViewComboBox.addItem("Bytecode");
        sideViewComboBox.addItem("Bytecode (JSON)");
        sideViewComboBox.addItem("Bytecode (CSV, Unsigned)");
        sideViewComboBox.addItem("Bytecode (CSV, Signed)");
        sideViewComboBox.addItem("Bytecode (CSV, Hex)");
        sideViewComboBox.addItem("Java Expression");
        sideViewComboBox.addItem("Java Class");
        sideViewComboBox.addItem("No Side View");
        sideViewComboBox.setSelectedItem("No Side View");
        sideViewComboBox.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SideView type;
                switch ((String)sideViewComboBox.getSelectedItem()) {
                    case "Tokens":
                        type = SideView.TOKENS;
                        break;
                    case "Tokens (Grouped)":
                        type = SideView.GROUPS;
                        break;
                    case "Binary Operators":
                        type = SideView.BINOPS;
                        break;
                    case "Bytecode":
                        type = SideView.BYTECODE;
                        break;
                    case "Bytecode (JSON)":
                        type = SideView.BYTECODE_JSON;
                        break;
                    case "Bytecode (CSV, Unsigned)":
                        type = SideView.BYTECODE_CSV;
                        break;
                    case "Bytecode (CSV, Signed)":
                        type = SideView.BYTECODE_CSV_SIGNED;
                        break;
                    case "Bytecode (CSV, Hex)":
                        type = SideView.BYTECODE_CSV_HEX;
                        break;
                    case "Java Expression":
                        type = SideView.JAVA_EXPRESSION;
                        break;
                    case "Java Class":
                        type = SideView.JAVA_CLASS;
                        break;
                    case "No Side View":
                        type = SideView.NONE;
                        break;
                    default:
                        throw new Error("WTF is " + sideViewComboBox.getSelectedItem());
                }

                refreshSideView(type);
            }});

        sideViewComboBox.setPreferredSize(new Dimension(190,15));
        controlPanelRight.add(sideViewComboBox, BorderLayout.EAST);

        controlPanel.add(controlPanelLeft, BorderLayout.WEST);
        controlPanel.add(controlPanelMiddle, BorderLayout.CENTER);
        controlPanel.add(controlPanelRight, BorderLayout.EAST);
    }

    private void createLayoutComponents() {
        outerSplitPane = new JSplitPane();
        outerSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        outerSplitPane.setResizeWeight(0.25);
        innerSplitPane = new JSplitPane();
        innerSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        innerDividerSize = innerSplitPane.getDividerSize();
        innerSplitPane.setDividerSize(0);
        innerSplitPane.setResizeWeight(0.66);

        textPaneScroller = new JScrollPane(textPane);
        textPaneScroller.setBorder(BorderFactory.createEmptyBorder());
        sideViewScroller = new JScrollPane(sideViewPane);
        sideViewScroller.setBorder(BorderFactory.createEmptyBorder());
        sideViewScroller.setVisible(false);

        innerSplitPane.setLeftComponent(textPaneScroller);
        innerSplitPane.setRightComponent(sideViewScroller);
        outerSplitPane.setTopComponent(innerSplitPane);
        outerSplitPane.setBottomComponent(outputPanel);

        add(outerSplitPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.PAGE_END);
    }

    private void refreshSideView(SideView type) {
        System.out.println("Refreshing side view (" + type + ")...");

        if (sideViewType == SideView.NONE && type != SideView.NONE) {
            System.out.println("Showing side view...");
            sideViewScroller.setVisible(true);
            innerSplitPane.setDividerLocation(innerDividerLocation);
            innerSplitPane.setDividerSize(innerDividerSize);
            sideViewPane.setText("Reformatting...");
            documentChangedSinceUpdate = true;
        } else if (type == SideView.NONE && sideViewType != SideView.NONE) {
            System.out.println("Hiding side view...");
            innerDividerLocation =  ((double)innerSplitPane.getDividerLocation())/((double)innerSplitPane.getWidth());
            innerDividerSize = innerSplitPane.getDividerSize();
            innerSplitPane.setDividerSize(0);
            sideViewScroller.setVisible(false);
        }

        if (type != SideView.NONE) {
            documentChangedSinceUpdate = true;
        }

        sideViewType = type;
    }

    void onTimerTick() {
        if (documentChangedSinceUpdate) {
            documentChangedSinceUpdate = false;

            System.out.println("Resetting document source...");

            getDocument().setSource(textPane.getText());

            if (getDocument().getSource().length() > 500000) {
                System.err.println("WARNING: The update mechanism isn't designed to handle files this large!");
            }

            System.out.println("Updating calculation...");
            if (calculateOnEdit || awaitingExplicitCalculation) {
                outputPanel.refresh(false);
                awaitingExplicitCalculation = false;
						/*
						DocumentInterpreter interpreter = new DocumentInterpreter(getDocument(), true){
							@Override
							protected void onResult(Value value) {
								rawTextResult = value.toString();
								awaitingExplicitCalculation = false;
							}

							@Override
							protected void onError(Object e) {
								if (e instanceof Throwable) {
									Throwable t = (Throwable)e;
									ByteArrayOutputStream baos = new ByteArrayOutputStream();
									t.printStackTrace(new PrintStream(baos));
									e = baos.toString();
								}
								rawTextResult = "Error:\n" + e;
								awaitingExplicitCalculation = false;
							}
						};
						interpreter.interpret();
						*/
            }

            System.out.println("Updating side view...");
            try {
                FormulaParser parser = getDocument().getParser();
                switch (sideViewType) {
                    case TOKENS:
                        String txt = "";
                        for (org.mettascript.parser.Token t : parser.getTokens(false)) {
                            txt += t.type + " (line " + t.firstSymbol.line + ", column " + t.firstSymbol.column + "):\n  '" + t.toString() + "'\n";
                        }
                        sideViewPane.setText(txt);
                        ((RSyntaxDocument) sideViewPane.getDocument()).setSyntaxStyle("text/plain");
                        break;
                    case GROUPS:
                        sideViewPane.setText(parser.getGroupedTokens().toString());
                        ((RSyntaxDocument) sideViewPane.getDocument()).setSyntaxStyle("text/plain");
                        break;
                    case BINOPS:
                        sideViewPane.setText(parser.getOperation().toString());
                        ((RSyntaxDocument) sideViewPane.getDocument()).setSyntaxStyle("text/plain");
                        break;
                    case BYTECODE:
                    case BYTECODE_JSON:
                    case BYTECODE_CSV:
                    case BYTECODE_CSV_SIGNED:
                    case BYTECODE_CSV_HEX:
                        try {
                            BytecodeFile bcf = new BytecodeFile(parser);
                            String s = null;
                            switch (sideViewType) {
                                case BYTECODE:
                                    ((RSyntaxDocument) sideViewPane.getDocument()).setSyntaxStyle("text/plain");
                                    s = bcf.printToString(false);
                                    break;
                                case BYTECODE_JSON:
                                    ((RSyntaxDocument) sideViewPane.getDocument()).setSyntaxStyle("text/json");
                                    s = bcf.printToString(true);
                                    break;
                                case BYTECODE_CSV:
									((RSyntaxDocument) sideViewPane.getDocument()).setSyntaxStyle("text/java");
                                    s = bcf.printBytesToString(false, false, false, 10);
                                    break;
                                case BYTECODE_CSV_SIGNED:
									((RSyntaxDocument) sideViewPane.getDocument()).setSyntaxStyle("text/java");
                                    s = bcf.printBytesToString(true, false, false, 10);
                                    break;
                                case BYTECODE_CSV_HEX:
									((RSyntaxDocument) sideViewPane.getDocument()).setSyntaxStyle("text/java");
                                    s = bcf.printBytesToString(false, true, true, 10);
                                    break;
                            }
                            sideViewPane.setText(s);
                        } catch (CompilationException ce) {
                            sideViewPane.setText("Error:\n" + ce.getMessage());
                            ((RSyntaxDocument) sideViewPane.getDocument()).setSyntaxStyle("text/plain");
                        }
                        break;
                    case JAVA_EXPRESSION:
                    case JAVA_CLASS:
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        PrintStream ps = new PrintStream(baos);
                        try {
                            new SimpleJavaOutput(
                                    parser,
                                    ps,
                                    "mettascript",
                                    getDocument().getName().replace(".metta", ""),
                                    false,
                                    sideViewType == SideView.JAVA_EXPRESSION);
                            sideViewPane.setText(baos.toString());
                            ((RSyntaxDocument) sideViewPane.getDocument()).setSyntaxStyle("text/java");
                        } catch (IOException e1) {
                            sideViewPane.setText(e1.toString());
                        }
                        break;
                    default:
                        sideViewPane.setText(sideViewType + ", eh?");
                        ((RSyntaxDocument) sideViewPane.getDocument()).setSyntaxStyle("text/java");
                }
            } catch (Throwable t) {
                t.printStackTrace();
                sideViewPane.setText("Error: (Internal error, likely not your fault):\n" + t);
                ((RSyntaxDocument) sideViewPane.getDocument()).setSyntaxStyle("text/plain");
            }
            sideViewPane.setCaretPosition(0);
        }
    }

    void updateTitle() {
        String newTitle = getDocument().getName().replace(".metta", "") +
                (documentChangedSinceSave ?
                        " (Unsaved!)" : (" - " + Main.APP_NAME));
        if (!newTitle.equals(previousTitle)) {
            previousTitle = newTitle;
            setTitle(newTitle);
        }
    }

    private void setTitle(String title) {
        tab.setTitle(title);
    }

    public Document getDocument() {
        return document;
    }
}

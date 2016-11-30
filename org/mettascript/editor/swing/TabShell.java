/* No copyright. No warranty. No code. USE AT YOUR OWN RISK.
 * Created by zak on 18/03/15. It was a good day.
 */

package org.mettascript.editor.swing;

import com.alee.extended.tab.*;
import org.mettascript.editor.Document;

import javax.swing.*;
import java.awt.*;

public class TabShell extends JPanel {
    private MainWindow mainWindow;
    private Tab singleTab;
    private WebDocumentPane<Tab> documentPane;

    private DocumentListener<Tab> tabListener = new DocumentListener<Tab>() {
        @Override
        public void opened(Tab tab, PaneData<Tab> paneData, int i) {

        }

        @Override
        public boolean closing(Tab tab, PaneData<Tab> paneData, int i) {
            return true;
        }

        @Override
        public void closed(Tab tab, PaneData<Tab> paneData, int i) {
            if (documentPane.getDocumentsCount() == 1) {
                switchToSingleTabMode(documentPane.getSelectedDocument());
            }
        }
    };

    public static class Tab extends DocumentData {
        private TabShell tabShell;
        private Document document;

        public Tab(Document document) {
            super(getIdForDocument(document), document.getName(), null);
            this.document = document;
            setComponent(new EditorPanel(this));
        }

        static String getIdForDocument(Document document) {
            if (document.getName() == null || document.getName().equals("") || document.getName().toLowerCase().startsWith("untitled")) {
                return "untitled-" + document.hashCode();
            } else {
                return document.getName();
            }
        }

        @Override
        public void setTitle(String title) {
            System.out.println("Setting tab title to '" + title + "'...");
            super.setTitle(title);
            if (tabShell != null) {
                tabShell.updateTitle(this);
            }
        }

        @Override
        public String getActualTitle() {
            return getTitle();
        }

        public TabShell getTabShell() {
            return tabShell;
        }

        void setTabShell(TabShell shell) {
            tabShell = shell;
        }

        public Document getDocument() {
            return document;
        }
    }

    private void updateTitle(Tab tab) {
        if (documentPane == null && singleTab != null && singleTab.getId().equals(tab.getId())) {
            mainWindow.setTitle(Main.APP_NAME + " - " + singleTab.getTitle());
        } else if (documentPane != null) {
            // TODO: Why doesn't this work? Why are Swing/WebLAF tabs so damn broken?
            documentPane.getPane(tab).getTabbedPane().setTabComponentAt(documentPane.getPane(tab).indexOf(tab), new JLabel(tab.getTitle()));
            if (tab == getSelectedDocument()) {
            	mainWindow.setTitle(Main.APP_NAME + " - " + tab.getTitle() + " (multiple tabs)");
            }
        }
    }

    private void closeDocument(Tab tab) {
        if (documentPane != null) {
            documentPane.closeDocument(tab);
        } else if (singleTab.getId().equals(tab.getId())) {
            singleTab = null;
            singleTab.setTabShell(null);
            remove(singleTab.getComponent());
            if (getRootPane() != null) {
                getRootPane().doLayout();
            }
        }
    }

    public TabShell(MainWindow mainWindow, Document document) {
        this(mainWindow, new Tab(document));
    }

    public TabShell(MainWindow mainWindow, Tab tab) {
        super(new BorderLayout());

        this.mainWindow = mainWindow;
        switchToSingleTabMode(tab);
    }

    public void openDocument(Document document) {
        openDocument(new Tab(document));
    }

    public void openDocument(Tab tab) {
        boolean actuallyOpened = false;
        if (documentPane == null && singleTab == null) {
            switchToSingleTabMode(tab);
            actuallyOpened = true;
        } else if (documentPane == null) {
            if (singleTab.getId().equals(tab.getId())) {
                /* No action. */
            } else {
                switchToDocumentPaneMode();
                documentPane.openDocument(tab);
                actuallyOpened = true;
            }
        } else {
            int ndocs = documentPane.getDocumentsCount();
            documentPane.openDocument(tab);
            if (documentPane.getDocumentsCount() == ndocs + 1) {
                actuallyOpened = true;
            }
        }

        if (actuallyOpened) {
            tab.setTabShell(this);
        }
    }

    private void switchToSingleTabMode(Tab tab) {
        if (documentPane != null) {
            remove(documentPane);
            documentPane = null;
        } else if (singleTab != null) {
            remove(singleTab.getComponent());
        }
        singleTab = tab;
        add(singleTab.getComponent(), BorderLayout.CENTER);
        singleTab.getComponent().setVisible(true);
        if (getRootPane() != null) {
            getRootPane().invalidate();
        }
        updateTitle(singleTab);
    }

    private void switchToDocumentPaneMode() {
        documentPane = new WebDocumentPane<Tab>();
        documentPane.addDocumentListener(tabListener);
        if (singleTab != null) {
            documentPane.openDocument(singleTab);
            remove(singleTab.getComponent());
            singleTab = null;
        }
        add(documentPane, BorderLayout.CENTER);
        documentPane.setVisible(true);
        if (getRootPane() != null) {
            getRootPane().invalidate();
        }
        mainWindow.setTitle(Main.APP_NAME + " - (multiple tabs)");
    }

    public Tab getSelectedDocument() {
        if (documentPane == null) {
            return singleTab;
        } else {
            return documentPane.getSelectedDocument();
        }
    }
}

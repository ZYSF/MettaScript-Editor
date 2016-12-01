/* No copyright, no warranty, only code. 
 * This file was created on 8 Nov 2014. It was a good day.
 */
package org.mettascript.editor.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

/**
 *
 * @author Zak Fenton
 */
public class AboutDialog extends JDialog {
	
	RSyntaxTextArea textArea;
	private JComboBox<String> selector;
	
	public void select(String section) {
		if (!selector.getSelectedItem().equals(section)) {
			selector.setSelectedItem(section);
		}
		String text = "";
		
		switch (section) {
		case "General Information":
			text += Main.APP_NAME + " (" + Main.APP_NAME_SHORT + ") version " + Main.APP_VERSION + ".\n";
			text += "MettaScript's parser/compilers/runtimes/editor are written\n";
			text += "by Zak Fenton and made available under the following\n";
			text += "terms:\n\n";
			text += "  * No Copyright\n";
			text += "  * No Warranty\n";
			text += "  * Not Tested for Production Environments\n";
			text += "  * No Guarantee of Non-Infringement\n";
			text += "  * No Guarantee of Fitness For Use\n";
			text += "  * Use Only At Your Own Risk\n\n";
			text += "NOTE:\n";
			text += "This PRERELEASE ALPHA VERSION is likely packaged with\n";
			text += "OTHER INDIVIDUALLY LICENSED COMPONENTS. Use the combo box\n";
			text += "above this text to view these licenses individually.";
			break;
		case "Credits":
			text += "Written by Zak Fenton.\n";
			text += "Tested with help from Michael Brown.\n";
			text += "RSyntaxTextArea is used for text views, so a big thanks to it's\n";
			text += "developer(s).\n";
			text += "WebLAF is used for UI styling (and maybe some custom elements), so\n";
			text += "I'd also like to thank it's developer(s).\n";
			text += "Special thanks to Timothy Budd, Jack Crenshaw and Jef Raskin for\n";
			text += "their books on language/compiler/user interface design.\n";
			text += "Special thanks to Alan Kay, Niklaus Wirth and Charles H. Moore for\n";
			text += "pioneering modern user interface & language design.\n";
			text += "Special thanks to the developers (past and present) of the Java\n";
			text += "platform. You did well.\n";
			break;
		case "RSyntaxTextArea License":
			text += "Copyright (c) 2012, Robert Futrell\n";
			text += "All rights reserved.\n";
			text += "\n";
			text += "Redistribution and use in source and binary forms, with or without\n";
			text += "modification, are permitted provided that the following conditions are met:\n";
			text += "    * Redistributions of source code must retain the above copyright\n";
			text += "      notice, this list of conditions and the following disclaimer.\n";
			text += "    * Redistributions in binary form must reproduce the above copyright\n";
			text += "      notice, this list of conditions and the following disclaimer in the\n";
			text += "      documentation and/or other materials provided with the distribution.\n";
			text += "    * Neither the name of the author nor the names of its contributors may\n";
			text += "      be used to endorse or promote products derived from this software\n";
			text += "      without specific prior written permission.\n";
			text += "\n";
			text += "THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND\n";
			text += "ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED\n";
			text += "WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE\n";
			text += "DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY\n";
			text += "DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES\n";
			text += "(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;\n";
			text += "LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND\n";
			text += "ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\n";
			text += "(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS\n";
			text += "SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\n";
			break;
		case "WebLAF License":
			text += "This prerelease of MSE includes a non-commercial copy of WebLAF.\n";
			text += "WebLAF is licensed under the GPL v3, which is too long to repeat here at the moment.\n";
			text += "(This 'About' dialog is a bit of a hack right now... My bad. -Zak.)\n";
			text += "A copy of WebLAF's license may be included somewhere in the .jar file\n";
			text += "(see the 'Additional License Information' page in the dropdown above).\n";
			text += "Until I clear this up, please see the following URLs for details about WebLAF licensing:\n";
			text += "  http://weblookandfeel.com/licensing/\n";
			text += "  https://www.gnu.org/licenses/gpl-3.0.html\n";
			break;
		case "Additional License Information":
			text += "Additional license information is provided in the 'licenses'\n";
			text += "directory of the full MettaScript .jar file (use an archiving\n";
			text += "application to view them - .jar files are like .zip files).\n";
			text += "Licenses will be managed and presented better in a future release,\n";
			text += "but please contact the developer (see 'Feedback' above) if you\n";
			text += "feel there has been any breach of licensing or that due credit\n";
			text += "has not been given to the developers of any included software.\n";
			break;
		case "Feedback":
			text += "This is a PRERELEASE ALPHA VERSION. The whole point of\n";
			text += "this release is to obtain feedback. Please check if the\n";
			text += "problem you'd like to report is already listed under the.\n";
			text += "Known Bugs & Limitations section (above) before reporting.\n\n";
			text += "Email:\n";
			text += "  zak.fenton@outlook.com\n";
			break;
		case "Known Bugs & Limitations":
			text += "Language/Compiler/Runtime Bugs & Limitations:\n";
			text += " * There is only support for arbitrary-precision integers at\n";
			text += "   the moment. A full number stack is coming.\n";
			text += " * There is no support for quantity calculus (dealing\n";
			text += "   with units of measurement) at this stage. It will be\n";
			text += "   implemented as context-based replacement of unknown\n";
			text += "   values on return from function calls, so it will apply\n";
			text += "   so generally that even elephants can be used as units.\n";
			text += " * Error reporting is very poor, and basically consists of\n";
			text += "   either total ignorance or random Java exceptions. Rest\n";
			text += "   assured, the compiler will soon be rewritten with this\n";
			text += "   in mind.\n";
			text += " * The only fully supported backend at this stage is bytecode\n";
			text += "   which is interpreted by a virtual machine written in Java.\n";
			text += "   (An optimised C-based VM is in development.)\n";
			text += " * The Java-based interpreter is not well optimised.\n";
			text += " * The interpreter will kill the program after a certain number\n";
			text += "   of steps (the number is set in the Context object). This is to\n";
			text += "   say that the language is not Turing complete (by design). The\n";
			text += "   number may be extended to some point, and the calculation can\n";
			text += "   be resumed after it's killed, but naturally it must end at\n";
			text += "   some point.\n";
			text += " * Direct export to Java is working in a limited form, but\n";
			text += "   this backend will be replaced by one which covers Java,\n";
			text += "   C# and D (and perhaps Groovy, F#, etc.).\n";
			text += " * The codebase was written in an exploratory manner and would\n";
			text += "   not pass any reasonable test of code quality at this stage.\n";
			text += "   This doesn't concern me right now, when it's finished I'll\n";
			text += "   know what it does and therefore how to fix it.\n";
			text += "\n";
			text += "User Interface/Editor Bugs & Limitations:\n";
			text += " * There are still some issues with tabs & unsaved warnings.\n";
			text += " * If you close the editor without saving first, game over.\n";
			text += " * If you keep showing and hiding either the console or the\n";
			text += "   side-view, the position of the divider will drift slowly\n";
			text += "   towards the top or the left of the window.\n";
			text += " * There is no search feature. I'll do it when I decide on a UI.\n";
			text += " * The UI as a whole needs to be shifted around a little.\n";
			text += " * Text such as this needs better formatting.\n";
			break;
		case "G'day":
			text = "G'day cunt!\nHowya goin?\n\n(A traditional Australian greeting.)";
			break;
		default:
			text = (section == null ? "null" : section) + "???";
		}
		
		textArea.setText(text);
		textArea.setCaretPosition(0);
	}

	public AboutDialog(MainWindow owner) {
		super(owner, "About MettaScript", true);
		
		selector = new JComboBox<String>();
		selector.addItem("G'day");
		selector.addItem("General Information");
		selector.addItem("Credits");
		selector.addItem("Known Bugs & Limitations");
		selector.addItem("Feedback");
		selector.addItem("RSyntaxTextArea License");
		selector.addItem("WebLAF License");
		selector.addItem("Additional License Information");
		selector.setSelectedItem("General Information");
		selector.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				select((String)selector.getSelectedItem());
			}
		});
		add(selector, BorderLayout.PAGE_START);
		textArea = new RSyntaxTextArea();
		textArea.setEditable(false);
		add(new JScrollPane(textArea), BorderLayout.CENTER);
		
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				AboutDialog.this.dispose();
			}
		});
		add(closeButton, BorderLayout.PAGE_END);
		

		select("General Information");
		setMinimumSize(new Dimension(550,650));
	}

}

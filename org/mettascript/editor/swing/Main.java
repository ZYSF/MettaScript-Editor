/* No copyright, no warranty, only code. 
 * This file was created on 8 Nov 2014. It was a good day.
 */
package org.mettascript.editor.swing;

import javax.swing.SwingUtilities;

import com.alee.laf.WebLookAndFeel;

/**
 *
 * @author Zak Fenton
 */
public class Main {
	
	public static final String APP_NAME = "MettaScript Editor";
	public static final String APP_NAME_SHORT = "MSE";
	public static final String APP_VERSION = "0.0.5-alpha";

	public static void main (String[] args) {
		SwingUtilities.invokeLater(new Runnable(){
			@Override public void run () {
				WebLookAndFeel.install();
				
				MainWindow e = new MainWindow();
				e.setVisible(true);
			}
		});
	}

}

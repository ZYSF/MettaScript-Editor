/* No copyright, no warranty, only code. 
 * This file was created on 22 Nov 2014. It was a good day.
 */
package org.mettascript.editor.swing.backend.scenegraph;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import org.mettascript.editor.swing.backend.presentation.Presentation;

/**
 *
 * @author Zak Fenton
 */
public class SceneComponent extends JComponent {

	private Presentation presentation;
	
	public SceneComponent(Presentation presentation) {
		this.presentation = presentation;
	}

	@Override
	public void paint(Graphics graphics) {
		Graphics2D g2d = (Graphics2D)graphics;
		g2d.drawString("Foobar", 10, 10);
		super.paint(graphics);
	}
}

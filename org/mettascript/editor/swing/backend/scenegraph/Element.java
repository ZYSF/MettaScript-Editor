/* No copyright, no warranty, only code. 
 * This file was created on 23 Nov 2014. It was a good day.
 */
package org.mettascript.editor.swing.backend.scenegraph;

import java.math.BigInteger;
import java.util.ArrayList;

import org.mettascript.library.*;
import org.mettascript.runtime.*;

/**
 *
 * @author Zak Fenton
 */
@Exposed
public abstract class Element extends NativeObject {
	
	private ArrayList<Element> members = new ArrayList<>();

	@Exposed
	public Element(NativeLibrary library, Value constructorParameters) {
		super(library, constructorParameters);
		
		if (constructorParameters instanceof WithValue) {
			WithValue wv = (WithValue) constructorParameters;
			while (wv != null) {
				if (wv.getKey() instanceof OperatorValue) {
					initialiseProperty(((OperatorValue)wv.getKey()).getName("binary"), wv.getValue());
				}
				if (wv.getOriginal() instanceof WithValue) {
					wv = (WithValue) wv.getOriginal();
				} else {
					wv = null;
				}
			}
			
			wv = (WithValue) constructorParameters;
			BigInteger i = BigInteger.ONE;
			BigInteger l = wv.countElements();
			while (i.compareTo(l) <= 0) {
				Value v = wv._invoke("@", new IntegerValue(i));
				if (v != NOTHING && !(v instanceof Unknown)) {
					insertMember(toElement(v));
				}
				i = i.add(BigInteger.ONE);
			}
		}
	}

	@Exposed
	public int minimumWidthInPixels() {
		return 0;
	}
	
	@Exposed
	public int minimumHeightInPixels() {
		return 0;
	}
	
	protected final Element toElement(Value value) {
		return toElement(getLibrary().getBindingContext(), value);
	}
	
	public static final Element toElement(BindingContext bindingContext, Value value) {
		return new Paragraph(bindingContext.load(SceneLibrary.class, false), new TextValue(value.toString()));
	}
	
	protected void insertMember(Element element) {
		System.out.println("Inserting " + element + " into " + this);
		members.add(element);
	}
	
	protected void initialiseProperty(String binaryName, Value property) {
		throw new NativeLibraryError(this.getClass(), "No such property '" + binaryName + "'!");
	}
}

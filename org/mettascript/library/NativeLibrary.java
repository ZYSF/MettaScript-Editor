/* No copyright, no warranty, only code. 
 * This file was created on 23 Nov 2014. It was a good day.
 */
package org.mettascript.library;

import java.util.Collection;
import java.util.HashMap;

import org.mettascript.runtime.Value;
import org.mettascript.vm.ClosureValue;

/**
 *
 * @author Zak Fenton
 */
public abstract class NativeLibrary extends LibraryValue {
	private BindingContext bindingContext;
	private int numberApplied;
	private int numberTested;
	private boolean checkingOnly;
	
	private HashMap<String,Binding> bindings = new HashMap<String, Binding>();
	
	public class Binding {
		String[] keys;
		Value value;
		
		private Binding(String... keys) {
			this.keys = keys;
		}
		
		public void to(Value value) {
			this.value = value;
			numberTested += keys.length;
			if (checkingOnly) {
				for (String unknown: bindingContext.getClosure().block.getReferencedSlots()) {
					for (String key: keys) {
						if (unknown.equals(key)) {
							numberApplied++;
						}
					}
				}
			} else {
				for (String key: keys) {
					if (bindingContext.getClosure().setFactor(key, value)) {
						numberApplied++;
						System.err.println("Applied " + key + " to " + value);
					}
				}
			}
		}
	}

	public NativeLibrary(BindingContext bindingContext) {
		this.bindingContext = bindingContext;
	}
	
	public boolean looksApplicableGlobally() {
		numberApplied = 0;
		numberTested = 0;
		checkingOnly = true;
		bindAll();
		
		if (numberApplied >= thresholdForLookingApplicableGlobally()) {
			return true;
		} else {
			return false;
		}
	}
	
	protected int thresholdForLookingApplicableGlobally() {
		return 1;
	}
	
	protected final Binding bind(Collection<String> names) {
		String[] namesAsArray = new String[names.size()];
		namesAsArray = names.toArray(namesAsArray);
		return bind(namesAsArray);
	}
	
	protected final Binding bind(String... names) {
		return new Binding(names);
	}
	
	protected final void bind(Class<? extends NativeObject> type) {
		NativeTypeValue ntv = new NativeTypeValue(this, NativeTypeMapping.of(type));
		bind(ntv.getNames()).to(ntv);
	}
	
	public void initialise(boolean global) {
		numberApplied = 0;
		numberTested = 0;
		checkingOnly = false;
		bindAll();
	}
	
	public BindingContext getBindingContext() {
		return bindingContext;
	}
	
	protected abstract void bindAll();
}

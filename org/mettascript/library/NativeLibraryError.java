/* No copyright, no warranty, only code. 
 * This file was created on 23 Nov 2014. It was a good day.
 */
package org.mettascript.library;

/**
 *
 * @author Zak Fenton
 */
public class NativeLibraryError extends Error {
	
	private final NativeLibrary library;
	private final Class<? extends NativeObject> type;

	public NativeLibraryError(NativeLibrary library, String message) {
		super("In library " + library.getClass().getName() + ": " + message);
		this.library = library;
		type = null;
	}

	public NativeLibraryError(NativeLibrary library, String message, Throwable cause) {
		super("In library " + library.getClass().getName() + ": " + message, cause);
		this.library = library;
		type = null;
	}

	public NativeLibraryError(Class<? extends NativeObject> type, String message) {
		super("In type " + type.getName() + ": " + message);
		this.type = type;
		library = null;
	}
	
	public NativeLibraryError(Class<? extends NativeObject> type, String message, Throwable cause) {
		super("In type " + type.getName() + ": " + message, cause);
		this.type = type;
		library = null;
	}
	
	public Class<? extends NativeObject> getType() {
		return type;
	}
	
	public NativeLibrary getLibrary() {
		return library;
	}
}

/* No copyright, no warranty, only code. 
 * This file was created on 5 Dec 2014. It was a good day.
 */
package org.mettascript.editor.swing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 *
 * @author Zak Fenton
 */
public class ConfigurationFile {
	private File file;
	
	private HashMap<String,String> properties;
	
	public ConfigurationFile(String fileName) {
		file = new File(fileName);
	}
	
	public synchronized void loadDefaults() {
		properties = new HashMap<String,String>();
	}
	
	public synchronized void load() throws IOException {
		if (!file.exists()) {
			loadDefaults();
			return;
		}
		FileInputStream input = new FileInputStream(file);
		ObjectInputStream objectInput = new ObjectInputStream(input);
		try {
			properties = (HashMap<String,String>)objectInput.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException("This file (" + file.getName() + ") is corrupt or from a newer version!", e);
		} finally {
			objectInput.close();
			input.close();
		}
	}
	
	public synchronized void save() throws IOException {
		FileOutputStream output = new FileOutputStream(file);
		ObjectOutputStream objectOutput = new ObjectOutputStream(output);
		objectOutput.writeObject(properties);
		objectOutput.close();
		output.close();
	}
	
	public void put(String key, String value) {
		properties.put(key, value);
	}
	
	public String getOrPut(String key, String defaultValue) {
		if (properties.containsKey(key)) {
			return properties.get(key);
		} else {
			properties.put(key, defaultValue);
			return defaultValue;
		}
	}
	
	public String getString(String key) {
		return properties.get(key);
	}
}

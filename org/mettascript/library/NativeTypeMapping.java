/* No copyright, no warranty, only code. 
 * This file was created on 23 Nov 2014. It was a good day.
 */
package org.mettascript.library;

import java.util.*;
import java.lang.reflect.*;

import org.mettascript.runtime.Value;

/**
 *
 * @author Zak Fenton
 */
public class NativeTypeMapping {
	
	private HashSet<String> names = new HashSet();
	
	public abstract class MemberMapping {
		private Member member;
		
		private HashSet<String> names = new HashSet<String>();
		
		protected MemberMapping(Member member) {
			this.member = member;
		}
		
		public Member getMember() {
			return member;
		}
		
		protected void addName(String name) {
			names.add(name);
		}
		
		public boolean isDeclaredLocally() {
			return member.getDeclaringClass() == type;
		}
		
		public Collection<String> getNames() {
			return names;
		}
	}
	
	public class MethodMapping extends MemberMapping {

		private MethodMapping(Method method) {
			super(method);
			
			Exposed exposedAnnotation = method.getAnnotation(Exposed.class);
			
			if (exposedAnnotation == null) {
				throw new IllegalArgumentException("This method (" + method + ") isn't marked @Exposed!");
			}
			
			if (exposedAnnotation.value().length != 0) {
				for (String name: exposedAnnotation.value()) {
					addName(name);
				}
			} else {
				addName(method.getName());
			}
		}
		
		public Method getMethod() {
			return (Method)getMember();
		}
	}
	
	public class FieldMapping extends MemberMapping {
		
		private FieldMapping(Field field) {
			super(field);
			
			Exposed exposedAnnotation = field.getAnnotation(Exposed.class);
			
			if (exposedAnnotation == null) {
				throw new IllegalArgumentException("This field (" + field + ") doesn't carry the @Exposed annotation!");
			}
			
			if (exposedAnnotation.value().length != 0) {
				for (String name: exposedAnnotation.value()) {
					addName(name);
				}
			} else {
				addName(field.getName());
			}
		}
		
		public Field getField() {
			return (Field)getMember();
		}
	}
	
	private static final HashMap<Class<? extends NativeObject>, NativeTypeMapping> typeMaps = new HashMap<Class<? extends NativeObject>, NativeTypeMapping>();
	
	private final Class<? extends NativeObject> type;
	private final Constructor<? extends NativeObject> exposedConstructor;
	private final NativeTypeMapping superClassTypeMap;

	private final HashMap<String, MemberMapping> members = new HashMap<String, MemberMapping>();
	
	@SuppressWarnings("unchecked")
	
	private NativeTypeMapping(Class<? extends NativeObject> type) {
		
		this.type = type;
		
		if (type == NativeObject.class) {
			superClassTypeMap = null;
		} else {
			if (type.isAnnotationPresent(Exposed.class)) {
				if (type.getAnnotation(Exposed.class).value().length > 0) {
					for (String name: type.getAnnotation(Exposed.class).value()) {
						names.add(name);
					}
				} else {
					names.add(type.getSimpleName());
				}
			} else {
				throw new NativeLibraryError(type,
						"The type, as well as any exposed members and a "
						+ "suitable constructor, must be marked @Exposed.");
			}
			superClassTypeMap = NativeTypeMapping.of(
					(Class<? extends NativeObject>)type.getSuperclass());
		}
		
		Constructor<? extends NativeObject> constructor = null;
		try {
			try {
				constructor = type.getConstructor(
						NativeLibrary.class, Value.class);
				if (!constructor.isAnnotationPresent(Exposed.class)) {
					throw new NativeLibraryError(type,
							"A suitable constructor was found but it wasn't "
							+ "marked @Exposed.");
				}
			} catch (NoSuchMethodException e) {
				throw new NativeLibraryError(type,
						"No public @Exposed constructor accepting a library "
						+ "and a value as parameters was found!");
			}
		} catch (NativeLibraryError e) {
			if (isAbstract()) {
				/* The constructor needs to be set to null so that it isn't
				 * exposed.
				 */
				constructor = null;
			} else {
				throw e;
			}
		}
		exposedConstructor = constructor;
		
		for (Field f: type.getFields()) {
			if (f.isAnnotationPresent(Exposed.class)) {
				addMember(new FieldMapping(f));
			}
		}
		
		for (Method m: type.getMethods()) {
			if (m.isAnnotationPresent(Exposed.class)) {
				addMember(new MethodMapping(m));
			}
		}
	}
	
	private void addMember(MemberMapping mm) {
		for (String name: mm.getNames()) {
			if (!members.containsKey(name)) {
				members.put(name, mm);
			} else {
				throw new NativeLibraryError(type,
						"The name '" + name + "' is used by two different " 
						+ "members: '" + mm + "' and '" + members.get(name)
						+ "'");
			}
		}
	}
	
	public static NativeTypeMapping of(Class<? extends NativeObject> type) {
		synchronized (NativeTypeMapping.class) {
			if (typeMaps.containsKey(type)) {
				return typeMaps.get(type);
			} else {
				NativeTypeMapping tm = new NativeTypeMapping(type);
				typeMaps.put(type, tm);
				return tm;
			}
		}
	}
	
	/** Returns true if this represents a base type (of an exposed type
	 * hierarchy), which is currently only considered to be the case for
	 * AbstractNativeType itself.
	 * 
	 * @return True if this represents the/a base type, otherwise false.
	 */
	public boolean isBaseType() {
		return superClassTypeMap == null;
	}

	/** Returns true if this mapping represents an abstract type, which can't
	 * be instantiated as-is.
	 * 
	 * @return True if this represents an abstract type, otherwise false.
	 */
	public boolean isAbstract() {
		return (type.getModifiers() & Modifier.ABSTRACT) != 0;
	}
	
	/** Should always be true. Will be extended later to support interfaces,
	 * enumerations and perhaps even annotations and integration with modern
	 * Java features (lambda and many others).
	 * 
	 * @return True if this TypeMapping maps a Java class, otherwise false.
	 */
	public boolean mapsJavaClass() {
		return !type.isInterface() && !type.isAnnotation() && !type.isEnum();
	}
	
	/** Returns true if this class has a constructor which will be exposed to
	 * formulae.
	 * 
	 * @return True if a suitable constructor is present, otherwise false.
	 */
	public boolean hasExposedConstructor() {
		return exposedConstructor != null;
	}
	
	public NativeObject instantiate(NativeLibrary library, Value parameters) {
		try {
			return (NativeObject)exposedConstructor.newInstance(library, parameters);
		} catch (Exception e) {
			throw new NativeLibraryError(type, "There was a problem invoking the constructor!", e);
		}
	}

	public Collection<String> getNames() {
		return names;
	}
}

package io.github.afonsomatelias.Core.Base;

import static io.github.afonsomatelias.Helpers.Global.PRIMITIVES;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import io.github.afonsomatelias.Helpers.$$;

public abstract class TypeFactory {

	public static Object create(
		Class<?> clazz
	) {
		return create(clazz, null, null);
	}

	/**
	 * Tries to create a new instance of a provided class
	 * 
	 * @param clazz     		the type of the field to instantiate
	 * @param fieldName    		the name of the destination field
	 * @param fieldParentType   the class of the destination type
	 * @return the new instance of the destination type, or null if it fails
	 */
 	public static Object create(
		Class<?> clazz,
		String fieldName,
		Class<?> fieldParentType
	) {
		try {
			return handleClassCreation(clazz);
		} catch (Exception e) {
			String path = clazz.getSimpleName();

			if (fieldName == null || fieldParentType == null) {
				path = describe(clazz);
			} else {
				path = String.format("\n| class %s { \n| 👉 %s %s;\n| }", describe(fieldParentType), describe(clazz), fieldName);
			}

			$$.out("Error creating the destination path '"+ ( fieldName == null ? "unknown" : fieldName ) +"':" 
				+ path + "\nYou can try to intercept it or .skip(...) "+
				"in mapping options to ignore the field mapping. "+
				"\nException Message: " + e.getMessage() + "\n");
		}
		return null;
	}

	private static Object handleClassCreation(Class<?> clazz) throws Exception {
		// Check type before continue
		if (PRIMITIVES.contains(clazz))
			return null;

		Object newInstace = null;
		Constructor<?>[] constructors = clazz.getDeclaredConstructors();

		// Collect the messages in of any exception to be able to show to the user
		List<String> exceptions = new ArrayList<>();

		// 1. First Try using the constructor and newInstance method
		for (Constructor<?> constructor : constructors) {

			// Setting the constructor to accessible ctor
			constructor.setAccessible(true);

			try {
				// Try to create a new instance of the class
				newInstace = constructor.newInstance(
						new Object[constructor.getParameterCount()]);
			} catch (Exception e) {
				exceptions.add(e.getMessage());
			}

			// If if successeded
			if (newInstace != null)
				break;
		}

		// 2. Could not create a new instance with the constructor
		if (newInstace == null) {
			try {
				Constructor<?> constructor = clazz.getDeclaredConstructor();

				// Use traditional way
				newInstace = constructor.newInstance();
			} catch (Exception e) {
				exceptions.add(e.getMessage());
			}
		}

		if (!exceptions.isEmpty()) throw new Exception(String.join("\n", exceptions).trim());
		// 3. Future Impl: Creating an unknown object with all the fields of the class

		return newInstace;
	}

	static String describe(Class<?> type) {
		if (type == null) return "Type";
	    try {
			return type.isArray()
				? type.getComponentType().getSimpleName() + "[]"
				: type.getSimpleName();
		} catch (Exception e) {
			return type.getSimpleName();
		}
	}
}

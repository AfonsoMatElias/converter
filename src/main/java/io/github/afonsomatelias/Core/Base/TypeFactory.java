package io.github.afonsomatelias.Core.Base;

import static io.github.afonsomatelias.Helpers.Global.PRIMITIVES;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.afonsomatelias.Helpers.$$;

public abstract class TypeFactory {

	public static Object create(
		Class<?> clazz
	) {
		return create(clazz, clazz.getName(), clazz);
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

			final String cls = fieldParentType == null ? "[Class]" : fieldParentType.getName();
			final String clsFieldName = fieldName == null ? "[Field]" : fieldName;
			final String clsFieldType = clazz.getSimpleName();
			final String path = String.join(":", Arrays.asList(cls, "[" + clsFieldType + "]", clsFieldName ));

			$$.out("Error creating the destination type for " + path + ", try to intercept it or .skip(...) "+
				"in mapping options to ignore the field mapping. "+
				"\nException Details: " + e.getMessage() + "\n");
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

		// If any error, throw it
		if (!exceptions.isEmpty()) {
			throw new Exception(
					String.join(";\n", exceptions).trim());
		}

		// 2. Future Impl: Creating an unknown object with all the fields of the class
		return newInstace;
	}
}

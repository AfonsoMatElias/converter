package io.github.afonsomatelias.Helpers;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.afonsomatelias.Callback.ICallbacks.I1Fn;
import io.github.afonsomatelias.Callback.ICallbacks.ITypeResolver;

public class Global {
	
	/** Primitive mapper */
	public static final Map<Class<?>, Class<?>> PRIMITIVE_MAPPER = new HashMap<Class<?>, Class<?>>() {{
		put(Integer.class, int.class);
		put(Byte.class, byte.class);
		put(String.class, String.class);
		put(Character.class, char.class);
		put(Boolean.class, boolean.class);
		put(Double.class, double.class);
		put(Float.class, float.class);
		put(Long.class, long.class);
		put(Short.class, short.class);
		put(Void.class, void.class);

		put(int.class, Integer.class);
		put(byte.class, Byte.class);
		put(char.class, Character.class);
		put(boolean.class, Boolean.class);
		put(double.class, Double.class);
		put(float.class, Float.class);
		put(long.class, Long.class);
		put(short.class, Short.class);
		put(void.class, Void.class);
	}};

	/** Primitives */
	public static final Set<Class<?>> PRIMITIVES = new HashSet<Class<?>>() {{
		PRIMITIVE_MAPPER.forEach((key, value) -> add(value));
	}};

	/** Default types resolver */
	public static final Map<Class<?>, ITypeResolver> DEFAULT_TYPES_RESOLVER = new HashMap<Class<?>, ITypeResolver>() {{
		// keywords types
		put(int.class, (Object v) -> Integer.parseInt((String)v));
		put(byte.class, (Object v) -> Byte.parseByte((String)v));
		put(char.class, (Object v) ->  v.toString().charAt(0));
		put(boolean.class, (Object v) -> Boolean.parseBoolean((String)v));
		put(double.class, (Object v) -> Double.parseDouble((String)v));
		put(float.class, (Object v) -> Float.parseFloat((String)v));
		put(long.class, (Object v) -> Long.parseLong((String)v));
		put(short.class, (Object v) -> Short.parseShort((String)v));

		// class types
		put(Integer.class, (Object v) -> Integer.parseInt((String)v));
		put(BigDecimal.class, (Object v) -> new BigDecimal((String)v));
		put(Byte.class, (Object v) -> Byte.parseByte((String)v));
		put(Character.class, (Object v) ->  v.toString().charAt(0));
		put(Boolean.class, (Object v) -> Boolean.parseBoolean((String)v));
		put(Double.class, (Object v) -> Double.parseDouble((String)v));
		put(Float.class, (Object v) -> Float.parseFloat((String)v));
		put(Long.class, (Object v) -> Long.parseLong((String)v));
		put(Short.class, (Object v) -> Short.parseShort((String)v));

		// Extra
		put(LocalDate.class, (Object v) -> LocalDate.parse((String)v));
		put(LocalDateTime.class, (Object v) -> LocalDateTime.parse((String)v));
	}};

	
	/**
	 * Verifies if the provided object is an array or an instance of {@link List}.
	 * 
	 * @param input the object to be verified
	 * @return true if the object is an array or an instance of {@link List}, or false otherwise
	 */
	public static boolean isArray(Object input) {
		return (input != null) && (input.getClass().isArray() || (input instanceof List<?>));
	}

	
	/**
	 * Checks if all elements in the provided list satisfy a given condition.
	 * 
	 * @param <T>      the type of elements in the list
	 * @param source   the list of elements to be checked
	 * @param callback a callback that determines if an element matches the condition
	 * @return true if all elements satisfy the condition, false otherwise. Returns true if the source is null.
	 */
	public static <T> Boolean allMatch(
		List<T> source,
		I1Fn<T, Boolean> callback
	) {
		// If the source is null, return true
		if (source == null) return true;

		final List<Boolean> allMatching = new ArrayList<>();

		for (T t : source)
			allMatching.add(callback.call(t));

		return allMatching.isEmpty() ? false : allMatching.stream().allMatch(x -> x);
	};

	
	/**
	 * Retrieves the type of elements within a List or array field.
	 * 
	 * @param field the field from which to infer the element type
	 * @return the class type of the list or array elements
	 */
	public static Class<?> getListType(Field field) {
		Class<?> type = field.getType().getComponentType();

		if (type == null)
			type = (Class<?>) ((ParameterizedType) field.getGenericType())
					.getActualTypeArguments()[0];

		return type;
	};
}

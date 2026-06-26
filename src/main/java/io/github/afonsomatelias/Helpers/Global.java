package io.github.afonsomatelias.Helpers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
import io.github.afonsomatelias.Callback.ICallbacks.IFn;
import io.github.afonsomatelias.Callback.ICallbacks.ITypeResolver;
import io.github.afonsomatelias.Enums.CollectionTypeEnum;

public class Global {

	/** Primitive mapper */
	public static final Map<Class<?>, Class<?>> PRIMITIVE_MAPPER = new HashMap<>();

	/** Primitives */
	public static final Set<Class<?>> PRIMITIVES = new HashSet<>();

	/** Default types resolver */
	public static final Map<Class<?>, ITypeResolver> DEFAULT_TYPES_RESOLVER = new HashMap<>();

	static {
		PRIMITIVE_MAPPER.put(Integer.class, int.class);
		PRIMITIVE_MAPPER.put(Byte.class, byte.class);
		PRIMITIVE_MAPPER.put(String.class, String.class);
		PRIMITIVE_MAPPER.put(Character.class, char.class);
		PRIMITIVE_MAPPER.put(Boolean.class, boolean.class);
		PRIMITIVE_MAPPER.put(Double.class, double.class);
		PRIMITIVE_MAPPER.put(Float.class, float.class);
		PRIMITIVE_MAPPER.put(Long.class, long.class);
		PRIMITIVE_MAPPER.put(Short.class, short.class);
		PRIMITIVE_MAPPER.put(Void.class, void.class);

		PRIMITIVE_MAPPER.put(int.class, Integer.class);
		PRIMITIVE_MAPPER.put(byte.class, Byte.class);
		PRIMITIVE_MAPPER.put(char.class, Character.class);
		PRIMITIVE_MAPPER.put(boolean.class, Boolean.class);
		PRIMITIVE_MAPPER.put(double.class, Double.class);
		PRIMITIVE_MAPPER.put(float.class, Float.class);
		PRIMITIVE_MAPPER.put(long.class, Long.class);
		PRIMITIVE_MAPPER.put(short.class, Short.class);
		PRIMITIVE_MAPPER.put(void.class, Void.class);

		// keywords types
		DEFAULT_TYPES_RESOLVER.put(int.class, (Object v) -> Integer.parseInt((String) v));
		DEFAULT_TYPES_RESOLVER.put(byte.class, (Object v) -> Byte.parseByte((String) v));
		DEFAULT_TYPES_RESOLVER.put(char.class, (Object v) -> v.toString().charAt(0));
		DEFAULT_TYPES_RESOLVER.put(boolean.class, (Object v) -> Boolean.parseBoolean((String) v));
		DEFAULT_TYPES_RESOLVER.put(double.class, (Object v) -> Double.parseDouble((String) v));
		DEFAULT_TYPES_RESOLVER.put(float.class, (Object v) -> Float.parseFloat((String) v));
		DEFAULT_TYPES_RESOLVER.put(long.class, (Object v) -> Long.parseLong((String) v));
		DEFAULT_TYPES_RESOLVER.put(short.class, (Object v) -> Short.parseShort((String) v));

		// class types
		DEFAULT_TYPES_RESOLVER.put(Integer.class, (Object v) -> Integer.parseInt((String) v));
		DEFAULT_TYPES_RESOLVER.put(BigDecimal.class, (Object v) -> new BigDecimal((String) v));
		DEFAULT_TYPES_RESOLVER.put(Byte.class, (Object v) -> Byte.parseByte((String) v));
		DEFAULT_TYPES_RESOLVER.put(Character.class, (Object v) -> v.toString().charAt(0));
		DEFAULT_TYPES_RESOLVER.put(Boolean.class, (Object v) -> Boolean.parseBoolean((String) v));
		DEFAULT_TYPES_RESOLVER.put(Double.class, (Object v) -> Double.parseDouble((String) v));
		DEFAULT_TYPES_RESOLVER.put(Float.class, (Object v) -> Float.parseFloat((String) v));
		DEFAULT_TYPES_RESOLVER.put(Long.class, (Object v) -> Long.parseLong((String) v));
		DEFAULT_TYPES_RESOLVER.put(Short.class, (Object v) -> Short.parseShort((String) v));

		// Extra
		DEFAULT_TYPES_RESOLVER.put(LocalDate.class, (Object v) -> LocalDate.parse((String) v));
		DEFAULT_TYPES_RESOLVER.put(LocalDateTime.class, (Object v) -> LocalDateTime.parse((String) v));


		PRIMITIVE_MAPPER.forEach((key, value) -> PRIMITIVES.add(value));
	}

	/**
	 * Verifies if the provided object is an array or an instance of {@link List}.
	 * 
	 * @param input the object to be verified
	 * @return true if the object is an array or an instance of {@link List}, or
	 *         false otherwise
	 */
	public static boolean isArray(Object input) {
		return (input != null) && (input.getClass().isArray() || (input instanceof List<?>));
	}

	/**
	 * Checks if all elements in the provided list satisfy a given condition.
	 * 
	 * @param <T>      the type of elements in the list
	 * @param source   the list of elements to be checked
	 * @param callback a callback that determines if an element matches the
	 *                 condition
	 * @return true if all elements satisfy the condition, false otherwise. Returns
	 *         true if the source is null.
	 */
	public static <T> boolean allMatch(
			List<T> source,
			I1Fn<T, Boolean> callback) {
		// If the source is null, return true
		if (source == null)
			return true;

		List<Boolean> allMatching = new ArrayList<>();

		for (T t : source)
			allMatching.add(callback.call(t));

		return !allMatching.isEmpty() && allMatching.stream().allMatch(x -> x);
	}

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
	}

	public static CollectionTypeEnum getListEnumType(Class<?> cls) {
		return cls.getClass().getComponentType() == null ? CollectionTypeEnum.COLLECTION : CollectionTypeEnum.ARRAY;
	}

	public static String getAndResolveMethodName(Method method) {
		String methodName = method.getName();

		// If it begins with 'get'
		if (methodName.startsWith("get"))
			return methodName.substring(3).toLowerCase();

		// Otherwise, check it begins with 'is' and the return type is Boolean
		if (methodName.startsWith("is") && Boolean.class.equals(method.getReturnType()))
			return methodName.substring(2).toLowerCase();

		// Return the full name
		return methodName.toLowerCase();
	}

	public static String toUpper1Char(String text) {
		return (text.charAt(0) + "").toUpperCase() + text.substring(1);
	}

	public static String toLower1Char(String text) {
		return (text.charAt(0) + "").toUpperCase() + text.substring(1);
	}

	public static boolean isAnInterface(Class<?> clazz) {
		IFn<Boolean> hasOnlyGetter = () -> {
			// All methods inside must look like traditional read-only getters
			for (Method method : clazz.getDeclaredMethods()) {
				boolean hasParameters = method.getParameterCount() > 0;
				boolean returnsVoid = method.getReturnType() == void.class || 
					method.getReturnType() == Void.class;
				
				if (hasParameters || returnsVoid) {
					return false; // Projections shouldn't have setters or complex actions
				}
			}

			return true;
		};

		return clazz.isInterface() || hasOnlyGetter.call();
	}
}

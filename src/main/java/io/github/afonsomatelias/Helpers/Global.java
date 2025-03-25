package io.github.afonsomatelias.Helpers;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.afonsomatelias.Callback.ICallbacks.CallbackP1;

public class Global {
	
	public static final Map<Class<?>, Class<?>> PRIMITIVE_MAPPER = new HashMap<Class<?>, Class<?>>() {
		{
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
		}
	};

	public static final Set<Class<?>> PRIMITIVES = new HashSet<Class<?>>() {
		{
			PRIMITIVE_MAPPER.forEach((key, value) -> {
				add(key);
			});

			add(Integer.class);
			add(Byte.class);
			add(String.class);
			add(Character.class);
			add(Boolean.class);
			add(Double.class);
			add(Float.class);
			add(Long.class);
			add(Short.class);
			add(Void.class);

			add(int.class);
			add(byte.class);
			add(char.class);
			add(boolean.class);
			add(double.class);
			add(float.class);
			add(long.class);
			add(short.class);
			add(void.class);
		}
	};

	public static boolean isArray(Object input) {
		return (input != null) && (input.getClass().isArray() || (input instanceof List<?>));
	}

	public static <T> Boolean allMatch(
		List<T> source,
		CallbackP1<T, Boolean> callback
	) {
		// If the source is null, return true
		if (source == null) return true;

		final List<Boolean> allMatching = new ArrayList<>();

		for (T t : source)
			allMatching.add(callback.call(t));

		return allMatching.isEmpty() ? false : allMatching.stream().allMatch(x -> x);
	};

	public static Class<?> getListType(Field field) {
		Class<?> type = field.getType().getComponentType();

		if (type == null)
			type = (Class<?>) ((ParameterizedType) field.getGenericType())
					.getActualTypeArguments()[0];

		return type;
	};
}

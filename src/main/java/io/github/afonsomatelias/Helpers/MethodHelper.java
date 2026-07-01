package io.github.afonsomatelias.Helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import io.github.afonsomatelias.Callback.ICallbacks.I3Action;

@SuppressWarnings("unchecked")
public class MethodHelper {
    public static Map<String, Method> toMappedMethods(Class<?> clazz) {
        return toMappedMethods(clazz, null);
    }

    public static Map<String, Method> toMappedMethods(Class<?> clazz, I3Action<String, Method, Class<?>> forEachField) {
        return new HashMap<String, Method>() {{

            Method[] methods = clazz.getDeclaredMethods();
            
            for(Method method : methods) {
                put(method.getName(), method);

                if (forEachField != null)
                    forEachField.call(method.getName().substring(3), method, clazz);
            }
        }};

    }

    public static <T> T call(Object obj, String field, Object... params) {
        for (Method method : obj.getClass().getMethods()) {
            try {
                if (method.getName().equalsIgnoreCase(field)) {
                    method.setAccessible(true);

                    if (!method.getReturnType().equals(void.class) || !method.getReturnType().equals(Void.class))
                        return (T) method.invoke(obj, params);
                    
                    method.invoke(obj, params);
                }
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                $$.err(e);
            }
        }

        return null;
    }
}

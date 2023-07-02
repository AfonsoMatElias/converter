package io.java.Helpers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("unchecked")
public class FieldHelper {

    public interface IFieldkeyValue<TKey, TValue> {
        void run(TKey key, TValue value, Class<?> type);
    }    

    public static Field[] getAllDeclaredFields(Class<?> clazz) {
        if (clazz == null)
            return new Field[0];

        final List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        final Class<?> superClass = clazz.getSuperclass();
        final Field[] arrayOfFields = new Field[fields.size()];

        if (superClass == null || superClass.getName().equals("java.lang.Object"))
            return fields.toArray(arrayOfFields);

        final Field[] superClassFields = getAllDeclaredFields(superClass);
        fields.addAll(Arrays.asList(superClassFields));

        return fields.toArray(arrayOfFields);
    }

    public static Map<String, Field> getMappedFieldsFor(Class<?> clazz) {
        return new HashMap<String, Field>() {
            {
                final Field[] fields = getAllDeclaredFields(clazz);
                for (final Field field : fields) {
                    field.setAccessible(true);
                    put(field.getName(), field);
                }
            }
        };

    }

    public static <T> void fields(T obj, List<String> fields, IFieldkeyValue<String, Object> action) {
        Map<String, Field> objFields = getMappedFieldsFor(obj.getClass());

        // Looping all the methods
        for (String field : fields) {
            try {
                final String mField = (field.charAt(0) + "").toUpperCase() + field.substring(1, field.length());
                final Field fieldObj = objFields.getOrDefault(mField, null);

                if (fieldObj == null)
                    continue;

                fieldObj.setAccessible(true);

                // Running the action
                action.run(field, fieldObj.get(obj), fieldObj.getType());
            } catch (IllegalAccessException | IllegalArgumentException e) {
                Printer.err(e);
            }
        }
    }

    public static <T> void fields(T obj, IFieldkeyValue<String, Object> action) {
        Map<String, Field> objFields = getMappedFieldsFor(obj.getClass());
        for (Field field : objFields.values()) {
            try {
                field.setAccessible(true);
                action.run(field.getName(), field.get(obj), field.getType());
            } catch (IllegalAccessException | IllegalArgumentException e) {
                Printer.err(e);
                break;
            }
        }
    }

    public static <T> Object field(T obj, String field) {
        Map<String, Field> objFields = getMappedFieldsFor(obj.getClass());
        Field fieldObj = objFields.getOrDefault(field, null);

        if (fieldObj == null)
            return null;

        try {
            return fieldObj.get(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> void setField(T obj, String field, Object value) {
        if (value == null)
            return;

        Map<String, Field> objFields = getMappedFieldsFor(obj.getClass());
        Field fieldObj = objFields.getOrDefault(field, null);

        if (fieldObj == null)
            return;

        try {
            fieldObj.set(obj, value);
        } catch (Exception e) {
            Printer.err(e);
        }
    }

    public static <T> Boolean hasField(T obj, String field) {
        return getMappedFieldsFor(obj.getClass()).containsKey(field);
    }

    public static <T> Object getValue(T obj, String field) {
        if (obj == null)
            return null;

        if (obj instanceof LinkedHashMap) {
            return ((LinkedHashMap<String, Object>) obj).getOrDefault(field, null);
        } else {
            return field(obj, field);
        }
    }

    public static <T> T call(Object obj, String field, Object... params) {

        for (Method method : obj.getClass().getMethods()) {
            try {
                if (method.getName().equalsIgnoreCase(field)) {
                    method.setAccessible(true);
                    return (T) method.invoke(obj, params);
                }
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                Printer.err(e);
            }
        }

        return null;
    }

    /**
     * Loops ClassModel or LinkedHashMap model
     * 
     * @param model  the model to be looped
     * @param action the action that will be performed
     */
    public static void loop(Object model, IFieldkeyValue<String, Object> action) {

        if (action == null)
            return;

        if (model instanceof LinkedHashMap) {
            for (Entry<String, Object> entry : ((LinkedHashMap<String, Object>) model).entrySet()) {
                action.run(entry.getKey(), entry.getValue(), entry.getValue().getClass());
            }
        } else {
            for (Method method : model.getClass().getMethods()) {
                // If it does not begin with get, skip!
                if (!method.getName().startsWith("get"))
                    continue;

                try {
                    action.run(method.getName().substring("get".length()), method.invoke(model),
                            method.getReturnType());
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    Printer.err(e);
                }
            }
        }
    }
}

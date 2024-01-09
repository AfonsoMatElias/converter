package io.github.afonsomatelias.Helpers;

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
    
    public interface IFieldOptionCallback<TKey, TValue> {
        void run(TKey key, TValue value, Field field, Class<?> type);
    }

    public static Field[] toFields(Class<?> clazz) {
        if (clazz == null)
            return new Field[0];

        final List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        final Class<?> superClass = clazz.getSuperclass();

        if (superClass == null || superClass.getName().equals("java.lang.Object"))
            return fields.toArray(new Field[fields.size()]);

        final Field[] superClassFields = toFields(superClass);
        fields.addAll(Arrays.asList(superClassFields));

        return fields.toArray(new Field[fields.size()]);
    }

    public static Map<String, Field> toMappedFields(Class<?> clazz) {
        return toMappedFields(clazz, null);
    }

    public static Map<String, Field> toMappedFields(Class<?> clazz, IFieldkeyValue<String, Field> forEachField) {
        return new HashMap<String, Field>() {
            {
                final Field[] fields = toFields(clazz);
                for (final Field field : fields) {
                    field.setAccessible(true);
                    put(field.getName(), field);

                    if (forEachField != null)
                        forEachField.run(field.getName(), field, field.getType());
                }
            }
        };

    }

    public static <T> Map<String, Field> fields(T obj, List<String> fieldNames, IFieldkeyValue<String, Object> action) {
        Map<String, Field> fieldsToReturn = new HashMap<>();
        Map<String, Field> fields = toMappedFields(obj.getClass());

        // Looping all the fields
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            try {
                final String mFieldName = (fieldName.charAt(0) + "").toUpperCase() + fieldName.substring(1, fieldName.length());
                final Field field = fields.getOrDefault(mFieldName, null);

                if (field == null)
                    continue;

                field.setAccessible(true);

                fieldsToReturn.put(fieldName, field);

                // Running the action
                action.run(fieldName, field.get(obj), field.getType());
            } catch (IllegalAccessException | IllegalArgumentException e) {
                Printer.err(e);
            }
        }

        return fieldsToReturn;
    }

    public static <T> Map<String, Field> fields(T obj, IFieldkeyValue<String, Object> forEachField) {
        return toMappedFields(obj.getClass(), (name, field, type) -> {
            try {
                forEachField.run(name, field.get(obj), type);
            } catch (IllegalAccessException | IllegalArgumentException e) {
                Printer.err(e);
            }
        });
    }

    public static <T> Map<String, Field> fields(T obj, IFieldOptionCallback<String, Object> forEachField) {
        return toMappedFields(obj.getClass(), (name, field, type) -> {
            try {
                forEachField.run(name, field.get(obj), field, type);
            } catch (IllegalAccessException | IllegalArgumentException e) {
                Printer.err(e);
            }
        });
    }

    public static <T> Object field(T obj, String field) {
        Map<String, Field> objFields = toMappedFields(obj.getClass());
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

        Map<String, Field> objFields = toMappedFields(obj.getClass());
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
        return toMappedFields(obj.getClass()).containsKey(field);
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

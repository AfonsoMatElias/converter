package io.github.afonsomatelias.Helpers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldHelper {

    @FunctionalInterface
    public interface IFieldkeyValue<TKey, TValue> {
        void run(TKey key, TValue value, Class<?> type);
    }

    @FunctionalInterface
    public interface IFieldOptionCallback<TKey, TValue> {
        void run(TKey key, TValue value, Field field, Class<?> type);
    }

    public static Field[] toFields(Class<?> clazz) {
        if (clazz == null)
            return new Field[0];

        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        Class<?> superClass = clazz.getSuperclass();

        if (superClass == null || superClass.getName().equals("java.lang.Object"))
            return fields.toArray(new Field[fields.size()]);

        Field[] superClassFields = toFields(superClass);
        fields.addAll(Arrays.asList(superClassFields));

        return fields.toArray(new Field[fields.size()]);
    }

    public static Map<String, Field> toMappedFields(Class<?> clazz) {
        return toMappedFields(clazz, null);
    }

    public static Map<String, Field> toMappedFields(Class<?> clazz, IFieldkeyValue<String, Field> forEachField) {
        return new HashMap<String, Field>() {{
            Field[] fields = toFields(clazz);
            for (Field field : fields) {
                field.setAccessible(true);
                put(field.getName(), field);

                if (forEachField != null)
                    forEachField.run(field.getName(), field, field.getType());
            }
        }};

    }

    public static Object getValue(Object obj, String fieldName) {
        try {
            if (obj == null)
                return null;
    
            Field field = obj.getClass().getField(fieldName);
    
            if (field == null)
                return null;
    
            field.setAccessible(true);
    
            return field.get(obj);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static boolean setValue(Object obj, String fieldName, Object value) {
        try {
            if (obj == null)
                return false;
    
            Field field = obj.getClass().getField(fieldName);
    
            if (field == null)
                return false;
    
            field.setAccessible(true);
            field.set(obj, value);
    
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

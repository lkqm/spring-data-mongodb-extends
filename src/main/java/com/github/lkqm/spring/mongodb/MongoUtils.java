package com.github.lkqm.spring.mongodb;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.ClassUtils;

public class MongoUtils {

    /**
     * Convert uri to readPreference=SecondaryPreferred if replicaSet cluster.
     *
     * @param uri
     * @return return null if not replicaSet cluster uri.
     */
    public static String parseSecondaryPreferredUri(String uri) {
        String uriSecondary = null;
        if (uri.contains("replicaSet")) {
            // 自己初始化mongoTemplate,指向从库,不支持事务
            if (uri.contains("readPreference")) {
                String[] uriArray = uri.split("&");

                String value = null;
                for (String u : uriArray) {
                    if (u.contains("readPreference")) {
                        String[] a = u.split("=");
                        if (a.length > 1) {
                            value = a[1];
                        }
                        break;
                    }
                }
                if (value != null) {
                    uriSecondary = uri.replace(value, "secondaryPreferred");
                }
            } else {
                uriSecondary = uri + "&readPreference=secondaryPreferred";
            }
        }
        return uriSecondary;
    }

    /**
     * Returns update statement by specific entity that ignore null value.
     */
    public static Update parseUpdate(Object entity, String idFieldName) {
        Update update = new Update();
        Class<?> clazz = ClassUtils.getUserClass(entity);
        List<Field> fields = getPersistentFields(clazz);
        for (Field f : fields) {
            String name = f.getName();
            if (name.equals(idFieldName)) {
                continue;
            }
            try {
                PropertyDescriptor pd = new PropertyDescriptor(name, clazz);
                Method getMethod = pd.getReadMethod();
                Object value = getMethod.invoke(entity);
                if (value != null) {
                    update.set(getPersistentFieldName(f), value);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return update;
    }

    /**
     * Returns persistent name in mongodb.
     */
    public static String getPersistentFieldName(Field field) {
        org.springframework.data.mongodb.core.mapping.Field fieldNameAnnotation = field
                .getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class);
        if (fieldNameAnnotation != null) {
            String nameAnnotation = fieldNameAnnotation.value().trim();
            if (nameAnnotation.length() > 0) {
                return nameAnnotation;
            }
        }
        return field.getName();
    }

    /**
     * returns persistent fields.
     */
    private static List<Field> getPersistentFields(Class<?> clazz) {
        List<Field> fields = getAllPropertyFields(clazz);

        List<Field> result = new ArrayList<>(fields.size());
        for (Field field : fields) {
            Transient annotation = field.getAnnotation(Transient.class);
            if (annotation == null) {
                result.add(field);
            }
        }
        return result;
    }

    /**
     * returns all property fields includes supper class.
     */
    private static List<Field> getAllPropertyFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        for (Field f : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(f.getModifiers())) {
                fields.add(f);
            }
        }

        Class<?> superClazz = clazz.getSuperclass();
        while (superClazz != Object.class) {
            for (Field f : superClazz.getDeclaredFields()) {
                if (!Modifier.isStatic(f.getModifiers())) {
                    fields.add(f);
                }
            }
            superClazz = superClazz.getSuperclass();
        }
        return fields;
    }

}

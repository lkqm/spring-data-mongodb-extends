package com.github.lkqm.spring.mongodb;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * DefaultMongoRepositoryPlus
 */
public class MongoRepositoryPlusImpl<T, ID> extends SimpleMongoRepository<T, ID> implements
        MongoRepositoryPlus<T, ID> {

    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<T, ID> entityInformation;

    public MongoRepositoryPlusImpl(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoOperations = mongoOperations;
        this.entityInformation = metadata;
    }

    @Override
    public <S extends T> void update(S entity) {
        Object id = entityInformation.getRequiredId(entity);
        Assert.notNull(id, "Id can't be null value.");

        Query query = new Query(Criteria.where("_id").is(id));
        Update update = resolveUpdate(entity);
        mongoOperations.updateFirst(query, update, entityInformation.getCollectionName());
    }

    @Override
    public <S extends T> void update(Query query, S entity) {
        Update update = resolveUpdate(entity);
        update(query, update);
    }

    @Override
    public void update(Query query, Update update) {
        mongoOperations.updateMulti(query, update, entityInformation.getJavaType());
    }

    @Override
    public long count(Query query) {
        return mongoOperations.count(query, entityInformation.getJavaType());
    }

    @Override
    public List<T> findAll(Query query) {
        return mongoOperations.find(query, entityInformation.getJavaType());
    }

    @Override
    public Page<T> findAll(Query query, Pageable pageable) {
        long total = mongoOperations.count(query, entityInformation.getJavaType());
        List<T> list = mongoOperations.find(query.with(pageable), entityInformation.getJavaType());
        return new PageImpl<T>(list, pageable, total);
    }

    //-------------------------------------------
    // supports
    //-------------------------------------------

    private Update resolveUpdate(T entity) {
        String idAttribute = entityInformation.getIdAttribute();

        Update update = new Update();
        Class<?> clazz = ClassUtils.getUserClass(entity);
        List<Field> fields = getPersistentFields(clazz);
        for (Field f : fields) {
            String name = f.getName();
            if (name.equals(idAttribute)) {
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
     * returns persistent field name.
     */
    private static String getPersistentFieldName(Field field) {
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

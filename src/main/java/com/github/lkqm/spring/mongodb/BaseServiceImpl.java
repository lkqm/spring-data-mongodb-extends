package com.github.lkqm.spring.mongodb;

import com.mongodb.client.result.DeleteResult;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

public class BaseServiceImpl<T, ID> implements BaseService<T, ID> {

    @Autowired
    private MongoTemplate mongoTemplate;

    private Class<T> entityClass;

    public BaseServiceImpl() {
        init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public <S extends T> S save(S entity) {
        Assert.notNull(entity, "Entity must not be null.");
        return mongoTemplate.save(entity);
    }

    @Override
    public <S extends T> void update(S entity) {
        Assert.notNull(entity, "Entity must not be null.");
        MongoPersistentEntity<?> persistentEntity = mongoTemplate.getConverter().getMappingContext()
                .getRequiredPersistentEntity(entityClass);
        MongoPersistentProperty idProperty = persistentEntity.getRequiredIdProperty();

        Object id = persistentEntity.getIdentifierAccessor(entity).getRequiredIdentifier();
        Update update = MongoUtils.parseUpdate(entity, idProperty.getFieldName());

        mongoTemplate.updateFirst(getByIdQuery(id), update, entityClass);
    }

    @Override
    public long deleteById(ID id) {
        Assert.notNull(id, "Id must not be null.");
        DeleteResult deleteResult = mongoTemplate.remove(getByIdQuery(id), entityClass);
        return deleteResult.getDeletedCount();
    }

    @Override
    public long deleteById(Collection<ID> ids) {
        Assert.notNull(ids, "Ids must not be null.");
        DeleteResult deleteResult = mongoTemplate.remove(getByIdQuery(ids), entityClass);
        return deleteResult.getDeletedCount();
    }

    @Override
    public T findById(ID id) {
        Assert.notNull(id, "Id must not be null.");
        return mongoTemplate.findOne(getByIdQuery(id), entityClass);
    }

    @Override
    public List<T> findById(Collection<ID> ids) {
        Assert.notEmpty(ids, "Ids must not be null or empty.");
        return mongoTemplate.find(getByIdQuery(ids), entityClass);
    }

    @Override
    public long count() {
        return mongoTemplate.count(new Query(), entityClass);
    }

    private Query getByIdQuery(Object id) {
        return Query.query(Criteria.where("_id").is(id));
    }

    private Query getByIdQuery(Collection<?> ids) {
        return Query.query(Criteria.where("_id").in(ids));
    }
}

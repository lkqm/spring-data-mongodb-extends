package com.github.lkqm.spring.mongodb;

import java.util.List;
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
        Update update = MongoUtils.parseUpdate(entity, entityInformation.getIdAttribute());
        mongoOperations.updateFirst(query, update, entityInformation.getJavaType());
    }

    @Override
    public <S extends T> void update(Query query, S entity) {
        Update update = MongoUtils.parseUpdate(entity, entityInformation.getIdAttribute());
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

}

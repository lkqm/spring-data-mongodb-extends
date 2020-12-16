package com.github.lkqm.spring.mongodb;

import com.mongodb.client.result.UpdateResult;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * MongoRepositoryPlus
 */
@NoRepositoryBean
public interface MongoRepositoryPlus<T, ID> extends MongoRepository<T, ID> {

    void update(T entity);

    UpdateResult update(Query query, T entity);

    UpdateResult update(Query query, Update update);

    long count(Query query);

    List<T> findAll(Query query);

    Page<T> findAll(Query query, Pageable pageable);

}

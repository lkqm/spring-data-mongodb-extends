package com.github.lkqm.spring.mongodb;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * MongoRepositoryPlus
 */
@NoRepositoryBean
public interface MongoRepositoryPlus<T, ID> extends MongoRepository<T, ID> {

    /**
     * Updates the entity ignore null value fields by its id.
     * <p>
     * Notes: entity id value must not be null.
     */
    <S extends T> void update(S entity);

    /**
     * Updates the entity ignore null value fields by specific query.
     */
    <S extends T> void update(Query query, S entity);

    /**
     * Returns number of entities with query.
     */
    long count(Query query);

    /**
     * Retrieves entities with query.
     */
    List<T> findAll(Query query);

    /**
     * Retrieves entities with query.
     */
    Page<T> findAll(Query query, Pageable pageable);

}

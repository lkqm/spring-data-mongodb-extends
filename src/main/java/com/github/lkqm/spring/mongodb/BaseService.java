package com.github.lkqm.spring.mongodb;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * CRUD operations for specific type.
 */
public interface BaseService<T, ID> {

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed
     * the entity instance completely.
     */
    <S extends T> S save(S entity);

    /**
     * Updates the entity ignore null value fields by its id.
     * <p>
     * Notes: entity id value must not be null.
     */
    <S extends T> void update(S entity);

    /**
     * Deletes the entity with the give id.
     */
    long deleteById(ID id);

    /**
     * Deletes the entity with the give id.
     */
    long deleteById(Collection<ID> ids);

    /**
     * Retrieves an entity by its id.
     */
    T findById(ID id);

    /**
     * Retrieves an entity by its id.
     */
    List<T> findById(Collection<ID> ids);

    /**
     * Retrieves an entity by its id.
     */
    List<Map<ID, T>> findByIdMap(Collection<ID> ids);

    /**
     * Returns the number of entities available.
     */
    long count();

}

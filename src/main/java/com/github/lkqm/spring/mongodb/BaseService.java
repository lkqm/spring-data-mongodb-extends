package com.github.lkqm.spring.mongodb;

import java.util.List;

public interface BaseService<T, ID> {

    <S extends T> S save(S entity);

    long deleteById(ID id);

    T findById(ID id);

    List<T> findById(List<ID> ids);

    long count();

}

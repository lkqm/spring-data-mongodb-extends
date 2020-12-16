package com.github.lkqm.spring.mongodb.integration;

import com.github.lkqm.spring.mongodb.MongoRepositoryPlus;

// 测试接口实现
public interface UserRepository extends MongoRepositoryPlus<User, String> {

}

package com.github.lkqm.spring.mongodb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.lkqm.spring.mongodb.integration.AdminUser;
import com.github.lkqm.spring.mongodb.integration.User;
import com.github.lkqm.spring.mongodb.integration.UserRepository;
import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class MongoRepositoryPlusImplTest {

    @Resource
    private UserRepository userRepository;

    @Resource
    private MongoTemplate mongoTemplate;


    @Test
    void update() {
        // 初始数据
        User user = new User(null, "Jackson", 18);
        mongoTemplate.save(user);
        String id = user.getId();

        // 部分更新
        userRepository.update(new User(id, null, 19));
        User lastUser = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(id)), User.class);
        assertNotNull(lastUser, "数据应该存在");
        assertEquals(19, lastUser.getAge(), "年龄应该被更新");
        assertEquals("Jackson", lastUser.getName(), "姓名不应该更新");

        userRepository.update(new User(id, "Mario", null));
        lastUser = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(id)), User.class);
        assertNotNull(lastUser, "数据应该存在");
        assertEquals("Mario", lastUser.getName(), "姓名应该更新");
        assertEquals(19, lastUser.getAge(), "年龄不应该被更新");
    }

    @Test
    void updateSubclass() {
        // 初始数据
        AdminUser user = new AdminUser(null, "Jackson", 18, "operator,manager");
        mongoTemplate.save(user);
        String id = user.getId();

        // 部分更新
        userRepository.update(new AdminUser(id, null, 19, "operator"));
        AdminUser lastUser = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(id)), AdminUser.class);
        assertNotNull(lastUser, "数据应该存在");
        assertEquals(19, lastUser.getAge(), "年龄应该被更新");
        assertEquals("operator", lastUser.getRole(), "角色应该被更新");
        assertEquals("Jackson", lastUser.getName(), "姓名不应该更新");

    }

}
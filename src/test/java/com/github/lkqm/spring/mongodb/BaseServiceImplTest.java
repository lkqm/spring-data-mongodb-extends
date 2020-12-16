package com.github.lkqm.spring.mongodb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.lkqm.spring.mongodb.integration.User;
import com.github.lkqm.spring.mongodb.integration.UserServiceImpl;
import java.util.Collections;
import java.util.List;
import javax.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

@DataMongoTest
@RunWith(SpringRunner.class)
class BaseServiceImplTest {

    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private UserServiceImpl userService;

    User user;

    @BeforeEach
    void beforeEach() {
        mongoTemplate.remove(new Query(), User.class);
        user = new User();
        user.setAge(18);
        user.setName("Mario Luo");
        mongoTemplate.save(user);
    }

    @Test
    void save() {
        User entity = new User();
        entity.setAge(4);
        entity.setName("Jack");
        userService.save(entity);
        assertNotNull(entity.getId());

        User lastUser = getLastUserFromDb(entity.getId());
        assertNotNull(lastUser);
        assertEquals(4, lastUser.getAge());
        assertEquals("Jack", lastUser.getName());
    }

    private User getLastUserFromDb(String id) {
        return mongoTemplate.findOne(Query.query(Criteria.where("_id").is(id)), User.class);
    }

    @Test
    void deleteById() {
        long count = userService.deleteById(user.getId());
        assertEquals(1, count, "删除数据应该成功");
    }

    @Test
    void findById() {
        User model = userService.findById(user.getId());
        assertEquals(user, model, "查询数据应该相等");
    }

    @Test
    void findByIds() {
        List<String> ids = Collections.singletonList(user.getId());
        List<User> data = userService.findById(ids);
        assertTrue(data != null && data.size() == 1);
        assertEquals(user, data.get(0));
    }

    @Test
    void count() {
        long count = userService.count();
        assertTrue(count > 0);
    }


}
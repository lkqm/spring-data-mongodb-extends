package com.github.lkqm.spring.mongodb;

import com.github.lkqm.spring.mongodb.integration.AdminUser;
import com.github.lkqm.spring.mongodb.integration.User;
import com.github.lkqm.spring.mongodb.integration.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ExtendWith(SpringExtension.class)
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
    void update() {
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setName("Mario");
        userService.update(updateUser);
        User lastUser = getLastUserFromDb(user.getId());
        assertEquals("Mario", lastUser.getName(), "名字应该被更新");
        assertEquals(18, lastUser.getAge(), "年龄不应该被更新");
    }

    @Test
    void updateSubclass() {
        // 初始数据
        AdminUser user = new AdminUser(null, "Jackson", 18, "operator,manager");
        mongoTemplate.save(user);
        String id = user.getId();

        // 部分更新
        userService.update(new AdminUser(id, null, 19, "operator"));
        AdminUser lastUser = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(id)), AdminUser.class);
        assertNotNull(lastUser, "数据应该存在");
        assertEquals(19, lastUser.getAge(), "年龄应该被更新");
        assertEquals("operator", lastUser.getRole(), "角色应该被更新");
        assertEquals("Jackson", lastUser.getName(), "姓名不应该更新");
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
    void findById2() {
        List<String> ids = Collections.singletonList(user.getId());
        List<User> data = userService.findById(ids);
        assertTrue(data != null && data.size() == 1);
        assertEquals(user, data.get(0));
    }

    @Test
    void findByIdMap() {
        List<String> ids = Collections.singletonList(user.getId());
        Map<String, User> data = userService.findByIdMap(ids);
        assertTrue(data != null && data.size() == 1);
        assertEquals(user, data.get(user.getId()));
    }

}
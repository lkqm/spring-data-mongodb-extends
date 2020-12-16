package com.github.lkqm.spring.mongodb;

import com.github.lkqm.spring.mongodb.integration.UserServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(repositoryBaseClass = MongoRepositoryPlusImpl.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public UserServiceImpl userService(MongoTemplate mongoTemplate) {
        UserServiceImpl userService = new UserServiceImpl();
        userService.setMongoTemplate(mongoTemplate);
        return userService;
    }
}
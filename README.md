# spring-data-mongodb-extends
Useful spring-data-mongodb extends, such as partial update method, generic crud service.

给力的spring-data-mongodb扩展, 例如部分更新方法、通用的curd服务类.

```
<dependency>
    <groupId>com.github.lkqm</groupId>
    <artifactId>spring-data-mongodb-extends</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Features
- MongoRepositoryPlus: Partial update entity methods.
- BaseService: Generic crud methods.

## Methods
- BaseService
```
save(entity)
update(entity)
deleteById(id)
deleteById(ids)
findById(id)
findById(ids)
count()
```
- MongoRepositoryPlus
```
update(entity)
update(query, entity)
count(query)
findOne(query)
findAll(query)
findAll(query, pageable)
mongoOperations()
```

## How to use?
```
// 1. Enable MongoRepositoryPlusImpl
@SpringBootApplication
@EnableMongoRepositories(repositoryBaseClass = MongoRepositoryPlusImpl.class)
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}

// 2. Implements MongoRepositoryPlus
public interface UserRepository extends MongoRepositoryPlus {
    /*
    default List<User> listUsers() {
        mongoOperations().findAll();
    }
    */
}

// 3. Use repository
userRepository.update(user);
```


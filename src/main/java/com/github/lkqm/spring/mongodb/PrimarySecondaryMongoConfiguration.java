package com.github.lkqm.spring.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoDbFactorySupport;
import org.springframework.util.StringUtils;

/**
 * Extends bean configuration.
 */
@Configuration
public class PrimarySecondaryMongoConfiguration {

    @Value("${spring.data.mongodb.uri:}")
    private String mongoUri;

    @Bean
    public MongoDbFactorySupport<?> mongoDbFactory(ObjectProvider<MongoClient> mongo,
            ObjectProvider<com.mongodb.client.MongoClient> mongoClient) {
        MongoClientURI mongoClientURI = new MongoClientURI(mongoUri);
        String uriSecondary = StringUtils.isEmpty(mongoUri) ? null : MongoUtils.parseSecondaryPreferredUri(mongoUri);

        MongoClient preferredClient = mongo.getIfAvailable();
        if (preferredClient != null) {
            MongoClient mongoClientSecondary = null;
            if (uriSecondary != null) {
                mongoClientSecondary = new MongoClient(uriSecondary);
            }
            return new PrimarySecondaryMongoDbFactory(preferredClient, mongoClientSecondary,
                    mongoClientURI.getDatabase());
        }
        com.mongodb.client.MongoClient fallbackClient = mongoClient.getIfAvailable();
        if (fallbackClient != null) {
            com.mongodb.client.MongoClient mongoClientSecondary = null;
            if (uriSecondary != null) {
                mongoClientSecondary = MongoClients.create(uriSecondary);
            }
            return new PrimarySecondaryMongoClientDbFactory(fallbackClient, mongoClientSecondary,
                    mongoClientURI.getDatabase());
        }
        throw new IllegalStateException("Expected to find at least one MongoDB client.");
    }

}

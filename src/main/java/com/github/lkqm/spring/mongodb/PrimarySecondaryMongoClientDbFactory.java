package com.github.lkqm.spring.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * Read write separation with two different uri configuration.
 *
 * @see MongoUtils#parseSecondaryPreferredUri
 */
public class PrimarySecondaryMongoClientDbFactory extends SimpleMongoClientDbFactory {

    private final MongoClient mongoClientSecondaryPreferred;

    public PrimarySecondaryMongoClientDbFactory(String connectionString, String connectionStringSecondaryPreferred) {
        super(connectionString);
        this.mongoClientSecondaryPreferred = MongoClients.create(connectionStringSecondaryPreferred);
    }

    public PrimarySecondaryMongoClientDbFactory(ConnectionString connectionString,
            ConnectionString connectionStringSecondaryPreferred) {
        super(connectionString);
        this.mongoClientSecondaryPreferred = MongoClients.create(connectionStringSecondaryPreferred);
    }

    public PrimarySecondaryMongoClientDbFactory(MongoClient mongoClient,
            MongoClient mongoClientSecondaryPreferred, String databaseName) {
        super(mongoClient, databaseName);
        this.mongoClientSecondaryPreferred = mongoClientSecondaryPreferred;
    }

    @Override
    protected MongoClient getMongoClient() {
        ReadPreference readPreference = PrimarySecondaryMongoReadHolder.getReadPreference();
        if (readPreference == ReadPreference.PRIMARY) {
            return super.getMongoClient();
        } else if (readPreference == ReadPreference.SECONDARY && mongoClientSecondaryPreferred != null) {
            return mongoClientSecondaryPreferred;
        }

        if (mongoClientSecondaryPreferred != null) {
            try {
                TransactionAspectSupport.currentTransactionStatus();
            } catch (NoTransactionException e) {
                return mongoClientSecondaryPreferred;
            }
        }
        return super.getMongoClient();
    }
}

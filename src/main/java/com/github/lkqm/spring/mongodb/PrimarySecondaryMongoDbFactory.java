package com.github.lkqm.spring.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * Read write separation with two different uri configuration.
 *
 * @see MongoUtils#parseSecondaryPreferredUri
 */
public class PrimarySecondaryMongoDbFactory extends SimpleMongoDbFactory {

    private final MongoClient mongoClientSecondaryPreferred;

    public PrimarySecondaryMongoDbFactory(MongoClientURI uri, MongoClientURI uriSecondaryPreferred) {
        super(uri);
        this.mongoClientSecondaryPreferred =
                uriSecondaryPreferred != null ? new MongoClient(uriSecondaryPreferred) : null;
    }

    public PrimarySecondaryMongoDbFactory(MongoClient mongoClient, MongoClient mongoClientSecondaryPreferred,
            String databaseName) {
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

package com.github.lkqm.spring.mongodb;

import java.util.function.Supplier;

/**
 * MongoReader safely switch mongo read preference.
 */
public class PrimarySecondaryMongoReader {

    private PrimarySecondaryMongoReader() {
    }

    /**
     * Execute action in primary mongo client context when read data.
     */
    public static <T> T doReadPrimary(Supplier<T> s) {
        return doRead(ReadPreference.PRIMARY, s);
    }

    /**
     * Execute action in secondary mongo client context when read data.
     */
    public static <T> T doReadSecondary(Supplier<T> s) {
        return doRead(ReadPreference.SECONDARY, s);
    }

    private static <T> T doRead(ReadPreference readPreference, Supplier<T> s) {
        ReadPreference originalReadPreference = PrimarySecondaryMongoReadHolder.getReadPreference();
        try {
            PrimarySecondaryMongoReadHolder.set(readPreference);
            return s.get();
        } finally {
            PrimarySecondaryMongoReadHolder.set(originalReadPreference);
        }
    }
}

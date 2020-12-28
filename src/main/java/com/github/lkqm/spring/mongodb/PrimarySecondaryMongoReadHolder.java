package com.github.lkqm.spring.mongodb;

import static com.github.lkqm.spring.mongodb.ReadPreference.PRIMARY;
import static com.github.lkqm.spring.mongodb.ReadPreference.SECONDARY;

/**
 * Holder mongo read preferences.
 *
 * @see PrimarySecondaryMongoDbFactory
 * @see PrimarySecondaryMongoClientDbFactory
 */
public class PrimarySecondaryMongoReadHolder {

    private static final ThreadLocal<ReadPreference> holder = new ThreadLocal<>();

    private PrimarySecondaryMongoReadHolder() {
    }

    /**
     * Returns current read preference from which mongo client.
     */
    public static ReadPreference getReadPreference() {
        return holder.get();
    }

    /**
     * Set read data from primary mongo client.
     */
    public static void setPrimary() {
        set(PRIMARY);
    }

    /**
     * Set read data from secondary mongo client.
     */
    public static void setSecondary() {
        set(SECONDARY);
    }

    /**
     * Cleans read data from with mongo client.
     */
    public static void clean() {
        set(null);
    }

    public static void set(ReadPreference readPreference) {
        holder.set(readPreference);
    }

}

enum ReadPreference {
    PRIMARY, SECONDARY
}

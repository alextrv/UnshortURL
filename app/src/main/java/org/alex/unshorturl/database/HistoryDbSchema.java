package org.alex.unshorturl.database;

/**
 * Database history URLs schema.
 */
public class HistoryDbSchema {
    public static final class HistoryTable {
        public static final String NAME = "history";

        public static final class Cols {
            public static final String ID = "_id";
            public static final String URL = "url";
            public static final String PARENT_ID = "parent_id";
        }
    }
}

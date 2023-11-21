package com.github.sukhin.processors.demo_processor.util;

import java.nio.file.Path;

public class TestData {
    public static final String CREATE_DB_PATH =
            Path.of("src/test/resources/sql/_db_main_create.sql").toAbsolutePath().toString();
    public static final String INIT_DB_PATH =
            Path.of("src/test/resources/sql/initDb.sql").toAbsolutePath().toString();
    public static final String INSERT_TEST_DATA_PATH =
            Path.of("src/test/resources/sql/insert_test_data.sql").toAbsolutePath().toString();
    public static final String SHUTDOWN_DB_PATH =
            Path.of("src/test/resources/sql/shutdownDb.sql").toAbsolutePath().toString();
}

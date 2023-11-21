package com.github.sukhin.processors.demo_processor;

import io.zonky.test.db.postgres.embedded.DatabasePreparer;
import io.zonky.test.db.postgres.junit5.EmbeddedPostgresExtension;
import io.zonky.test.db.postgres.junit5.PreparedDbExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractIntegrationTest {
    protected static final DatabasePreparer EMPTY_PREPARER = ds -> {
    };

    @RegisterExtension
    public static PreparedDbExtension db = EmbeddedPostgresExtension.preparedDatabase(EMPTY_PREPARER);

    @BeforeAll
    static void initDb() throws SQLException, IOException {
        try (Connection connection = db.getTestDatabase().getConnection()) {
            // executeScript(connection, Files.readString(Paths.get(CREATE_DB_PATH)));
            // executeScript(connection, Files.readString(Paths.get(INIT_DB_PATH)));
        }
    }

    protected static void executeScript(Connection connection, String script) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(script);
        }
    }
}

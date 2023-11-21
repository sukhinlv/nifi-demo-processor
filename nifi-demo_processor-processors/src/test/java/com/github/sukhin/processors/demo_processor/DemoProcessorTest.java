package com.github.sukhin.processors.demo_processor;

import com.github.sukhin.processors.demo_processor.util.DummyDBCPService;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.sukhin.processors.demo_processor.DemoProcessor.DBCP_SERVICE;
import static com.github.sukhin.processors.demo_processor.DemoProcessor.REL_FAILURE;
import static com.github.sukhin.processors.demo_processor.DemoProcessor.REL_SUCCESS;
import static com.github.sukhin.processors.demo_processor.DemoProcessor.STRING_PROPERTY;
import static org.assertj.core.api.Assertions.assertThat;

class DemoProcessorTest extends AbstractIntegrationTest {

    @Test
    void should_run_processor() throws InitializationException, SQLException {
        try (Connection connection = db.getTestDatabase().getConnection()) {
            // Given
            TestRunner testRunner = TestRunners.newTestRunner(DemoProcessor.class);

            Map<String, String> attributes = new HashMap<>();
            attributes.put("Some attribute 1", "Attribute 1 value");
            attributes.put("Some attribute 2", "Attribute 2 value");

            testRunner.setProperty(STRING_PROPERTY, "Test property value");

            DummyDBCPService dummyDBCPService = new DummyDBCPService(connection);
            testRunner.addControllerService("TestDBCPService", dummyDBCPService);
            testRunner.setProperty(DBCP_SERVICE, "TestDBCPService");
            testRunner.enableControllerService(dummyDBCPService);

            String content = "Some basic content";

            testRunner.enqueue(content, attributes);
            // testRunner.enqueue(content);
            // testRunner.enqueue();

            // When
            testRunner.run();

            // Then
            List<MockFlowFile> successFlowFiles = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
            List<MockFlowFile> failureFlowFiles = testRunner.getFlowFilesForRelationship(REL_FAILURE);
            assertThat(successFlowFiles).hasSize(1);
            assertThat(failureFlowFiles).isEmpty();
        }
    }
}
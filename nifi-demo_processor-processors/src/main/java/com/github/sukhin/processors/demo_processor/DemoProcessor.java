package com.github.sukhin.processors.demo_processor;

import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.dbcp.DBCPService;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.util.StandardValidators;

import java.io.InputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Set;

import static java.util.Objects.nonNull;

/**
 * Демонстрационный процессор Nifi, показывающий процесс создания процессора.
 */
@Tags("nifi--demo-processor")
@CapabilityDescription("Demonstrates custom processor creation")
public class DemoProcessor extends AbstractProcessor {
    public static final String ATTR_EXCEPTION = "_Exception";
    public static final String ATTR_EXCEPTION_MESSAGE = "_ExceptionMessage";

    public static final PropertyDescriptor DBCP_SERVICE = new
            PropertyDescriptor.Builder()
            .name("Database Connection Pooling Service")
            .description("The Controller Service that is used to obtain connection to database")
            .required(true)
            .identifiesControllerService(DBCPService.class)
            .build();

    public static final PropertyDescriptor STRING_PROPERTY = new
            PropertyDescriptor.Builder()
            .name("String property")
            .description("Some property.")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES)
            .build();

    public static final Relationship REL_SUCCESS = new Relationship.Builder()
            .name("success")
            .description("Successful processing relation")
            .build();

    public static final Relationship REL_FAILURE = new Relationship.Builder()
            .name("failure")
            .description("If a FlowFile fails processing for any reason, it will be routed to this relationship")
            .build();

    private List<PropertyDescriptor> descriptors;
    private Set<Relationship> relationships;

    private DBCPService dbcpService;

    @Override
    protected void init(ProcessorInitializationContext context) {
        super.init(context);
        this.descriptors = List.of(DBCP_SERVICE, STRING_PROPERTY);
        this.relationships = Set.of(REL_SUCCESS, REL_FAILURE);
    }

    @Override
    public Set<Relationship> getRelationships() {
        return relationships;
    }

    @Override
    public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return descriptors;
    }

    @OnScheduled
    public void onScheduled(ProcessContext context) {
        dbcpService =
                context.getProperty(DBCP_SERVICE).asControllerService(DBCPService.class);
    }

    @Override
    public void onTrigger(ProcessContext context, ProcessSession session) {
        FlowFile flowFile = session.get();
        if (flowFile == null) {
            return;
        }

        String stringProperty;
        try {
            stringProperty =
                    context.getProperty(STRING_PROPERTY).evaluateAttributeExpressions(flowFile).getValue();
        } catch (Exception e) {
            processException(session, flowFile, "Error parsing flowfile attributes", e);
            return;
        }

        FlowFile newFlowFile = null;
        try (Connection connection = dbcpService.getConnection()) {
            getLogger().info("Try to save differences to db...");

            String content;
            try (InputStream is = session.read(flowFile)) {
                content = new String(is.readAllBytes());
            }

            // TODO использовать или удалить инициализацию connection
            String newContent = content.length() > 100 ? content.substring(0, 100) : content;

            newFlowFile = session.create();
            session.putAllAttributes(newFlowFile, flowFile.getAttributes());
            session.putAttribute(newFlowFile, "_newAttribute", stringProperty);
            newFlowFile = session.write(newFlowFile, outputStream -> outputStream.write(newContent.getBytes()));

            session.transfer(newFlowFile, REL_SUCCESS);
            session.remove(flowFile);
            getLogger().info("Diffs successfully saved");
        } catch (Exception e) {
            removeFlowFile(session, newFlowFile);
            processException(session, flowFile, "Error saving differences", e);
        }
    }

    private void processException(ProcessSession session, FlowFile flowFile, String message, Exception e) {
        session.putAttribute(flowFile, ATTR_EXCEPTION, getRootCause(e).getClass().getName());
        session.putAttribute(flowFile, ATTR_EXCEPTION_MESSAGE, e.getMessage());
        session.transfer(flowFile, REL_FAILURE);
        getLogger().error(message, e);
    }

    private static void removeFlowFile(ProcessSession session, FlowFile flowFile) {
        if (nonNull(flowFile)) {
            session.remove(flowFile);
        }
    }

    private static Throwable getRootCause(Throwable original) {
        Throwable rootCause = original;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }
}

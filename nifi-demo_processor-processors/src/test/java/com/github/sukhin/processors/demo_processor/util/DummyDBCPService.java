package com.github.sukhin.processors.demo_processor.util;

import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.dbcp.DBCPService;
import org.apache.nifi.processor.FlowFileFilter;
import org.apache.nifi.processor.exception.ProcessException;

import java.sql.Connection;
import java.util.Map;

public class DummyDBCPService extends AbstractControllerService implements DBCPService {

    private Connection connection;

    private DummyDBCPService() {
    }

    public DummyDBCPService(Connection connection) {
        super();
        this.connection = connection;
    }

    @Override
    public Connection getConnection() throws ProcessException {
        return connection;
    }

    @Override
    public Connection getConnection(Map<String, String> attributes) throws ProcessException {
        return DBCPService.super.getConnection(attributes);
    }

    @Override
    public FlowFileFilter getFlowFileFilter() {
        return DBCPService.super.getFlowFileFilter();
    }

    @Override
    public FlowFileFilter getFlowFileFilter(int batchSize) {
        return DBCPService.super.getFlowFileFilter(batchSize);
    }
}

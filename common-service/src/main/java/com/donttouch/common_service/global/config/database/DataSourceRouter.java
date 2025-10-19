package com.donttouch.common_service.global.config.database;


import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataSourceRouter extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        String ds = readOnly ? "read-replica (Slave)" : "write (Master)";
        log.info("[DataSourceRouter] Routing to {}", ds);
        return readOnly ? "read" : "write";
    }
}
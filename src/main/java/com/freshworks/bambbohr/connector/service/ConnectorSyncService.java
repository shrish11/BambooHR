package com.freshworks.bambbohr.connector.service;


import com.freshworks.core.traverser.ParentStep;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Service;
import com.freshworks.core.shared.SyncServiceContainer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ConnectorSyncService {


    public void runSync(ImmutableMap<String, String> map , String transactionId ,  ConnectorHagridConfiguration connectorHagridConfiguration) throws Exception {

        SyncServiceContainer syncServiceContainer = connectorHagridConfiguration.getHagridSyncService().startSync(ParentStep.class , transactionId , 1 , map);
        connectorHagridConfiguration.setHagridContainers(syncServiceContainer);
    }

    public void clearSync(ConnectorHagridConfiguration connectorHagridConfiguration) throws InterruptedException {

        connectorHagridConfiguration.getHagridSyncService().clearSync();
    }

    public int getSyncStatus(ConnectorHagridConfiguration connectorHagridConfiguration) {

        return connectorHagridConfiguration.getSyncStatusService().getSyncStatus();

    }
}

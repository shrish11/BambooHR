package com.freshworks.bambbohr.connector.service;

import com.freshworks.core.shared.ApplicationContextUtil;
import com.freshworks.core.shared.SyncServiceContainer;
import com.freshworks.core.shared.consumer.ConsumerService;
import com.freshworks.core.shared.sync.SyncService;
import com.freshworks.core.shared.sync.SyncStatusService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class ConnectorHagridConfiguration {

    public SyncService hagridSyncService;
    public SyncServiceContainer syncServiceContainer;
    public ConsumerService consumerService;



    public SyncStatusService syncStatusService;

    public  ConnectorHagridConfiguration() {

        this.hagridSyncService = ApplicationContextUtil.getBean(SyncService.class);

    }

    public void setHagridContainers(SyncServiceContainer syncServiceContainer){

        this.syncServiceContainer = syncServiceContainer;
        this.consumerService = this.syncServiceContainer.getConsumerService();
        this.syncStatusService = this.syncServiceContainer.getSyncStatusService();
        log.info("Hagrid Containers set successfully");
    }


}

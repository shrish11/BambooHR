package com.freshworks.bambbohr.connector.service;


import com.freshworks.bambbohr.connector.request.EmployeeConnectorRequest;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
public class EmployeeConnectorService implements ConnectorService{

    private final ConnectorSyncService connectorSyncService;
    private final AbstractConsumerService abstractConsumerService;

    @Autowired
    public EmployeeConnectorService(ConnectorSyncService connectorSyncService,  AbstractConsumerService abstractConsumerService) {
        this.connectorSyncService = connectorSyncService;
        this.abstractConsumerService = abstractConsumerService;
    }



    @Override
    public ImmutableMap<String , String> filterAndCreateBaggageMap(Map<String,String> inputData) {


            // filter map .


          ImmutableMap<String, String> baggageMap = ImmutableMap.copyOf(inputData);

          return baggageMap;
    }

    @Override
    public Map<String, Object> consume(EmployeeConnectorRequest connectorRequest , ConnectorHagridConfiguration connectorHagridConfiguration) {
        // Implement your logic here
        return Map.of();
    }

    @Override
    public void startSync(ImmutableMap<String,String> map , EmployeeConnectorRequest connectorRequest , ConnectorHagridConfiguration connectorHagridConfiguration) {
        try {
            connectorSyncService.runSync(map , connectorRequest.getRequestId() , connectorHagridConfiguration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void clearData(ConnectorHagridConfiguration connectorHagridConfiguration) {
        try{
            connectorSyncService.clearSync(connectorHagridConfiguration);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isSyncComplete(ConnectorHagridConfiguration connectorHagridConfiguration) {

        int syncStatus = connectorSyncService.getSyncStatus(connectorHagridConfiguration);
//        SyncServiceContainer syncServiceContainer = syncService.getSyncServiceContainer();
        if(syncStatus == 0 ){
            return false;
        }
        return true;

    }

}

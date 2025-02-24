package com.freshworks.bambbohr.connector.service;

import com.freshworks.bambbohr.connector.request.EmployeeConnectorRequest;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

public interface ConnectorService {

    ImmutableMap<String , String> filterAndCreateBaggageMap(Map<String,String> inputData);

    Map<String , Object> consume(EmployeeConnectorRequest connectorRequest , ConnectorHagridConfiguration connectorHagridConfiguration);



    void startSync(ImmutableMap<String,String> map , EmployeeConnectorRequest connectorRequest , ConnectorHagridConfiguration connectorHagridConfiguration);

    void clearData(ConnectorHagridConfiguration connectorHagridConfiguration);

    boolean isSyncComplete(ConnectorHagridConfiguration connectorHagridConfiguration);
}

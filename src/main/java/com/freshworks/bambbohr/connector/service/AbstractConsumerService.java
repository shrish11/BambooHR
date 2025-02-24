package com.freshworks.bambbohr.connector.service;

import com.freshworks.core.shared.consumer.ConsumerService;
import com.freshworks.bambbohr.connector.request.EmployeeConnectorRequest;
import com.freshworks.bambbohr.hagrid.assets.*;
import java.util.List;
import com.freshworks.freshindex.index.query.Expression;

public abstract class AbstractConsumerService {


        public abstract List<Employee> consumeEmployeeAsset(EmployeeConnectorRequest connectorRequest , ConnectorHagridConfiguration connectorHagridConfiguration) throws Exception;


         public abstract List<Employee> consumeEmployeeAssetByFilter(EmployeeConnectorRequest connectorRequest , ConnectorHagridConfiguration connectorHagridConfiguration , Expression expression) throws Exception;


         public abstract List<Employee> consumeEmployeeAssetStream(EmployeeConnectorRequest connectorRequest , ConnectorHagridConfiguration connectorHagridConfiguration , int startToken , int endToken) throws Exception;


}

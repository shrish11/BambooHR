package com.freshworks.bambbohr.connector.service;

import java.util.Map;
import com.freshworks.bambbohr.connector.request.EmployeeConnectorRequest;
import com.freshworks.bambbohr.hagrid.assets.*;
import java.util.List;
import com.freshworks.freshindex.index.query.Expression;
import com.freshworks.core.shared.consumer.AssetStreamResponse;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.Objects;
import java.util.ArrayList;

@Slf4j
@Service
public class ConnectorConsumerService extends AbstractConsumerService{


    @Override
     public List<Employee> consumeEmployeeAsset(EmployeeConnectorRequest connectorRequest , ConnectorHagridConfiguration connectorHagridConfiguration) throws Exception{

        log.info("consumeEmployeeAsset invoked");
        return connectorHagridConfiguration.getConsumerService().getAssetByAssetType(Employee.class);

     }


     @Override
      public List<Employee> consumeEmployeeAssetByFilter(EmployeeConnectorRequest connectorRequest , ConnectorHagridConfiguration connectorHagridConfiguration , Expression expression) throws Exception{

           log.info("consumeEmployeeAssetByFilter invoked");
           return connectorHagridConfiguration.getConsumerService().getAssetByAssetTypeAndFilter(Employee.class , expression);

      }


       @Override
        public List<Employee> consumeEmployeeAssetStream(EmployeeConnectorRequest connectorRequest , ConnectorHagridConfiguration connectorHagridConfiguration , int startToken , int endToken) throws Exception{

            log.info("consumeEmployeeAssetStream invoked");
             AssetStreamResponse.Token employeeToken = new AssetStreamResponse.Token();
             employeeToken.setStart(startToken);
             employeeToken.setCount(endToken);
             AssetStreamResponse<Employee> employeeAssetStreamResponse = connectorHagridConfiguration.getConsumerService().streamAssetByAssetType(Employee.class, employeeToken);
               if(Objects.isNull(employeeAssetStreamResponse.getNextToken()))
                     return new ArrayList<>();
             return employeeAssetStreamResponse.getAbstractAssetList();

        }

}

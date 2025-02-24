package com.freshworks.bambbohr.worker.config;

import com.netflix.conductor.client.config.ConductorClientConfiguration;
import com.netflix.conductor.client.http.ConductorClient;
import com.netflix.conductor.client.http.TaskClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;


@org.springframework.context.annotation.Configuration
public class Configuration {

       @Value("${conductor.client.externalPayloadStorage:false}")
       private boolean externalPayloadStorageEnabled;

       @Value("${conductor.client.enforceThresholds:false}")
       private boolean enforceThresholds;

       @Value("${conductor.client.workflowInputPayloadThresholdKB:5120}")
       private int workflowInputPayloadThresholdKB;

       @Value("${conductor.client.workflowInputMaxPayloadThresholdKB:10240}")
       private int workflowInputMaxPayloadThresholdKB;

       @Value("${conductor.client.taskOutputPayloadThresholdKB:3072}")
       private int taskOutputPayloadThresholdKB;

       @Value("${conductor.client.taskOutputMaxPayloadThresholdKB:10240}")
       private int taskOutputMaxPayloadThresholdKB;

       @Bean
       public TaskClient taskClient(ConductorClient client) {

           ConductorClientConfiguration conductorClientConfiguration = getConductorClientConfiguration();

           return new TaskClient(client , conductorClientConfiguration);
       }

       private  @NotNull ConductorClientConfiguration getConductorClientConfiguration() {

           ConductorClientConfiguration conductorClientConfiguration = new ConductorClientConfiguration() {
               @Override
               public int getWorkflowInputPayloadThresholdKB() {
                   return workflowInputPayloadThresholdKB;
               }

               @Override
               public int getWorkflowInputMaxPayloadThresholdKB() {
                   return workflowInputMaxPayloadThresholdKB;
               }

               @Override
               public int getTaskOutputPayloadThresholdKB() {
                   return taskOutputPayloadThresholdKB;
               }

               @Override
               public int getTaskOutputMaxPayloadThresholdKB() {
                   return taskOutputMaxPayloadThresholdKB;
               }

               @Override
               public boolean isExternalPayloadStorageEnabled() {
                   return externalPayloadStorageEnabled;
               }

               @Override
               public boolean isEnforceThresholds() {
                   return enforceThresholds;
               }
           };

           return conductorClientConfiguration;

    }

}

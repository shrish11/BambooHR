package com.freshworks.bambbohr.tests;

import com.freshworks.bambbohr.worker.EmployeeWorker;
import com.netflix.conductor.common.metadata.tasks.Task;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.context.ApplicationContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@TestPropertySource(properties = {
		"DEFAULT_PERCENTILE=0.9,0.95",
		"DEFAULT_LATENCY_PERCENTILE=0.9,0.95",
		"taskToDomain=TEST"
})
@Import(EmployeeWorkerTests.PrometheusTestConfig.class)
class EmployeeWorkerTests {



	@Autowired
	private PrometheusMeterRegistry prometheusMeterRegistry;

	@Test
	void contextLoads(ApplicationContext applicationContext) {

		EmployeeWorker employeeWorker = applicationContext.getBean(EmployeeWorker.class);

		Task task = new Task();
        task.setWorkflowInstanceId(UUID.randomUUID().toString());
        task.setTaskId(UUID.randomUUID().toString());
        Map<String, Object> inputData = new HashMap<>();

        inputData.put("input_required_from", new HashMap<>());

        Map<String, Object> workflowData = new HashMap<>();

        Map<String, Object> jiraConnector = new HashMap<>();
        jiraConnector.put("jiraAccount", "freshworks-ip");
        jiraConnector.put("jiraEmail", "test@example.com");
        jiraConnector.put("jiraAPIToken", "REPLACE_WITH_TOKEN");

        Map<String, Object> centralConnector = new HashMap<>();
        centralConnector.put("payload_type", "ipass_test_payload");
        centralConnector.put("central_url", "https://central-staging.freshworksapi.com/collector/");
        centralConnector.put("central_authentication_header", "REPLACE_WITH_AUTH_HEADER");
        centralConnector.put("account_id", "123456");
        centralConnector.put("organisation_id", "987654321");
        centralConnector.put("pod", "US");
        centralConnector.put("region", "US");
        centralConnector.put("payloadVersion", "2.0.0");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("accountId", "1234");

        workflowData.put("jira_connector", jiraConnector);
        workflowData.put("central_connector", centralConnector);
        workflowData.put("metadata", metadata);

        inputData.put("workflow", workflowData);

        task.setInputData(inputData);
        employeeWorker.work(task);

	}



	@TestConfiguration
	static class PrometheusTestConfig {

		@Bean
		public PrometheusMeterRegistry prometheusMeterRegistry() {
			return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
		}
	}


}


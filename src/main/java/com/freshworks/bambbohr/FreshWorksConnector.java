package com.freshworks.bambbohr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.netflix.conductor", "com.freshworks" })
public class FreshWorksConnector {

	public static void main(String[] args) {
		SpringApplication.run(FreshWorksConnector.class, args);
	}

}

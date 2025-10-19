package com.donttouch.internal_assistant_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {
		"com.donttouch.common_service",
		"com.donttouch.internal_assistant_service"
})
@EntityScan(basePackages = {
		"com.donttouch.common_service",
		"com.donttouch.internal_assistant_service"
})
@EnableJpaRepositories(basePackages = {
		"com.donttouch.common_service",
		"com.donttouch.internal_assistant_service"
})
public class InternalAssistantServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InternalAssistantServiceApplication.class, args);
	}

}

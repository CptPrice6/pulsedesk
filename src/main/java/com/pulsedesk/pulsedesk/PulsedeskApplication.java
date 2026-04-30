package com.pulsedesk.pulsedesk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// we scan all subpackages since main class is in a nested package
@SpringBootApplication(scanBasePackages = "com.pulsedesk")
@EnableJpaRepositories(basePackages = "com.pulsedesk.repository")
@EntityScan(basePackages = "com.pulsedesk.model")
public class PulsedeskApplication {

	public static void main(String[] args) {
		SpringApplication.run(PulsedeskApplication.class, args);
	}

}
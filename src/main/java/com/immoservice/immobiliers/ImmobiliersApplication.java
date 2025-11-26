package com.immoservice.immobiliers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.immoservice.immobiliers.config.TwilioProperties;

@SpringBootApplication
@EntityScan(basePackages = "com.immoservice.immobiliers.entities")
@EnableJpaRepositories(basePackages = "com.immoservice.immobiliers.repository")
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EnableScheduling
@EnableConfigurationProperties(TwilioProperties.class)
public class ImmobiliersApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImmobiliersApplication.class, args);
	}

}

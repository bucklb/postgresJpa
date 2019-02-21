package com.example.postgresdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PostgresDemoApplication {
	private static Logger log = LoggerFactory.getLogger(PostgresDemoApplication.class);


	public static void main(String[] args) {
		log.warn("Starting!");
		SpringApplication.run(PostgresDemoApplication.class, args);
	}
}

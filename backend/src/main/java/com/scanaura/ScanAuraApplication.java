package com.scanaura;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ScanAuraApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScanAuraApplication.class, args);
	}

}

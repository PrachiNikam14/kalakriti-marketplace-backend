package com.Kalakriti.Kalakriti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class KalakritiApplication {
	public static void main(String[] args) {
		SpringApplication.run(KalakritiApplication.class, args);
	}
}

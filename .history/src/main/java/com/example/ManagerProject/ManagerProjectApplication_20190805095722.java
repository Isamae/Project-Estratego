package com.example.ManagerProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ComponentScan(basePackages="com.arquitecturajava")
public class ManagerProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManagerProjectApplication.class, args);
	}

}

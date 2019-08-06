package com.example.ManagerProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@RestController
//@ComponentScan(basePackages="com.arquitecturajava")
public class ManagerProjectApplication {
@GetMapping(value = "/Hello")
public String getMethodName(){
	return "hello"
}
	public static void main(String[] args) {
		SpringApplication.run(ManagerProjectApplication.class, args);
	}

}

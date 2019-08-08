package com.example.ManagerProject;



import com.example.ManagerProject.Object.Project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;





@SpringBootApplication
@RestController
public class ManagerProjectApplication {

	private static final String HELLO = "/hello";
	@GetMapping(value=HELLO)
	public String getMethodName() throws Exception{
		
		Project project = new Project();
		return "Casa3.mpp";

	}

	

	public static void main(String[] args) {
		SpringApplication.run(ManagerProjectApplication.class, args);
	}
	

}
 
package com.example.ManagerProject;

import javax.annotation.Resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.ProjectReader;



@SpringBootApplication
@RestController
public class ManagerProjectApplication {

private static final String HELLO = "/hello";
@GetMapping(value=HELLO)
public String getMethodName(){
	ProjectReader reader = new UniversalProjectReader ();
	ProjectFile project = reader.read("example.mpp");
	for (Resource resource : project.getAllResources())
	{
	System.out.println("Resource: " + resource.getName() + " (Unique ID=" + resource.getUniqueID() + ")");
	}
}



	public static void main(String[] args) {
		SpringApplication.run(ManagerProjectApplication.class, args);
	}

}
 
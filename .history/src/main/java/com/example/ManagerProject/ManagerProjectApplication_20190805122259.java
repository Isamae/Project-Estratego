package com.example.ManagerProject;


import java.io.Console;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.UniversalProjectReader;
import net.sf.mpxj.Resource;




@SpringBootApplication
@RestController
public class ManagerProjectApplication {

private static final String HELLO = "/hello";
@GetMapping(value=HELLO)
public String getMethodName() throws MPXJException {
	ProjectReader reader = new UniversalProjectReader ();
	ProjectFile project = reader.read("./Casa3.mpp");
	String var = "";
	for (Resource resource : project.getResources())
	{
		var = var + "Resource: " + resource.getName() + " (Unique ID=" + resource.getUniqueID() ;
		
	}
	return var;
}

	public static void main(String[] args) {
		SpringApplication.run(ManagerProjectApplication.class, args);
	}

}
 
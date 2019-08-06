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
import net.sf.mpxj.Task;




@SpringBootApplication
@RestController
public class ManagerProjectApplication {

private static final String HELLO = "/hello";
@GetMapping(value=HELLO)
public String getMethodName() throws MPXJException {
	
	


	/*String var = "";

	ProjectReader reader = new UniversalProjectReader ();
	ProjectFile project = reader.read("classpath:Casa3.mpp");
	for (Task task : project.getAllTasks())
	{
		var = var + "Task: " + task.getName() + " ID=" + task.getID() + " Unique ID=" + task.getUniqueID();
		System.out.print(var);
	}
	return var;*/


	File file = ResourceUtils.getFile("classpath:config/sample.txt")
 
	//File is found
	System.out.println("File Found : " + file.exists());
	
	//Read File Content
	String content = new String(Files.readAllBytes(file.toPath()));
	System.out.println(content);
}

	public static void main(String[] args) {
		SpringApplication.run(ManagerProjectApplication.class, args);
	}

}
 
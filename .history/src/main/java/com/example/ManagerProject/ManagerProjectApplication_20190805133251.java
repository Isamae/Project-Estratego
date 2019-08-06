package com.example.ManagerProject;


import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import com.example.ManagerProject.Object.Project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ResourceUtils;
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
public String getMethodName() throws Exception{
	

	Project project = new Project():
	
	return var;


	
}

	public static void main(String[] args) {
		SpringApplication.run(ManagerProjectApplication.class, args);
	}
	

}
 


package com.example.ManagerProject.Controller;
import java.io.File;

import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.sf.mpxj.*; 
import net.sf.mpxj.reader.*;  
import net.sf.mpxj.writer.*;  
import net.sf.mpxj.mpp.*; 

/**
 * RestProject
 */
@RestController
@RequestMapping(value = "/project")
public class RestProjectFileMpp {
    
    @GetMapping(value = "/dato")
    public String postJson() throws Exception{
		File file = ResourceUtils.getFile("classpath:"+"010101010010101001010101001010100101.mpp");
		System.out.print("Este es el archivo usado:"+ file);
        ProjectReader reader = new MPPReader();  
        ProjectFile projectObj = reader.read(file);

        for (Task task : project.getAllTasks())  
        {  
        System.out.println("Task: " + task.getName() + " ID=" + task.getID() + " Unique ID=" + task.getUniqueID());  
        } 
        
        return "Hola Mundo";
    }

}




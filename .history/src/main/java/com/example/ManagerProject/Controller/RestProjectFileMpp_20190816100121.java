

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
        //Project project = new Project();
		File file = ResourceUtils.getFile("classpath:"+"Casa6.mpp");
		System.out.print("Este es el archivo usado:"+ file);
        //ProjectReader reader = new UniversalProjectReader();
        //ProjectFile archivo = reader.read(file)
        
        ProjectReader reader = new MPPReader();  
        ProjectFile projectObj = reader.read(file);
        return "Hola Mundo";
    }

}




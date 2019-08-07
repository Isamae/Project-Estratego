
    
package com.example.ManagerProject.Controller;

import org.springframework.web.bind.annotation.RestController;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.UniversalProjectReader;

import java.io.File;

import com.example.ManagerProject.Object.Project;

import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * RestProject
 */
@RestController
@RequestMapping("/Project")
public class RestProject {
    
    /*@GetMapping
    public String getjsonProject() throws Exception{
        Project project = new Project();
        return project.getTareas("Casa3.mpp");
    }*/
    @GetMapping
    public String getjsonProject() throws Exception{
        Project project2 = new Project();
        File file = ResourceUtils.getFile("classpath:"+"Casa6.mpp");
        ProjectReader reader = new UniversalProjectReader();
        ProjectFile project = reader.read(file);
        return  project2.getRecursos(project);
    }
    /*@PostMapping
    public String postjsonProject() throws Exception{
        Project project = new Project();
        return project.getTareas("Casa3.mpp");
    }*/


    
    
}

    
package com.example.ManagerProject.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.ManagerProject.Object.Project;

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
    
    @GetMapping
    public String getjsonProject() throws Exception{
        Project project = new Project();
        return project.getTareas("Casa3.mpp");
    }

    @PostMapping
    public String postjsonProject() throws Exception{
        Project project = new Project();
        return project.getTareas("Casa3.mpp");
    }
    
    
}
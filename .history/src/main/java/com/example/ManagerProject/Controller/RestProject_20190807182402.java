
    
package com.example.ManagerProject.Controller;

import org.springframework.web.bind.annotation.RestController;

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
    public String getJson() throws Exception{
        ProjectController controller = new  ProjectController();
        
        return controller.getjsonProject();
    }
    


    
    
}

    
package com.example.ManagerProject.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.JsonObject;

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
    public JsonObject getJson() throws Exception,JsonMappingException{
        ProjectController controller = new  ProjectController();
        return controller.getjsonProject();
    }
    
    

    
    
}


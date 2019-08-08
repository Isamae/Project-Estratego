
    
package com.example.ManagerProject.Controller;

import org.springframework.web.bind.annotation.RestController;



import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.JsonObject;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * RestProject
 */
@RestController

public class RestProject {
    
    @RequestMapping(value = "/Project")
    public JsonObject getJson() throws Exception,JsonMappingException{
        ResponseGenerator responseGenerator = new ResponseGenerator();
        ProjectController controller = new  ProjectController();
        responseGenerator.setJSONData(controller.getjsonProject());

        System.out.println(controller.getjsonProject());
        return controller.getjsonProject();
    }
    
    

    
    
}


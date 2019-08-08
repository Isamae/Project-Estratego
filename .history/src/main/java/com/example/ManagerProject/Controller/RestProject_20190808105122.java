
    
package com.example.ManagerProject.Controller;

import org.springframework.web.bind.annotation.RestController;



import com.fasterxml.jackson.databind.JsonMappingException;
///import com.google.gson.JsonObject;

import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * RestProject
 */
@RestController
@RequestMapping(value = "/Project")
public class RestProject {
    
    @GetMapping(value = "/dato", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getJson() throws Exception{
        ProjectController controller = new  ProjectController();

        System.out.println( controller.getjsonProject());

        return controller.getjsonProject().toString();
    }
    
    

    
    
}



    
package com.example.ManagerProject.Controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * RestProject
 */
@RestController
public class RestProject {
    
    @GetMapping
    public String jsonProject(){
        return "";
    }
    
    
}
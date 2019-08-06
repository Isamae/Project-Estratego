
    
package com.example.ManagerProject.Controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * RestProject
 */
@RestController
@RequestMapping
public class RestProject {
    
    @GetMapping
    public String jsonProject(){
        return "";
    }
    
    
}
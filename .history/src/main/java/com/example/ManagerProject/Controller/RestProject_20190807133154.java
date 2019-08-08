
    
package com.example.ManagerProject.Controller;

import org.springframework.web.bind.annotation.RestController;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Day;
import net.sf.mpxj.DayType;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.UniversalProjectReader;

import java.util.List;
import java.io.File;
import java.util.ArrayList;

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
    
    
    /*@PostMapping
    public String postjsonProject() throws Exception{
        Project project = new Project();
        return project.getTareas("Casa3.mpp");
    }*/


    
    
}
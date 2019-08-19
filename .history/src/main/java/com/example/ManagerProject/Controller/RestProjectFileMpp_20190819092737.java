

package com.example.ManagerProject.Controller;
import java.io.Console;
import java.io.File;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.sf.mpxj.*; 
import net.sf.mpxj.reader.*;  
import net.sf.mpxj.writer.*;  
import net.sf.mpxj.mpp.*;
import net.sf.mpxj.ganttproject.schema.Resource;

import java.util.Calendar;
import java.util.List;
import java.util.Map;


/**
 * RestProject
 */
@RestController
@RequestMapping(value = "/project")
public class RestProjectFileMpp {
    
    @GetMapping(value = "/dato")
    public String postJson(@RequestBody String payload) throws Exception{
        //Project project = new Project();
		File file = ResourceUtils.getFile("classpath:"+"010101010010101001010101001010100101.mpp");
        //System.out.println("Este es el archivo usado:"+ file);
        //System.out.println("Este json:"+ payload);
        //ProjectReader reader = new UniversalProjectReader();
        //ProjectFile archivo = reader.read(file)

        
        ProjectReader reader = new MPPReader();  
        ProjectFile projectObj = reader.read(file);

        JSONObject jsonObject= new JSONObject(payload);
        
        
        


        for(int i=0;i< ((JSONArray)(jsonObject.get("tareas"))).length();i++){
            try {

                JSONObject json = ((JSONArray)(jsonObject.get("tareas"))).getJSONObject(i);

                (projectObj.addTask()).setID(json.getInt("id"));
                (projectObj.getTaskByID(json.getInt("id"))).setName(json.getString("name"));
                (projectObj.getTaskByID(json.getInt("id"))).setUniqueID(json.getInt("uniqueID"));
                (projectObj.getTaskByID(json.getInt("id"))).setActive(json.getBoolean("estado"));
                System.out.print("Este es dato agregado:"+projectObj.getAllTasks());

            
                //Resource resource = new Resource();
                
                System.out.println();
                //resource.setName(json.getString("name"));
                ///resource.setId(json.getInt("id"));
                //projectObj.addResource().setName(json.getString("name"));
               // projectObj.addResource().setID(json.getInt("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        



        //projectObj.addResource();






        /*for (Task task : projectObj.getAllTasks())  
        {  
            System.out.println("Task: " + task.getName() + " ID=" + task.getID() + " Unique ID=" + task.getUniqueID());  
        } 
        for (Resource resource : projectObj.getAllResources())  
        {  
        System.out.println("Resource: " + resource.getName() + " (Unique ID=" + resource.getUniqueID() + ")");  
        } 

        List tasks = projectObj.getChildTasks();  
        Task task = (Task) tasks.get(0);
        tasks = task.getChildTasks();

        Resource r = projectObj.getResourceByUniqueID(Integer.valueOf(99));  
        Task t = projectObj.getTaskByUniqueID(Integer.valueOf(99)); 

        ProjectCalendarContainer defaultCalendar = projectObj.getCalendars();           
        ProjectCalendar taskCalendar = task.getCalendar(); */


       /* for (Task task2: projectObj.getAllTasks())  
        {  
            List predecessors = task2.getPredecessors();  
            if (predecessors != null && predecessors.isEmpty() == false)  
            {  
                System.out.println(task2.getName() + " predecessors:");  
                for (Relation relation: predecessors)  
                {  
                    System.out.println("   Task: " + file.getTaskByUniqueID(relation.getTaskUniqueID()).getName());  
                    System.out.println("   Type: " + relation.getType());  
                    System.out.println("   Lag: " + relation.getDuration());  
                }  
            }  
        } */

     
        ProjectWriter writer = ProjectWriterUtility.getProjectWriter("HOLA.mpx");  
        writer.write(projectObj,"HOLA.mpx");




        return "Hola Mundo";
    }

}




package com.example.ManagerProject.Object;

import java.io.File;

import org.springframework.util.ResourceUtils;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.UniversalProjectReader;

public class Project {
    public static String getRecursos(String urlString){
        String Recursos ="";
        return Recursos;
    }
    public static String getTareas(String urlString){
        String Tareas ="";
        
        File file = ResourceUtils.getFile("classpath:"+urlString);
        ProjectReader reader = new UniversalProjectReader() ();
        ProjectFile project = reader.read(file);
        for (Task task : project.getAllTasks())
        {
            Tareas = Tareas + "Task: " + task.getName() + " ID=" + task.getID() + " Unique ID=" + task.getUniqueID();
            
        }
        return Tareas;
    }
}
 
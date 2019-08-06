package com.example.ManagerProject.Object;

import java.io.File;

import org.springframework.util.ResourceUtils;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.Task;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.UniversalProjectReader;

public class Project  {
    public static String getRecursos(String urlString) throws Exception{
        String recursos ="";

        File file = ResourceUtils.getFile("classpath:"+urlString);
        ProjectReader reader = new UniversalProjectReader ();
        ProjectFile project = reader.read(file);
        for (Resource resource : project.getAllResources())
        {
            recursos = recursos + "Resource: " + resource.getName() + " (Unique ID=" + resource.getUniqueID() ;
        }
        return recursos;
    }
    public static String getTareas(String urlString)  throws Exception{
        String tareas ="";
        
        File file = ResourceUtils.getFile("classpath:"+urlString);
        ProjectReader reader = new UniversalProjectReader();
        ProjectFile project = reader.read(file);
        for (Task task : project.getAllTasks())
        {
            tareas = tareas + "Task: " + task.getName() + " ID=" + task.getID() + " Unique ID=" + task.getUniqueID();
            
        }
        return tareas;
    }

    public static String getCalendario(String urlString)  throws Exception{
        String tareas ="";
        
       
        return tareas;
    }

    public static String asignacionRecurso(String urlString)  throws Exception{
        String asignacion ="";
        
        File file = ResourceUtils.getFile("classpath:"+urlString);
        ProjectReader reader = new UniversalProjectReader();
        ProjectFile project = reader.read(file);
        for (Task task : project.getAllTasks())
        {
            asignacion = asignacion + "Task: " + task.getName() + " ID=" + task.getID() + " Unique ID=" + task.getUniqueID();
            
        }
        return asignacion;
    }

}
 
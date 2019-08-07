package com.example.ManagerProject.Object;

import org.springframework.util.ResourceUtils;

import net.sf.mpxj.Column;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Table;

import java.util.Iterator;
import java.util.List;
import java.io.File;

import net.sf.mpxj.Task;
import net.sf.mpxj.conceptdraw.schema.Document.Calendars.Calendar;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.UniversalProjectReader;

public class Project  {

    public static String getRecursos(ProjectFile project) throws Exception{
        String recursos ="[";

        for (Resource resource : project.getAllResources()){

            if(resource.getName() ==null) {}
            else{
                recursos = recursos + "{ " + "name :" + resource.getName() +" , "+ " id : " + resource.getUniqueID()  +" }"  + ",";
            }
            
        }

        if(recursos.length()>1){
            recursos = recursos.substring(0, recursos.length()-1) ;
        }
        else{}

        recursos = recursos + "]" ;
        return recursos;
    }
    
    public static String getTareas(ProjectFile project)  throws Exception{
        String tareas ="[";
    
        for (Task task : project.getAllTasks())
        {
            if(task.getName() ==null) {}

            else{
                tareas = tareas  + "{ " + "name :" +  task.getName() +" , "+ " id : " + task.getUniqueID() +"," + "recursos: " + asignacionesRecursosTarea(project,task.getUniqueID())  +" }"  + ",";
            }
        }

        if(tareas.length()>1){
            tareas = tareas.substring(0, tareas.length()-1) ;
        }
        else{}

        tareas = tareas + "]" ;

        return tareas;
    }

    public static String asignacionesRecursos(ProjectFile project)  throws Exception{
        String asignaciones ="[";

        for (ResourceAssignment assignment : project.getAllResourceAssignments()){
            Task task = assignment.getTask();
            int taskId;
            if (task == null){
                taskId = -1;
            }
            else{
                taskId = task.getUniqueID();
            }
            
            Resource resource = assignment.getResource();
            int resourceId;
            if (resource == null){
                resourceId = -1;
            }
            else{
                resourceId = resource.getUniqueID();
            }

            if(taskId == -1 || resourceId == -1) {}
            else{
                asignaciones = asignaciones  + "{ " + "idTask :" +  taskId +" , "+ " idResource : " + resourceId +" }"  + ",";
            }

        }
        if(asignaciones.length()>1){
            asignaciones = asignaciones.substring(0, asignaciones.length()-1) ;
        }
        else{}

        asignaciones = asignaciones + "]" ;
        return asignaciones;
    }

    public static String asignacionesRecursosTarea(ProjectFile project, int  idTask)  throws Exception{
       
        String asignaciones ="[";
        Task task = project.getTaskByID(idTask);
        for (ResourceAssignment assignment : task.getResourceAssignments())
        {
            Resource resource = assignment.getResource();
           

            if (resource == null){}
            else
            {
                asignaciones = asignaciones +  resource.getUniqueID()  + ",";
                
            }

            
        }
        if(asignaciones.length()>1){
            asignaciones = asignaciones.substring(0, asignaciones.length()-1) ;
        }
        else{}

        asignaciones = asignaciones + "]" ;
        return asignaciones;
    }

    public static String listHierarchyTask(ProjectFile file)
    {
        String tareasM ="["; 
        for (Task task : file.getChildTasks())
        {
            
                tareasM = tareasM + "{ " + " padreId : " + task.getName()  + " , " +  " hijosId : " + listHierarchy(task) + " }" + " ," ;
            
            
        }

        if(tareasM.length()>1){
            tareasM = tareasM.substring(0, tareasM.length()-1) ;
        }
        else{}

        tareasM = tareasM + "]" ;
        return tareasM;
    }

    private static String listHierarchy(Task task)
    {
        String tareasH ="";
        for (Task child : task.getChildTasks())
        {
            tareasH = tareasH + child.getName()+ "," + listHierarchy(child);
        }

        if(tareasH.length()>1){
            tareasH = tareasH.substring(0, tareasH.length()-1) ;
        }
        else{}

        
        return tareasH;
    }

    public static String recursosTareas(String urlString) throws Exception
    {
        File file = ResourceUtils.getFile("classpath:"+urlString);
        ProjectReader reader = new UniversalProjectReader();
        ProjectFile project = reader.read(file);

        String recursos = "";

        for (Resource resource : project.getAllResources())
        {
            recursos =  recursos + "Assignments for resource " + resource.getName() + ":";

            for (ResourceAssignment assignment : resource.getTaskAssignments())
            {
                Task task = assignment.getTask();
                recursos =  recursos +"   " + task.getName();
            }
        }
        return recursos;
        
    }

    public static String relacionesPredecesoraTareas(String urlString) throws Exception
    {
        String relacionPrecedecesora = "";


        File file = ResourceUtils.getFile("classpath:"+urlString);
        ProjectReader reader = new UniversalProjectReader();
        ProjectFile project = reader.read(file);

        for (Task task : project.getAllTasks())
        {
            List<Relation> predecessors = task.getPredecessors();
            if (predecessors != null && predecessors.isEmpty() == false)
            {
                relacionPrecedecesora = relacionPrecedecesora + task.getName() + " predecessors:";
                for (Relation relation : predecessors)
                {
                    relacionPrecedecesora = relacionPrecedecesora + "   Task: " + project.getTaskByUniqueID((relation.getTargetTask()).getID()).getName() ;
                    relacionPrecedecesora = relacionPrecedecesora + "   Type: " + relation.getType();
                    relacionPrecedecesora = relacionPrecedecesora + "   Lag: " + relation.getTargetTask().getDuration();
                }
            }
        }
        
        return relacionPrecedecesora;
    }

    public static String calendarios(String urlString) throws Exception
    {

        String tareas ="";
        
        File file = ResourceUtils.getFile("classpath:"+urlString);
        ProjectReader reader = new UniversalProjectReader();
        ProjectFile project = reader.read(file);
        for (Task task : project.getAllTasks())
        {
            tareas = tareas + "Duracion:"+  task.getDuration() + "Tarea:"+ task.getUniqueID() + "Inicio:"+ task.getStart() + "Fin:"+ task.getFinish();
            
        }
        return tareas;
        
    }

    public static void columnaNoName(String urlString) throws Exception
    {

        File file = ResourceUtils.getFile("classpath:"+urlString);
        ProjectReader reader = new UniversalProjectReader();
        ProjectFile project = reader.read(file);

        List<Table> tables= project.getTables();
        Iterator iter = tables.iterator();
        Table table = (Table)iter.next();
        
        List resources = project.getAllTasks();
        Iterator resourceIter = resources.iterator();
        while (resourceIter.hasNext()){
            Task resource = (Task)resourceIter.next();
            List columns = table.getColumns();
			Iterator columnIter = columns.iterator();
			Object columnValue = null;
            while (columnIter.hasNext()){
				Column column = (Column)columnIter.next();
				if (column.getFieldType().toString().equalsIgnoreCase("Duration")){
					columnValue = resource.getDuration();
				}else if (column.getFieldType().toString().equalsIgnoreCase("Start")){
					columnValue = resource.getStart();
				}else if (column.getFieldType().toString().equalsIgnoreCase("Finish")){
					columnValue = resource.getFinish();
				}else {
					columnValue = resource.getCachedValue(column.getFieldType());
				}                
                System.out.print(columnValue);
                System.out.print(",");
            }
            System.out.println("");
        }
            
    }




}
 
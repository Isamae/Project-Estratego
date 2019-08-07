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
    public static String getRecursos(String urlString) throws Exception{
        String recursos ="[";

        File file = ResourceUtils.getFile("classpath:"+urlString);
        ProjectReader reader = new UniversalProjectReader ();
        ProjectFile project = reader.read(file);
        for (Resource resource : project.getAllResources())
        {
            recursos = recursos + "{ " + "name :" + resource.getName() +" , "+ " id : " + resource.getUniqueID()  +" }"  + ",";
        }
        recursos = recursos + "]" ;
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

    public static String asignacionesRecursosArchivo(String urlString)  throws Exception{
        String asignaciones ="";
        
        File file = ResourceUtils.getFile("classpath:"+urlString);
        ProjectReader reader = new UniversalProjectReader();
        ProjectFile project = reader.read(file);
        for (ResourceAssignment assignment : project.getAllResourceAssignments())
        {
            Task task = assignment.getTask();
            String taskName;
            if (task == null)
            {
                taskName = "(null task)";
            }
            else
            {
                taskName = task.getName();
            }
            
            Resource resource = assignment.getResource();
            String resourceName;
            if (resource == null)
            {
                resourceName = "(null resource)";
            }
            else
            {
                resourceName = resource.getName();
            }
            

            asignaciones = asignaciones + "Assignment: Task=" + taskName + " Resource=" + resourceName;
        }
        return asignaciones;
    }

    public static String asignacionesRecursosTarea(String urlString)  throws Exception{
        String asignaciones ="";
        
        File file = ResourceUtils.getFile("classpath:"+urlString);
        ProjectReader reader = new UniversalProjectReader();
        ProjectFile project = reader.read(file);
        for (Task task : project.getAllTasks())
        {
            asignaciones = asignaciones + "Assignments for task " + task.getName() + ":";

            for (ResourceAssignment assignment : task.getResourceAssignments())
            {
                Resource resource = assignment.getResource();
                String resourceName;

                if (resource == null)
                {
                    resourceName = "(null resource)";
                }
                else
                {
                    resourceName = resource.getName();
                }

                asignaciones = asignaciones + "   " + resourceName;
            }
        }
        return asignaciones;
    }

    public static String jerarquiaTareas(String urlString)  throws Exception{
        
        File file = ResourceUtils.getFile("classpath:"+urlString);
        ProjectReader reader = new UniversalProjectReader();
        ProjectFile project = reader.read(file);
        return listHierarchy(project);
    }

    public static String listHierarchy(ProjectFile file)
    {
        String tareasM =""; 
        for (Task task : file.getChildTasks())
        {
            tareasM = tareasM +  "Task Padre: " + task.getName();
            tareasM = tareasM + listHierarchy(task, " ");
        }
        return tareasM;
        
    }

    private static String listHierarchy(Task task, String indent)
    {
        String tareasH ="";
        for (Task child : task.getChildTasks())
        {
            tareasH = tareasH  + "Task Hijo:" + child.getName();
            listHierarchy(child, indent + " ");
        }
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
 
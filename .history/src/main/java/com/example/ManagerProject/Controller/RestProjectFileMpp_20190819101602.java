

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
import java.util.Iterator;
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
     
		File file = ResourceUtils.getFile("classpath:"+"010101010010101001010101001010100101.mpp");
   

        
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
                //System.out.print("Este es dato agregado:"+projectObj.getAllTasks());
                //System.out.println();
              
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

        System.out.print("Este es dato agregado:"+ addColumnas(projectObj,jsonObject).getAllTasks());
        ProjectWriter writer = ProjectWriterUtility.getProjectWriter("HOLA.mpx");  
        writer.write(projectObj,"HOLA.mpx");




        return "Hola Mundo";
    }

    public static ProjectFile addColumnas(ProjectFile project,JSONObject jsonObject) throws Exception
    {
        
        List<Table> tables= project.getTables();
        tables.get(0);
        
        JSONObject json = ((JSONArray)(jsonObject.get("allColum"))).getJSONObject(0);
        //for(int i = 0; i < json.names().length() ; i++ ){
            tables.get(0).getColumns().get(0);
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
            
            }
           
           
        //}

        System.out.print(json.names());

        for(int i=0;i< ((JSONArray)(jsonObject.get("allColum"))).length();i++){
            try {

                JSONObject json2 = ((JSONArray)(jsonObject.get("allColum"))).getJSONObject(i);
                System.out.print(json2.names());
              
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        Iterator iter = tables.iterator();
        Table table = (Table)iter.next();
        String dataTable = " [ " ;
        List resources = project.getAllTasks();
        Iterator resourceIter = resources.iterator();
        while (resourceIter.hasNext()){
            Task resource = (Task)resourceIter.next();
            List columns = table.getColumns();
			Iterator columnIter = columns.iterator();
            Object columnValue = null;
            dataTable = dataTable  + " { " ; 
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
                dataTable = dataTable  + "'"+column.getFieldType().toString()+"'" + " : " + "'"+columnValue+"'" + " ,";
            
            }
            if(dataTable.length()>1){
                dataTable = dataTable.substring(0, dataTable.length()-1) ;
            }
            else{}

            dataTable = dataTable  + " } " + " ," ; 
        }
        if(dataTable.length()>1){
            dataTable = dataTable.substring(0, dataTable.length()-1) ;
        }
        else{}

        dataTable = dataTable + "]" ;
        return project;
            
    }

}




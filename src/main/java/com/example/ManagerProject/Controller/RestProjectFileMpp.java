

package com.example.ManagerProject.Controller;
import java.io.Console;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.omg.IOP.ENCODING_CDR_ENCAPS;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.core.subst.Token.Type;
import net.sf.mpxj.*;
import net.sf.mpxj.listener.FieldListener;
import net.sf.mpxj.reader.*;  
import net.sf.mpxj.writer.*;  
import net.sf.mpxj.mpp.*;
import net.sf.mpxj.planner.schema.Days;
import net.sf.mpxj.ganttproject.schema.Resource;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * RestProject
 */
@RestController
@RequestMapping(value = "/project")
public class RestProjectFileMpp {
    
    @GetMapping(value = "/datoJson")
    public String postJson(@RequestBody String payload) throws Exception{
     
		File file = ResourceUtils.getFile("classpath:"+"010101010010101001010101001010100101.mpp");
        
        ProjectReader reader = new MPPReader();  
        ProjectFile projectObj = reader.read(file);
        JSONObject jsonObject = null;
        try {
            jsonObject= new JSONObject(payload);
        } catch (Exception e) {
            System.out.println("Error al convertir Json: ");
                e.printStackTrace();
        }
        
        
        // for(int i=0;i< ((JSONArray)(jsonObject.get("tareas"))).length();i++){
        //     try {

        //         JSONObject json = ((JSONArray)(jsonObject.get("tareas"))).getJSONObject(i);

        //         (projectObj.addTask()).setID(json.getInt("id"));
        //         (projectObj.getTaskByID(json.getInt("id"))).setName(json.getString("name"));
        //         (projectObj.getTaskByID(json.getInt("id"))).setUniqueID(json.getInt("uniqueID"));
        //         (projectObj.getTaskByID(json.getInt("id"))).setActive(json.getBoolean("estado"));
        //         //System.out.print("Este es dato agregado:"+projectObj.getAllTasks());
        //         //System.out.println();
              
        //     } catch (JSONException e) {
        //         System.out.println("Error: ");
        //         e.printStackTrace();
        //     }
        // }
        /******************* */
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

        //addColumnas(projectObj,jsonObject).getAllTasks();
        projectObj = addCalendario(projectObj,jsonObject);

        ProjectWriter writer = ProjectWriterUtility.getProjectWriter("HOLA.mpx");  
        writer.write(projectObj,"HOLA.mpx");
        return "Hola Mundo";
    }

    public ProjectFile addCalendario(ProjectFile project,JSONObject jsonObject) throws Exception{
        JSONArray calendariosJson = ((JSONArray)(jsonObject.get("calendarios")));

        JSONArray recursosJson = ((JSONArray)(jsonObject.get("recursos")));
        for (int i=0; i<recursosJson.length(); i++){
            net.sf.mpxj.Resource resource = project.addResource();
            JSONObject j = recursosJson.getJSONObject(i);
            resource.setName(j.getString("name"));
            resource.setID(j.getInt("id"));
            
        }

        ProjectCalendar calendar = new ProjectCalendar(project);
        for(int i=0; i< calendariosJson.length(); i++){
            try {
                JSONObject json = calendariosJson.getJSONObject(i);
                // set calenderName
                calendar.setName(json.getString("nombre"));
                // set workingDays
                calendar = setDiasLaborables (json.getJSONArray("diaslab"), calendar);
                // setHours
                JSONArray horariosCalendario = json.getJSONArray("calenderHorario");
                calendar = setHorarioDia (horariosCalendario, calendar);
                // set exceptions
                JSONArray excepcionesCalendario = json.getJSONArray("calenderExcepciones");
                calendar = setExcepciones (excepcionesCalendario, calendar);
                
                project.addCalendar();
                project.addCalendar().setName(calendar.getName());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // List <ProjectCalendar> calendarios = project.getCalendars();
        // for (ProjectCalendar calendario : calendarios){

        // }



        // Table table = project.getTables().get(0);
        // List calendars = project.getCalendars();
        

        // //Iterator resourceIter = resources.iterator();
        // //while (resourceIter.hasNext()){
        //     //Task resource = (Task)resourceIter.next();
        //     List columns = table.getColumns();
		// 	Iterator columnIter = columns.iterator();
        //     Object columnValue = null;
        //     while (columnIter.hasNext()){
                
        //         Column column = (Column)columnIter.next();
        //         if(   json.names().  column.getFieldType().toString())
                
		// 		if (column.getFieldType().toString().equalsIgnoreCase("Duration")){
		// 			columnValue = resource.getDuration();
		// 		}else if (column.getFieldType().toString().equalsIgnoreCase("Start")){
		// 			columnValue = resource.getStart();
		// 		}else if (column.getFieldType().toString().equalsIgnoreCase("Finish")){
		// 			columnValue = resource.getFinish();
		// 		}else {
		// 			columnValue = resource.getCachedValue(column.getFieldType());
        //         }
            
        //     }
           

        //}
        return project;
            
    }

    public ProjectCalendar setDiasLaborables (JSONArray diasLab, ProjectCalendar calendario) throws JSONException {
        for (int i=0; i<diasLab.length(); i++){
            Day day = Day.valueOf(diasLab.get(i).toString());
            calendario.setWorkingDay(day, true);            
        }
        for (int i=1; i<8; i++){
            if (!calendario.isWorkingDay(Day.getInstance(i))){
                calendario.setWorkingDay(Day.getInstance(i), false);
            }
        }
        return calendario;      
    }

    public ProjectCalendar setHorarioDia (JSONArray horarioCalendario, ProjectCalendar calendario) throws JSONException, ParseException {
        for (int  i=0; i<horarioCalendario.length(); i++){
            String horarioStr = horarioCalendario.getString(i);

            String[] horario = horarioStr.split("/");
            String dayName = horario[0];
            String start1 = horario[1];
            String end1 = horario[2];
            String start2 = horario[3];
            String end2 = horario[4];

            Day day = Day.valueOf(dayName);
            DateRange dateRange = new DateRange(new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(start1), new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(end1));
            DateRange dateRange2 = new DateRange(new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(start2), new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(end2));
            
            ProjectCalendarHours hours = calendario.addCalendarHours(day);
           
            hours.addRange(dateRange);
            hours.addRange(dateRange2);

            /* ** */
            String h = "'";
            ProjectCalendarHours horas [] = calendario.getHours();
            for (ProjectCalendarHours hora : horas){
                if (hora!= null){
                    ProjectCalendarHours c = hora.getParentCalendar().getCalendarHours(day);
                    System.out.println("hora.getParentCalendar() " + hora.getParentCalendar());
                    h = h + hora.getDay();
                    for (int k=0; k<c.getRangeCount(); k++){
                        h = h + "/" + c.getRange(k).getStart()+"/" + c.getRange(k).getEnd();
                    }
                    h = h + "'";
                    
                }
                System.out.println(h);
                h = "'";
            }
            
        }
        return calendario;
    }

    public ProjectCalendar setExcepciones (JSONArray excepcionesCalendario, ProjectCalendar calendario) throws JSONException, ParseException {
        for (int  i=0; i<excepcionesCalendario.length(); i++){
            String exStr = excepcionesCalendario.getString(i);

            String[] excepcion = exStr.split("/");
            
            calendario.addCalendarException(new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(excepcion[1]), new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(excepcion[2]));
            ProjectCalendarException ex = calendario.getException(new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(excepcion[1]));
            ex.setName(excepcion[0]);
        }
        return calendario;
    }

 

}




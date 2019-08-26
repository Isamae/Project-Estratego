package com.example.ManagerProject.Controller;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;


import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.sf.mpxj.*;
import net.sf.mpxj.Resource;
import net.sf.mpxj.reader.*;  
import net.sf.mpxj.writer.*;  
import net.sf.mpxj.mpp.*;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.mspdi.SaveVersion;


/**
 * RestProject
 */
@RestController
@RequestMapping(value = "/project")
public class RestProjectFileMpp {
    String BASECALENDAR = "";
    
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
        
        /* * */
        ResourceContainer resourceContainer = projectObj.getResources();
        System.out.println(resourceContainer.size());
        for (int i=0; i<resourceContainer.size(); i++){
            resourceContainer.remove(i);
        }
        System.out.println(resourceContainer.size());
        ////
        try {
            JSONArray array = jsonObject.getJSONArray("recursos");
            for(int i=0; i< array.length();i++){
                Resource resource = projectObj.addResource();
                resource.setName(array.getJSONObject(i).getString("name"));
                resource.setID(array.getJSONObject(i).getInt("id"));
                System.out.println(resource.getName());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        projectObj.updateStructure();
        /////
        resourceContainer = projectObj.getResources();
        System.out.println(resourceContainer.size());
        for (int i=0; i<resourceContainer.size(); i++){
            System.out.println(resourceContainer.get(i).getName());
        }
        /* * */

        //addColumnas(projectObj, jsonObject).getAllTasks();
        
        JSONArray calendariosJson = jsonObject.getJSONArray("calendarios");

        // set default calendar
        System.out.println("\n\t SET CALENDARIO DEFAULT\n"); 
        ProjectCalendar defaultCalendario = projectObj.getDefaultCalendar();
        System.out.println("DEFAULT CALENDAR NAME --> " + defaultCalendario.getName());
        JSONObject defCalendarioJson = getDefaultCalendario(calendariosJson, defaultCalendario.getName());
        JSONObject parentJson = null;
        setCalendario(defaultCalendario, defCalendarioJson, parentJson);
        projectObj.setDefaultCalendar(defaultCalendario);
        // end set default calendar

        // add calendars
        System.out.println("\n\t ADD CALENDARIOS"); 
        ProjectCalendarContainer projectCalendars = projectObj.getCalendars();
        JSONObject calJson = null;
        for(int i=0; i< calendariosJson.length(); i++){
            calJson = calendariosJson.getJSONObject(i);
            if (!calJson.getString("nombre").equalsIgnoreCase(defaultCalendario.getName())){
                System.out.println("\n\t add new calendar");
                System.out.println("\n" + calJson.getString("nombre"));
                ProjectCalendar calendar = projectObj.addCalendar();
                addCalendario(calendar, calJson);
                //ProjectCalendar calendar = new ProjectCalendar(projectObj);
                //projectCalendars.add(calendar);
            }
        }
        // end add calendars

        ProjectCalendarContainer calendarContainer = projectObj.getCalendars();
        
        // add resources
        System.out.println("\n\t ADD RESOURCES"); 
        String id = "";
        for (int i=0; i<calendarContainer.size(); i++){
            ProjectCalendar calendar = calendarContainer.get(i);
            for (int j=0; j<calendariosJson.length(); j++){
                JSONObject json = calendariosJson.getJSONObject(j);
                if (calendar.getName().equalsIgnoreCase(json.getString("nombre"))){
                    id = json.getString("recursos");
                }
            }
            net.sf.mpxj.Resource resource = null;
            if (!id.equalsIgnoreCase("null")){
                resource = projectObj.getResourceByID(Integer.parseInt(id));
                System.out.println("resource get name: " + resource.getName());
                calendar.setResource(resource);
                System.out.println("set resource: " + calendar.getResource().getName());
            }
        }
        // end add resources

        // set calendarios base
        System.out.println("\n\t SET CALENDARIOS BASE\n");
        calendarContainer = projectObj.getCalendars();
        ProjectCalendar calendarParent = null;
        for (int i=0; i<calendarContainer.size(); i++){
            ProjectCalendar calendar = calendarContainer.get(i);
            System.out.println("calendar name: " + calendar.getName());
            for (int j=0; j<calendariosJson.length(); j++){
                JSONObject json = calendariosJson.getJSONObject(j);
                if (calendar.getName().equalsIgnoreCase(json.getString("nombre"))){
                    calendarParent = calendarContainer.getByName(json.getString("calenderBase"));
                    calendar.setParent(calendarParent);
                    
                    if (calendar.getParent() != null){
                        System.out.println(calendar.getParent().getName());
                    }else{
                        System.out.println("calendar.getParent().getName() = null");
                    }
                }
            }
            calendarContainer.set(i, calendar);
            System.out.println("calendar name: " + calendarContainer.get(i).getName());
        }
        // end set calendrios base

        // set calendarios
        System.out.println("\n\t SET CALENDARIOS\n");
        calendarContainer = projectObj.getCalendars();
        JSONObject jsonParent = null;
        //JSONObject calJson2 = null;
        //ProjectCalendar calendar = null;
        //ProjectCalendar calendarParent = null;
        for (int i=0; i<calendarContainer.size(); i++){
            System.out.println("\n\t set calendar");
            ProjectCalendar calendar = calendarContainer.get(i);
            for (int j=0; j<calendariosJson.length(); j++){
                JSONObject json = calendariosJson.getJSONObject(j);
                if (calendar.getName().equalsIgnoreCase(json.getString("nombre"))){
                    if (json.getString("nombre").equalsIgnoreCase(json.getString("calenderBase"))){
                        setCalendario(calendar, json, jsonParent);
                    }else{
                        jsonParent = getDefaultCalendario(calendariosJson, calendar.getParent().getName());
                        System.out.println("jsonParent: " + jsonParent.getString("nombre"));
                        setCalendario(calendar, json, jsonParent);
                    }
                }
            }
            calendarContainer.set(i, calendar);
            System.out.println("calendar name: " + calendarContainer.get(i).getName());
        }
        // end set calendarios

        MSPDIWriter writer = new MSPDIWriter();
        //writer.setSaveVersion(SaveVersion.Project2007);
        writer.write(projectObj, "HOLA.xml");

        // ProjectWriter writer = ProjectWriterUtility.getProjectWriter("HOLA.mpx");  
        // writer.write(projectObj,"HOLA.mpx");
        return "Hola Mundo";
    }

    public JSONObject getDefaultCalendario (JSONArray calendarios, String calendarName) throws JSONException {
        JSONObject json = null;
        for (int i =0; i<calendarios.length(); i++){
            json = calendarios.getJSONObject(i);
            if (json.getString("nombre").equalsIgnoreCase(calendarName)){
                return json;
            }
        } 
        return json;       
    }

    public void addCalendario(ProjectCalendar calendar, JSONObject json){
        try {
            // set calenderName
            calendar.setName(json.getString("nombre"));
            System.out.println("setName " + calendar.getName());
            //set calender id
            calendar.setUniqueID(json.getInt("calenderID"));
            System.out.println("setUniqueID " + calendar.getUniqueID());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCalendarioBase (ProjectCalendar calendar, ProjectCalendarContainer calendarContainer, JSONArray calendariosJson) throws JSONException {
        System.out.println("=========== setBaseCalendar ===========");
        ProjectCalendar calendarParent = null;
        for (int i=0; i<calendarContainer.size(); i++){
            //ProjectCalendar calendar = calendarContainer.get(i);
            ProjectCalendar cal = calendarContainer.get(i);
            for (int j=0; j<calendariosJson.length(); j++){
                JSONObject json = calendariosJson.getJSONObject(j);
                if (calendar.getName().equalsIgnoreCase(json.getString("nombre"))){
                    // if (json.getString("nombre").equalsIgnoreCase(json.getString("calenderBase"))){
                    //     calendarParent = calendar;
                    // }else{
                    //     calendarParent = calendarContainer.getByName(json.getString("calenderBase"));
                    // }
                    calendarParent = calendarContainer.getByName(json.getString("calenderBase"));
                    calendar.setParent(calendarParent);
                    
                    if (calendar.getParent() != null){
                        System.out.println(calendar.getParent().getName());
                    }else{
                        System.out.println("calendar.getParent().getName() = null");
                    }
                    
                }
            }
            calendarContainer.set(i, calendar);
            
        }
        // en
    }

    public void setCalendario(ProjectCalendar calendar,JSONObject json, JSONObject jsonParent) throws Exception{
        //ProjectCalendar calendar = project.addCalendar();
        //ProjectCalendar calendar = new ProjectCalendar(project);
        try {
            // set working Days
            setDayType (json.getJSONArray("diaslab"), calendar, DayType.WORKING);
            // set non working Days
            setDayType (json.getJSONArray("diasnolab"), calendar, DayType.NON_WORKING);
            // set default Days
            setDayType (json.getJSONArray("calenderDefault"), calendar, DayType.DEFAULT);
            // setHours
            JSONArray horariosCalendario = json.getJSONArray("calenderHorario");
            if (horariosCalendario.length() == 0){
                System.out.println(jsonParent.getString("nombre"));
                horariosCalendario = jsonParent.getJSONArray("calenderHorario");
                setHorario (horariosCalendario, calendar);
            }else{
                setHorario (horariosCalendario, calendar);
            }
            
            // set exceptions
            JSONArray excepcionesCalendario = json.getJSONArray("calenderExcepciones");
            setExcepciones (excepcionesCalendario, calendar);
            
            //project.addCalendar();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //return calendar; 
    }

    public ProjectCalendar setDayType (JSONArray dias, ProjectCalendar calendario, DayType dT) throws JSONException {
        System.out.println("=========== setDayType ===========");
        for (int i=0; i<dias.length(); i++){
            Day day = Day.valueOf(dias.get(i).toString());
            calendario.setWorkingDay(day, dT);         
        }
        System.out.println(dT);
        return calendario;      
    }

    public ProjectCalendar setHorario (JSONArray horarioCalendario, ProjectCalendar calendario) throws JSONException, ParseException {
        System.out.println("=========== setHorario ===========");
        for (int  i=0; i<horarioCalendario.length(); i++){
            // get horario from horarioCalendario
            String horarioStr = horarioCalendario.getString(i);

            String[] horario = horarioStr.split("/");

            Day day = Day.valueOf(horario[0]);
            DateRange dateRange = new DateRange(new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(horario[1]), new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(horario[2]));
            DateRange dateRange2 = new DateRange(new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(horario[3]), new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(horario[4]));
            
            ProjectCalendarHours hours = calendario.addCalendarHours(day);
            hours.addRange(dateRange);
            hours.addRange(dateRange2);
        }
        
        /* ** */
        ProjectCalendarHours horas [] = calendario.getHours();
        for (ProjectCalendarHours hora : horas){
            if (hora!= null){
                System.out.println(hora.getDay().toString() + "\n" + hora.getParentCalendar());
            }
        }
        /* ** */

        return calendario;
    }

    public ProjectCalendar setExcepciones (JSONArray excepcionesCalendario, ProjectCalendar calendario) throws JSONException, ParseException {
        System.out.println("=========== setExcepciones ===========");
        for (int  i=0; i<excepcionesCalendario.length(); i++){
            String exStr = excepcionesCalendario.getString(i);
            String[] excepcion = exStr.split("/");           
            calendario.addCalendarException(new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(excepcion[1]), new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(excepcion[2]));
            ProjectCalendarException ex = calendario.getException(new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(excepcion[1]));
            if (!excepcion[0].equals("null")){
                ex.setName(excepcion[0]);
            }
        }
        return calendario;
    }
}




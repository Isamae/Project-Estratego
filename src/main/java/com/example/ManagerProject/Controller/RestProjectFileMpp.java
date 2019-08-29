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
//import net.sf.mpxj.writer.*;  
import net.sf.mpxj.Task;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.reader.UniversalProjectReader;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * RestProject
 */
@RestController
@RequestMapping(value = "/project")
public class RestProjectFileMpp {
    String BASECALENDAR = "";
    
    @GetMapping(value = "/datoJson")
    public String postJson(@RequestBody String payload) throws Exception{
     
		File file = ResourceUtils.getFile("classpath:"+"Proyecto1.xml");
        UniversalProjectReader reader = new UniversalProjectReader();
        ProjectFile projectObj = reader.read(file);
    
        JSONObject jsonObject = null;
        try {
            jsonObject= new JSONObject(payload);
        } catch (Exception e) {
            System.out.println("Error al convertir Json: ");
            e.printStackTrace();
        }
        
        //addColumnas(projectObj, jsonObject).getAllTasks();

        //projectObj = addColumnas(projectObj,jsonObject);

        projectObj = setPropiedades(projectObj, jsonObject);

        projectObj =  addRecursos(projectObj,jsonObject);
        //projectObj = addDuracionProyecto(projectObj,jsonObject);
        projectObj = addTarea(projectObj,jsonObject);

        projectObj = addCalendario(projectObj,jsonObject);

        projectObj = addPredecesoras(projectObj,jsonObject);
        projectObj = addDuracionTareas(projectObj,jsonObject);
        
        //projectObj = addFechasTareas(projectObj,jsonObject);
        
        //projectObj = addHijosTarea(projectObj,jsonObject);
        
                                
        /*ProjectWriter writer = ProjectWriterUtility.getProjectWriter("HOLA.mpx");  
        writer.write(projectObj,"HOLA.mpx");*/
        MSPDIWriter writer = new MSPDIWriter();
        writer.write(projectObj, "hola.xml");
        return "Hola Mundo";
    }

    public ProjectFile setPropiedades (ProjectFile project, JSONObject json) throws JSONException, ParseException {
        Date startDate = new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(json.getString("PStartDate"));
        project.getProjectProperties().setStartDate(startDate);
        
        Date finishDate = new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(json.getString("PFinishDate"));
        project.getProjectProperties().setFinishDate(finishDate);

        if (json.getString("projectName").equalsIgnoreCase("null") && !json.getString("projectTittle").equalsIgnoreCase("null")){
            project.getProjectProperties().setName(json.getString("projectTittle"));
            project.getProjectProperties().setProjectTitle(json.getString("projectTittle"));
        }

        project.getProjectProperties().setCompany(json.getString("projectCompany"));

        project.getProjectProperties().setAuthor(json.getString("projectAuthor"));

        

        // System.out.println(project.getProjectProperties().getDefaultCalendarName());
        // JSONObject defCalendarioJson = json.getJSONArray("defaultCalendario").getJSONObject(0);
        // System.out.println(defCalendarioJson.getString("nombre"));
        // if (!project.getProjectProperties().getDefaultCalendarName().equalsIgnoreCase(defCalendarioJson.getString("nombre"))){
        //     project.getProjectProperties().setDefaultCalendarName(defCalendarioJson.getString("nombre"));
        // }
        // System.out.println(project.getProjectProperties().getDefaultCalendarName());
        return project;
    }
    
    public static ProjectFile addHijosTarea(ProjectFile project,JSONObject jsonObject) throws JSONException, ParseException {
        
        for(int i=0;i< ((JSONArray)(jsonObject.get("tareas"))).length();i++){
            try {

                JSONObject json = ((JSONArray)(jsonObject.get("tareas"))).getJSONObject(i);
                JSONArray hijos = json.getJSONArray("hijos");
                for(int j=0; j <hijos.length() ; j++){
                    JSONObject hijo = hijos.getJSONObject(j);
                    Task taskhijo = project.getTaskByID(hijo.getInt("id"));
                    (project.getTaskByID(json.getInt("id"))).addChildTask(taskhijo);
                }
                
            } catch (JSONException e) {
                e.printStackTrace();
            } 
        }
        project.updateStructure();
        return project;
    }

    public static ProjectFile addDuracionTareas(ProjectFile project,JSONObject jsonObject) throws JSONException{
        for(int i=0 ;i< ((JSONArray)(jsonObject.get("tareas"))).length();i++){
            try {
                JSONObject json = ((JSONArray)(jsonObject.get("tareas"))).getJSONObject(i);
                if(json.getJSONArray("hijos").length() == 0){
                    String duracion = json.getString("duracion");
                    Duration duracionD;
                    if(duracion.contains("h")){
                        duracionD = Duration.getInstance(Double.parseDouble(duracion.replace("h", "")), TimeUnit.HOURS);
                    }

                    else if(duracion.contains("m")){
                        duracionD = Duration.getInstance(Double.parseDouble(duracion.replace("m", "")), TimeUnit.MINUTES);
                    }

                    else if(duracion.contains("d")){
                        duracionD = Duration.getInstance(Double.parseDouble(duracion.replace("d", "")), TimeUnit.DAYS);
                    }
                    else if(duracion.contains("w")){
                        duracionD = Duration.getInstance(Double.parseDouble(duracion.replace("w", "")), TimeUnit.WEEKS);
                    }

                    else if(duracion.contains("M")){
                        duracionD = Duration.getInstance(Double.parseDouble(duracion.replace("M", "")), TimeUnit.MONTHS);
                    }
                    else if(duracion.contains("y")){
                        duracionD = Duration.getInstance(Double.parseDouble(duracion.replace("y", "")), TimeUnit.YEARS);
                    }
                    else{
                        duracionD = Duration.getInstance(Double.parseDouble(duracion.replace("p", "")), TimeUnit.PERCENT);
                    }


                    String Actualduracion = json.getString("ActualDuration");
                    Duration duracionA;
                    if(Actualduracion.contains("h")){
                        duracionA = Duration.getInstance(Double.parseDouble(Actualduracion.replace("h", "")), TimeUnit.HOURS);
                    }

                    else if(Actualduracion.contains("m")){
                        duracionA = Duration.getInstance(Double.parseDouble(Actualduracion.replace("m", "")), TimeUnit.MINUTES);
                    }

                    else if(Actualduracion.contains("d")){
                        duracionA = Duration.getInstance(Double.parseDouble(Actualduracion.replace("d", "")), TimeUnit.DAYS);
                    }
                    else if(Actualduracion.contains("w")){
                        duracionA = Duration.getInstance(Double.parseDouble(Actualduracion.replace("w", "")), TimeUnit.WEEKS);
                    }

                    else if(Actualduracion.contains("M")){
                        duracionA = Duration.getInstance(Double.parseDouble(Actualduracion.replace("M", "")), TimeUnit.MONTHS);
                    }
                    else if(Actualduracion.contains("y")){
                        duracionA = Duration.getInstance(Double.parseDouble(Actualduracion.replace("y", "")), TimeUnit.YEARS);
                    }
                    else{
                        duracionA = Duration.getInstance(Double.parseDouble(Actualduracion.replace("p", "")), TimeUnit.PERCENT);
                    }

                    String ActualTrabajo = json.getString("ActualDuration");
                    Duration actualT;
                    if(ActualTrabajo.contains("h")){
                        actualT = Duration.getInstance(Double.parseDouble(ActualTrabajo.replace("h", "")), TimeUnit.HOURS);
                    }

                    else if(ActualTrabajo.contains("m")){
                        actualT = Duration.getInstance(Double.parseDouble(ActualTrabajo.replace("m", "")), TimeUnit.MINUTES);
                    }

                    else if(ActualTrabajo.contains("d")){
                        actualT = Duration.getInstance(Double.parseDouble(ActualTrabajo.replace("d", "")), TimeUnit.DAYS);
                    }
                    else if(ActualTrabajo.contains("w")){
                        actualT = Duration.getInstance(Double.parseDouble(ActualTrabajo.replace("w", "")), TimeUnit.WEEKS);
                    }

                    else if(ActualTrabajo.contains("M")){
                        actualT = Duration.getInstance(Double.parseDouble(ActualTrabajo.replace("M", "")), TimeUnit.MONTHS);
                    }
                    else if(ActualTrabajo.contains("y")){
                        actualT = Duration.getInstance(Double.parseDouble(ActualTrabajo.replace("y", "")), TimeUnit.YEARS);
                    }
                    else{
                        actualT = Duration.getInstance(Double.parseDouble(ActualTrabajo.replace("p", "")), TimeUnit.PERCENT);
                    }

                    System.out.println("ID :" +json.getInt("id") +"-> " + duracionD);
                    project.getTaskByID(json.getInt("id")).setDuration(duracionD);
                    project.getTaskByID(json.getInt("id")).setActualDuration(duracionA);
                    project.getTaskByID(json.getInt("id")).setActualWork(actualT);
                    project.getTaskByID(json.getInt("id")).setDurationText(duracion);
                }
                else{

                }

            } catch (JSONException e) {
                e.printStackTrace();
            } 
        }
        project.updateStructure();
        return project;
    }

    public static ProjectFile addFechasTareas(ProjectFile project,JSONObject jsonObject) throws JSONException{
       
        SimpleDateFormat df = new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy");
        for(int i=0;i< ((JSONArray)(jsonObject.get("tareas"))).length();i++){
            try {
                
                JSONObject json = ((JSONArray)(jsonObject.get("tareas"))).getJSONObject(i);
                //System.out.println(project.getTaskByID(json.getInt("id")));
                project.getTaskByID(json.getInt("id")).setActualStart(df.parse(json.getString("AfechaInicio")));
                project.getTaskByID(json.getInt("id")).setStart(df.parse(json.getString("fechaInicio")));
                project.getTaskByID(json.getInt("id")).setStartText(json.getString("TfechaInicio"));
               

                project.getTaskByID(json.getInt("id")).setActualFinish(df.parse(json.getString("AfechaFin")));
                project.getTaskByID(json.getInt("id")).setFinish(df.parse(json.getString("fechaFin")));
                project.getTaskByID(json.getInt("id")).setFinishText(json.getString("TfechaFin"));
                
                
                //(project.getTaskByID(json.getInt("id"))).addRecurringTask().setFinishDate(df.parse(json.getString("fechaFin")));

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e){
                e.printStackTrace();
            }
        }
        project.updateStructure();
        return project;
    }

    public static ProjectFile addTarea(ProjectFile project,JSONObject jsonObject) throws JSONException, ParseException {

        
        project.getAllTasks().get(0).setName(((JSONArray)(jsonObject.get("tareas"))).getJSONObject(0).getString("name"));
        project.getAllTasks().get(0).setUniqueID(((JSONArray)(jsonObject.get("tareas"))).getJSONObject(0).getInt("uniqueID"));
        project.getAllTasks().get(0).setActive(((JSONArray)(jsonObject.get("tareas"))).getJSONObject(0).getBoolean("estado"));
        project.getAllTasks().get(0).setOutlineNumber(((JSONArray)(jsonObject.get("tareas"))).getJSONObject(0).getString("OutlineNumber"));
        project.getAllTasks().get(0).setOutlineLevel(((JSONArray)(jsonObject.get("tareas"))).getJSONObject(0).getInt("OutlineLevel"));
       
        for(int i=0 ;i< ((JSONArray)(jsonObject.get("tareas"))).length();i++){
            try {
                JSONObject json = ((JSONArray)(jsonObject.get("tareas"))).getJSONObject(i);
                if( project.getTaskByID(json.getInt("id")) ==  null){

                    (project.addTask()).setID(json.getInt("id"));
                    (project.getTaskByID(json.getInt("id"))).setName(json.getString("name"));
                    (project.getTaskByID(json.getInt("id"))).setUniqueID(json.getInt("uniqueID"));
                    (project.getTaskByID(json.getInt("id"))).setActive(json.getBoolean("estado"));
                    (project.getTaskByID(json.getInt("id"))).setOutlineNumber(json.getString("OutlineNumber"));
                    (project.getTaskByID(json.getInt("id"))).setOutlineLevel(json.getInt("OutlineLevel"));
                    TaskType taskType;
                    if(json.getString("Type").equalsIgnoreCase("FIXED_DURATION")){
                        taskType = TaskType.FIXED_DURATION;
                    }
                    else if(json.getString("Type").equalsIgnoreCase("FIXED_UNITS")){
                        taskType = TaskType.FIXED_UNITS;
                    }
                    else{
                        taskType = TaskType.FIXED_WORK;
                    }

                    (project.getTaskByID(json.getInt("id"))).setType(taskType);

                    
                    JSONArray hijos = json.getJSONArray("hijos");
                    for(int j=0; j <hijos.length() ; j++){
                        JSONObject hijo = hijos.getJSONObject(j);
                        Task taskhijo = project.addTask();
                      
                        taskhijo.setID(hijo.getInt("id"));
                        taskhijo.setName(hijo.getString("name"));
                        taskhijo.setUniqueID(hijo.getInt("idUnique"));
                        taskhijo.setOutlineNumber(hijo.getString("OutlineNumber"));
                        taskhijo.setOutlineLevel(hijo.getInt("OutlineLevel"));
                        
                        
                        TaskType taskTypeH;
                        if(hijo.getString("Type").equalsIgnoreCase("FIXED_DURATION")){
                            taskTypeH = TaskType.FIXED_DURATION;
                        }
                        else if(hijo.getString("Type").equalsIgnoreCase("FIXED_UNITS")){
                            taskTypeH = TaskType.FIXED_UNITS;
                        }
                        else{
                            taskTypeH = TaskType.FIXED_WORK;
                        }

                        taskhijo.setType(taskTypeH);


                        if(json.getInt("id") == 0){
                        }
                        else{
                            //taskhijo.generateWBS(project.getTaskByID(json.getInt("id")));
                            taskhijo.generateOutlineNumber(project.getTaskByID(json.getInt("id")));
                        }


                        project.getTaskByID(json.getInt("id")).addChildTask(taskhijo);
                    }

                }
                else{
                    JSONArray hijos = json.getJSONArray("hijos");
                    for(int j=0; j <hijos.length() ; j++){
                        JSONObject hijo = hijos.getJSONObject(j);
                        Task taskhijo = project.addTask();
                        taskhijo.setID(hijo.getInt("id"));
                        taskhijo.setName(hijo.getString("name"));
                        taskhijo.setUniqueID(hijo.getInt("idUnique"));
                        taskhijo.setOutlineNumber(hijo.getString("OutlineNumber"));
                        taskhijo.setOutlineLevel(hijo.getInt("OutlineLevel"));
                        
                        TaskType taskTypeH;
                        if(hijo.getString("Type").equalsIgnoreCase("FIXED_DURATION")){
                            taskTypeH = TaskType.FIXED_DURATION;
                        }
                        else if(hijo.getString("Type").equalsIgnoreCase("FIXED_UNITS")){
                            taskTypeH = TaskType.FIXED_UNITS;
                        }
                        else{
                            taskTypeH = TaskType.FIXED_WORK;
                        }

                        taskhijo.setType(taskTypeH);

                        if(json.getInt("id") == 0){
                        }
                        else{
                            //taskhijo.generateWBS(project.getTaskByID(json.getInt("id")));
                            taskhijo.generateOutlineNumber(project.getTaskByID(json.getInt("id")));
                        }
                        project.getTaskByID(json.getInt("id")).addChildTask(taskhijo);

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } 
        }
        project.updateStructure();
        return project;
    }

    public static ProjectFile addPredecesoras(ProjectFile project,JSONObject jsonObject) throws JSONException, ParseException{
        for(int i=0;i< ((JSONArray)(jsonObject.get("tareas"))).length();i++){
            try {
                JSONObject json = ((JSONArray)(jsonObject.get("tareas"))).getJSONObject(i);
                JSONArray array = json.getJSONArray("predecesoras");
                Task task  = project.getTaskByID(json.getInt("id"));
                for(int x=0; x <array.length(); x++){
                    JSONObject predecesor = array.getJSONObject(x);
                    String lag = predecesor.getString("lag"); 
                    String tipo  = predecesor.getString("type"); 
                    String idH = predecesor.getString("taskId"); 

                    RelationType relationType; 
                    if(tipo.contains("FF")){
                        relationType = RelationType.FINISH_FINISH;
                    }
                    else if(tipo.contains("FS")){
                        relationType = RelationType.FINISH_START;
                    }
                    else if(tipo.contains("SS")){
                        relationType = RelationType.START_START;
                    }
                    else{
                        relationType = RelationType.START_FINISH;
                    }


                    if(lag.contains("d")){
                        task.addPredecessor( project.getTaskByID(Integer.parseInt(idH)), relationType,  Duration.getInstance(Double.parseDouble(lag.replace("d", "")),TimeUnit.DAYS));
                    }
                    else if(lag.contains("h")){
                        task.addPredecessor( project.getTaskByID(Integer.parseInt(idH)), relationType,  Duration.getInstance(Double.parseDouble(lag.replace("h", "")),TimeUnit.HOURS));
                    }
                    else if(lag.contains("y")){
                        task.addPredecessor( project.getTaskByID(Integer.parseInt(idH)), relationType,  Duration.getInstance(Double.parseDouble(lag.replace("y", "")),TimeUnit.YEARS));
                    }
                    else if(lag.contains("w")){
                        task.addPredecessor( project.getTaskByID(Integer.parseInt(idH)), relationType,  Duration.getInstance(Double.parseDouble(lag.replace("w", "")),TimeUnit.WEEKS));
                    }
                    else if(lag.contains("m")){
                        task.addPredecessor( project.getTaskByID(Integer.parseInt(idH)), relationType,  Duration.getInstance(Double.parseDouble(lag.replace("m", "")),TimeUnit.MINUTES));
                    }
                    else if(lag.contains("M")){
                        task.addPredecessor( project.getTaskByID(Integer.parseInt(idH)), relationType,  Duration.getInstance(Double.parseDouble(lag.replace("M", "")),TimeUnit.MONTHS));
                    }
                    else{
                        task.addPredecessor( project.getTaskByID(Integer.parseInt(idH)), relationType,  Duration.getInstance(Double.parseDouble(lag.replace("p", "")),TimeUnit.PERCENT));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } 
        }
        project.updateStructure();
        return project;
    }

    public static ProjectFile addSucesores(ProjectFile project,JSONObject jsonObject) throws JSONException, ParseException{
        SimpleDateFormat df = new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy");
        JSONObject jsonObject2 = ((JSONObject)(jsonObject.get("allColum")));
        for(int i=0;i< ((JSONArray)(jsonObject.get("tareas"))).length();i++){
            try {
                JSONObject json = ((JSONArray)(jsonObject.get("tareas"))).getJSONObject(i);
                JSONArray array = json.getJSONArray("Sucesores");
                Task task  = project.getTaskByID(json.getInt("id"));
                for(int x=0; x <array.length(); x++){
                    JSONObject sucesores = array.getJSONObject(x);
                    String lag = sucesores.getString("lag"); 
                    String tipo  = sucesores.getString("type"); 
                    String idH = sucesores.getString("taskId"); 

                    RelationType relationType; 
                    if(tipo.contains("FF")){
                        relationType = RelationType.FINISH_FINISH;
                    }
                    else if(tipo.contains("FS")){
                        relationType = RelationType.FINISH_START;
                    }
                    else if(tipo.contains("SS")){
                        relationType = RelationType.START_START;
                    }
                    else{
                        relationType = RelationType.START_FINISH;
                    }


                    if(lag.contains("d")){
                        //task.getSuccessors().add ( project.getTaskByID(Integer.parseInt(idH)), relationType,  Duration.getInstance(Double.parseDouble(lag.replace("d", "")),TimeUnit.DAYS));
                    }
                    else if(lag.contains("h")){
                        task.addPredecessor( project.getTaskByID(Integer.parseInt(idH)), relationType,  Duration.getInstance(Double.parseDouble(lag.replace("h", "")),TimeUnit.HOURS));
                    }
                    else if(lag.contains("y")){
                        task.addPredecessor( project.getTaskByID(Integer.parseInt(idH)), relationType,  Duration.getInstance(Double.parseDouble(lag.replace("y", "")),TimeUnit.YEARS));
                    }
                    else if(lag.contains("w")){
                        task.addPredecessor( project.getTaskByID(Integer.parseInt(idH)), relationType,  Duration.getInstance(Double.parseDouble(lag.replace("w", "")),TimeUnit.WEEKS));
                    }
                    else if(lag.contains("m")){
                        task.addPredecessor( project.getTaskByID(Integer.parseInt(idH)), relationType,  Duration.getInstance(Double.parseDouble(lag.replace("m", "")),TimeUnit.MINUTES));
                    }
                    else if(lag.contains("M")){
                        task.addPredecessor( project.getTaskByID(Integer.parseInt(idH)), relationType,  Duration.getInstance(Double.parseDouble(lag.replace("M", "")),TimeUnit.MONTHS));
                    }
                    else{
                        task.addPredecessor( project.getTaskByID(Integer.parseInt(idH)), relationType,  Duration.getInstance(Double.parseDouble(lag.replace("p", "")),TimeUnit.PERCENT));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } 
        }
        project.updateStructure();
        return project;
    }

    public static  ProjectFile addDuracionProyecto(ProjectFile project,JSONObject jsonObject) throws JSONException, ParseException{
        
        SimpleDateFormat df = new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy");
        
        /*
        ProjectReader mppReader = new MPPReader();
        ProjectFile project = mppReader.read(filePath);
        ProjectHeader projectHeader = project.getProjectHeader();
        projectHeader.setStartDate("currentDate");*/
        
        System.out.println(project.getProjectProperties().getStartDate());

        if(jsonObject.getString("StartDate")!="null"){
            
            //project.getStartDate().setTime(df.parse(jsonObject.getString("StartDate")).getTime());
            //project.getStartDate().setSeconds(df.parse(jsonObject.getString("StartDate")).getSeconds());
            //project.getStartDate().setMinutes(df.parse(jsonObject.getString("StartDate")).getMinutes());
            //project.getStartDate().setHours(df.parse(jsonObject.getString("StartDate")).getHours());
            //project.getStartDate().setMonth(df.parse(jsonObject.getString("StartDate")).getMonth());
            //project.getStartDate().setYear(Integer.parseInt(jsonObject.getString("StartDate").split(" ")[5]));            
            
            //project.getStartDate().before(df.parse(jsonObject.getString("StartDate")));
            
            //System.out.println(df.parse(jsonObject.getString("StartDate")).getTime());
            //System.out.println(df.parse(jsonObject.getString("StartDate")).getSeconds());
            //System.out.println(df.parse(jsonObject.getString("StartDate")).getMinutes());
            //System.out.println(df.parse(jsonObject.getString("StartDate")).getHours());
            //System.out.println(df.parse(jsonObject.getString("StartDate")).getMonth());
            //System.out.println(df.parse(jsonObject.getString("StartDate")).getYear());
            

        }
        if(jsonObject.getString("FinishDate")!="null"){
            //project.getFinishDate().setTime(df.parse(jsonObject.getString("FinishDate")).getTime());
            //project.getFinishDate().setSeconds(df.parse(jsonObject.getString("FinishDate")).getSeconds());
            //project.getFinishDate().setMinutes(df.parse(jsonObject.getString("FinishDate")).getMinutes());
            //project.getFinishDate().setHours(df.parse(jsonObject.getString("FinishDate")).getHours());
            //project.getFinishDate().setMonth(df.parse(jsonObject.getString("FinishDate")).getMonth());
            //project.getFinishDate().setYear(df.parse(jsonObject.getString("FinishDate")).getYear());
        }
        if(jsonObject.getString("PStartDate")!="null"){
            project.getProjectProperties().getStartDate().setTime(df.parse(jsonObject.getString("StartDate")).getTime());
            project.getProjectProperties().getStartDate().setSeconds(df.parse(jsonObject.getString("StartDate")).getSeconds());
            project.getProjectProperties().getStartDate().setMinutes(df.parse(jsonObject.getString("StartDate")).getMinutes());
            project.getProjectProperties().getStartDate().setHours(df.parse(jsonObject.getString("StartDate")).getHours());
            project.getProjectProperties().getStartDate().setMonth(df.parse(jsonObject.getString("StartDate")).getMonth());
            project.getProjectProperties().getStartDate().setYear(df.parse(jsonObject.getString("StartDate")).getYear());
        }
        if(jsonObject.getString("PFinishDate")!="null"){
           project.getProjectProperties().getFinishDate().setTime(df.parse(jsonObject.getString("FinishDate")).getTime());
           project.getProjectProperties().getFinishDate().setSeconds(df.parse(jsonObject.getString("FinishDate")).getSeconds());
           project.getProjectProperties().getFinishDate().setMinutes(df.parse(jsonObject.getString("FinishDate")).getMinutes());
           project.getProjectProperties().getFinishDate().setHours(df.parse(jsonObject.getString("FinishDate")).getHours());
           project.getProjectProperties().getFinishDate().setMonth(df.parse(jsonObject.getString("FinishDate")).getMonth());
           project.getProjectProperties().getFinishDate().setYear(df.parse(jsonObject.getString("FinishDate")).getYear());
        }
        System.out.println(project.getProjectProperties().getStartDate());
        //System.out.println(project.getProjectProperties().getStartDate());
        //System.out.println(project.getProjectProperties().getFinishDate());
        //System.out.println(project.getFinishDate());
        //System.out.println(project.getStartDate());
        project.updateStructure();
        return project;
    }

    public static ProjectFile addRecursos(ProjectFile project,JSONObject jsonObject){
        try {
            JSONArray array = (JSONArray) jsonObject.get("recursos");
            for(int i=0; i< array.length();i++){
                Resource resource = project.addResource();
                resource.setName(((JSONObject)array.get(i)).getString("name"));
                resource.setID(((JSONObject)array.get(i)).getInt("id"));
                resource.setUniqueID(((JSONObject)array.get(i)).getInt("idUni"));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.print("Si actualiza");
        project.updateStructure();
        return project;
    }

    public static ProjectFile addColumnas(ProjectFile project,JSONObject jsonObject) throws Exception
    {
        
        JSONObject json = ((JSONArray)(jsonObject.get("allColum"))).getJSONObject(0);
        System.out.println("Estas son las tabla:" +  project.getTables().get(0));
        CustomFieldContainer fields = project.getCustomFields();
        System.out.println("El tamanao de los campos personalizados es:"+ fields.size());
        //CustomFieldValueItem customFieldValueItem = new CustomFieldValueItem(617);
        //fields.registerValue(customFieldValueItem);
    
       
        FieldType fieldType = new FieldType(){
        
            @Override
            public int getValue() {
                return 62;
            }
        
            @Override
            public String name() {
                return "RESOURCE_NAMES";
            }
        
            @Override
            public FieldType getUnitsType() {
                return null;
            }
        
            @Override
            public String getName(Locale locale) {
                return locale.getDisplayName();
            }
        
            @Override
            public String getName() {
                return "Resource Names";
            }
        
            @Override
            public FieldTypeClass getFieldTypeClass() {
                return FieldTypeClass.TASK;
            }
        
            @Override
            public DataType getDataType() {
                return DataType.STRING;
            }
        };
        CustomField customField = new CustomField(fieldType, project.getCustomFields());
        
        /*FieldContainer container = new FieldContainer(){
        
            @Override
            public void set(FieldType field, Object value) {
                
            }
        
            @Override
            public void removeFieldListener(FieldListener listener) {
                
            }
        
            @Override
            public Object getCurrentValue(FieldType field) {
                return null;
            }
        
            @Override
            public Object getCachedValue(FieldType field) {
                return null;
            }
        
            @Override
            public void addFieldListener(FieldListener listener) {
                
            }
        };*/
       
       

        
        Column column = new Column(project);
        column.setFieldType(customField.getFieldType());
        column.setTitle("Hola Mundo");
        column.setWidth(14);
        project.getTables().get(0).getColumns().add(column);
        
        /*CustomFieldValueItem item = new CustomFieldValueItem(7);
        item.setDescription("hola Mundo");
        item.setParent(200);*/
        fields.getCustomField(fieldType);

        for (CustomField field :project.getCustomFields())
        {
            //fields.getCustomField(field.getFieldType()).setAlias("hola");
            System.out.println("Field: " + field);
            System.out.println("Typo, getName , name,  value , unidades , clase: " + field.getFieldType().getDataType() 
            + "     "  +field.getFieldType().getName()
            + "     "  +field.getFieldType().name()
            + "     "  +field.getFieldType().getValue()
            + "     "  +field.getFieldType().getUnitsType()
            + "     "  +field.getFieldType().getFieldTypeClass());
        }
        //project.getProjectProperties();
        //project.getProjectConfig();
        //project.getEventManager();

        System.out.println("El tamanao de los campos personalizados es:"+ project.getCustomFields().size());

        
        /*for (CustomField field :project.getCustomFields())
        {
            //fields.getCustomField(field.getFieldType()).setAlias("hola");
            System.out.println("Field: " + field);
            System.out.println("Typo, getName , name,  value , unidades , clase: " + field.getFieldType().getDataType() 
            + "     "  +field.getFieldType().getName()
            + "     "  +field.getFieldType().name()
            + "     "  +field.getFieldType().getValue()
            + "     "  +field.getFieldType().getUnitsType()
            + "     "  +field.getFieldType().getFieldTypeClass());
        }*/

        /*Table table = project.getTables().get(0);
        project.getTables().remove(table);
        

        Table table2 = new Table();
        table2.setName("Table1");
        table2.setID(1);
        for(int i = 0; i< json.names().length(); i ++){
            Column column = new Column(project);
          
            column.setTitle((String)json.names().get(i));

            
            
            //column.setFieldType(fieldType);
            table2.addColumn(column);
        }
        project.getTables().add(table2);*/
        return project;
        
    }
    
    public static ProjectFile addDataTablas(ProjectFile project,JSONObject jsonObject) throws Exception{
        JSONArray jsonClaves = ((JSONArray)(jsonObject.get("allColum"))).getJSONObject(0).names();
        
        Table table = project.getTables().get(0);
        try {
            for (int i = 0; i < ((JSONArray) (jsonObject.get("allColum"))).length(); i++) {
                JSONObject object =  ((JSONArray) (jsonObject.get("allColum"))).getJSONObject(i);

                for(int j=0; j<jsonClaves.length(); j++){
                   // System.out.println("Clase: " + object.get((String)jsonClaves.get(j)));
                    Column column = new Column(project);
                    
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return project;
    }
   
    public static int containsPalabra(JSONArray findArray, String palabra) {
        int result = -1;
        
        for(int i=0; i< findArray.length(); i++){
            try {
                if (((String) findArray.get(i)).equals(palabra)) {
                    result = i;
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
           
        }
        return result;
    } 
  
    public ProjectFile addCalendario(ProjectFile project,JSONObject jsonObject) throws Exception{
        ProjectCalendarContainer container = project.getCalendars();
        for (int i=0; i<container.size(); i++){
            container.remove(i);
        }
        
        JSONArray calendariosJson = jsonObject.getJSONArray("calendarios");
        
        // set default calendar
        project = setDefaultCalendario (project, jsonObject);

        // set base line calendar
        project = setBaselineCalendario (project, jsonObject);

        // add calendars
        project = addCalendarios(project, calendariosJson);

        // link calendars to resources
        project = linkCalendarioRecurso (project, calendariosJson);
        
        // set base calendars
        project = setCalendariosBase (project, calendariosJson);

        // set calendarios
        project = setCalendarios (project, calendariosJson);

        project.updateStructure();

        return project;
    }

    public ProjectFile setDefaultCalendario (ProjectFile project, JSONObject json) throws Exception {
        System.out.println("\n\t SET CALENDARIO DEFAULT\n");
        JSONObject calendarioJson = json.getJSONArray("defaultCalendario").getJSONObject(0);
        ProjectCalendar defaultCalendario = project.getDefaultCalendar();
        // set calenderName
        defaultCalendario.setName(calendarioJson.getString("nombre"));
        // set uniqueID
        defaultCalendario.setUniqueID(calendarioJson.getInt("calenderID"));

        setCalendario(defaultCalendario, calendarioJson, null);
        project.setDefaultCalendar(defaultCalendario);
        System.out.println("Default calendar name " + project.getDefaultCalendar().getName() + ", UniqueID " + project.getDefaultCalendar().getUniqueID());
        return project;
    }

    public ProjectFile setBaselineCalendario (ProjectFile project, JSONObject json) throws Exception {
        System.out.println("\n\t SET BASE LIINE CALENDARIO \n");
        JSONObject calendarioJson = json.getJSONArray("baselineCalendario").getJSONObject(0);
        ProjectCalendar baselineCalendario = project.getBaselineCalendar();
        // set calenderName
        baselineCalendario.setName(calendarioJson.getString("nombre"));
        // set uniqueID
        baselineCalendario.setUniqueID(calendarioJson.getInt("calenderID"));

        setCalendario(baselineCalendario, calendarioJson, null);
        project.setDefaultCalendar(baselineCalendario);
        System.out.println("Baseline calendar name " + project.getDefaultCalendar().getName() + ", UniqueID " + project.getDefaultCalendar().getUniqueID());
        return project;
    }

    public ProjectFile addCalendarios(ProjectFile project, JSONArray calendariosJson) throws JSONException {
        System.out.println("\n\t ADD CALENDARIOS");
        ProjectCalendar defaultCalendario = project.getDefaultCalendar();
        for(int i=0; i< calendariosJson.length(); i++){
            JSONObject json = calendariosJson.getJSONObject(i);
            if (!json.getString("nombre").equalsIgnoreCase(defaultCalendario.getName())){
                System.out.println("\n\t add new calendar");
                System.out.println("\njson name: " + json.getString("nombre"));
                ProjectCalendar calendar = project.addCalendar();
                // set calenderName
                calendar.setName(json.getString("nombre"));
                //set calender id
                calendar.setUniqueID(json.getInt("calenderID"));
                System.out.println("setName " + calendar.getName() + ", setUniqueID " + calendar.getUniqueID());
            }
        }

        if (){
            
        }

        return project;
    }

    public ProjectFile linkCalendarioRecurso (ProjectFile project, JSONArray calendariosJson) throws JSONException {
        System.out.println("\n\t LINK RESOURCES"); 
        String id = "";
        JSONObject json = null;
        ProjectCalendar calendar = null;
        for (int i=0; i<calendariosJson.length(); i++){
            json = calendariosJson.getJSONObject(i);
            calendar = project.getCalendarByName(json.getString("nombre"));
            id = json.getString("recursoID");
            if (calendar != null){
                Resource resource = null;
                if (!id.equalsIgnoreCase("null") && !id.equalsIgnoreCase("")){
                    resource = project.getResourceByID(Integer.parseInt(id));
                    if (resource != null){
                        calendar.setResource(resource);
                        System.out.println("set resource: " + calendar.getResource().getName());
                    }else{
                        System.out.println("resource null");
                    }
                }
            }
        }
        project.updateStructure();

        return project;
    }

    public ProjectFile setCalendariosBase (ProjectFile project, JSONArray calendariosJson) throws JSONException {
        System.out.println("\n\t SET CALENDARIOS BASE\n");
        ProjectCalendar calendarParent = null;
        JSONObject json = null;
        ProjectCalendar calendar = null;
        for (int i=0; i<calendariosJson.length(); i++){
            json = calendariosJson.getJSONObject(i);
            calendar = project.getCalendarByName(json.getString("nombre"));
            if (calendar != null){
                calendarParent = project.getCalendarByName(json.getString("calenderBase"));
                calendar.setParent(calendarParent);
                if (calendar.getParent() != null){
                    System.out.println("get calendarParent: " + calendar.getParent().getName());
                }else{
                    System.out.println(calendar.getName() + " es un calendario base");
                }
            }
        }
        
        ProjectCalendar calendar1 = project.getCalendarByName("Estándar");
        ProjectCalendar calendar2 = project.getCalendarByName("Standard");
        if (calendar1 != null && calendar2 != null){
            int idCalendar;
            if (project.getDefaultCalendar().getName().equalsIgnoreCase(calendar1.getName())){
                idCalendar = calendar2.getUniqueID();
                System.out.println(calendar2.getName() + ", " + calendar2.getUniqueID());
                calendar2 = calendar1;
                calendar2.setUniqueID(idCalendar);
                System.out.println(calendar2.getName() + ", " + calendar2.getUniqueID());
            }else if (project.getDefaultCalendar().getName().equalsIgnoreCase(calendar2.getName())){
                idCalendar = calendar1.getUniqueID();
                System.out.println(calendar1.getName() + ", " + calendar1.getUniqueID());
                calendar1 = calendar2;
                calendar1.setUniqueID(idCalendar);
                System.out.println(calendar1.getName() + ", " + calendar1.getUniqueID());
            }
                   
        }
        project.updateStructure();
        return project;
    }

    public ProjectFile setCalendarios (ProjectFile project, JSONArray calendariosJson) throws Exception {
        System.out.println("\n\t SET CALENDARIOS\n");
        //calendarContainer = project.getCalendars();
        JSONObject jsonParent = null;
        JSONObject json = null;
        ProjectCalendar calendar = null;
        for (int i=0; i<calendariosJson.length(); i++){
            json = calendariosJson.getJSONObject(i);
            calendar = project.getCalendarByName(json.getString("nombre"));

            if (json.getString("nombre").equalsIgnoreCase(json.getString("calenderBase"))){
                calendar = setCalendario(calendar, json, jsonParent);
            }else{
                jsonParent = getDefaultCalendario(calendariosJson, calendar.getParent().getName());
                System.out.println("jsonParent: " + jsonParent.getString("nombre"));
                calendar = setCalendario(calendar, json, jsonParent);
            }
        }
        return project;
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

    public ProjectCalendar setCalendario(ProjectCalendar calendar,JSONObject json, JSONObject jsonParent) throws Exception{
        try {
            // set working Days
            calendar = setDayType (json.getJSONArray("diaslab"), calendar, DayType.WORKING);
            // set non working Days
            calendar = setDayType (json.getJSONArray("diasnolab"), calendar, DayType.NON_WORKING);
            // set default Days
            calendar = setDayType (json.getJSONArray("calenderDefault"), calendar, DayType.DEFAULT);
            // setHours
            JSONArray horariosCalendario = json.getJSONArray("calenderHorario");
            if (horariosCalendario.length() == 0){
                horariosCalendario = jsonParent.getJSONArray("calenderHorario");
                calendar = setHorario (horariosCalendario, calendar);
            }else{
                calendar = setHorario (horariosCalendario, calendar);
            }
            
            // set exceptions
            JSONArray excepcionesCalendario = json.getJSONArray("calenderExcepciones");
            calendar = setExcepciones (excepcionesCalendario, calendar);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return calendar;
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




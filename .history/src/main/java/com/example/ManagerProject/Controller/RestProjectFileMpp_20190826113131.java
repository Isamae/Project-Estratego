

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
//import net.sf.mpxj.writer.*;  
import net.sf.mpxj.Task;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.reader.UniversalProjectReader;


import java.util.Locale;


/**
 * RestProject
 */
@RestController
@RequestMapping(value = "/project")
public class RestProjectFileMpp {
    
    @GetMapping(value = "/datoJson")
    public String postJson(@RequestBody String payload) throws Exception{
     
		File file = ResourceUtils.getFile("classpath:"+"Proyecto1.xml");
        
        //ProjectReader reader = new MPPReader();  
        UniversalProjectReader reader = new UniversalProjectReader();
        ProjectFile projectObj = reader.read(file);
    
        JSONObject jsonObject = null;
        try {
            jsonObject= new JSONObject(payload);
        } catch (Exception e) {
            System.out.println("Error al convertir Json: ");
                e.printStackTrace();
        }
        
        //projectObj = addCalendario(projectObj,jsonObject);
        //projectObj = addColumnas(projectObj,jsonObject);
        projectObj =  addRecursos(projectObj,jsonObject);
        //projectObj = addDuracionProyecto(projectObj,jsonObject);
        projectObj = addTarea(projectObj,jsonObject);
        ///projectObj = addDuracionTareas(projectObj,jsonObject);
        projectObj = addPredecesoras(projectObj,jsonObject);
        //projectObj = addFechasTareas(projectObj,jsonObject);
        
        //projectObj = addHijosTarea(projectObj,jsonObject);
        
                                
        /*ProjectWriter writer = ProjectWriterUtility.getProjectWriter("HOLA.mpx");  
        writer.write(projectObj,"HOLA.mpx");*/
        MSPDIWriter writer = new MSPDIWriter();
        writer.write(projectObj, "hola.xml");
        return "Hola Mundo";
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
                System.out.println("ID :" +json.getInt("id") +"-> " + project.getTaskByID(json.getInt("id")).getDate(2));

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
        SimpleDateFormat df = new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy");
        JSONObject jsonObject2 = ((JSONObject)(jsonObject.get("allColum")));
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

                /*JSONObject jsonObject3 = jsonObject2.getJSONObject(json.getString("id"));
                String[] predecesoras = jsonObject3.getString("Predecessors").split("Relation ");
                Task task  = project.getTaskByID(json.getInt("id"));
                
                

                for(int j = 0 ; j < predecesoras.length; j++){
                    if(predecesoras[j].length() < 8){}
                    else{
                        predecesoras[j] = predecesoras[j].replace("[", "");
                        predecesoras[j] = predecesoras[j].replace("]", "");
                        String[] datos = predecesoras[j].split("->");

                        String[] temp = datos[0].split(" ");
                        String[] temp1 = datos[1].split(" ");
                        String lag = temp[1];
                        String tipo  = temp[3];
                        String idH = temp1[2].replace("id=", "");


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
                            //task.generateWBS(project.getTaskByID(Integer.parseInt(idH)));
                            ///(project.getTaskByID(json.getInt("id"))).setResourceNames(json.getString("name"));
                            
                        
                        }
                        else if(lag.contains("h")){
                            task.addPredecessor( project.getTaskByID(Integer.parseInt(idH)), relationType,  Duration.getInstance(Double.parseDouble(lag.replace("h", "")),TimeUnit.HOURS));
                            //task.generateWBS(project.getTaskByID(Integer.parseInt(idH)));
                        }
                        else if(lag.contains("y")){
                            task.addPredecessor( project.getTaskByID(Integer.parseInt(idH)), relationType,  Duration.getInstance(Double.parseDouble(lag.replace("y", "")),TimeUnit.YEARS));
                            //task.generateWBS(project.getTaskByID(Integer.parseInt(idH)));
                        }
                        else if(lag.contains("w")){
                            task.addPredecessor( project.getTaskByID(Integer.parseInt(idH)), relationType,  Duration.getInstance(Double.parseDouble(lag.replace("w", "")),TimeUnit.WEEKS));
                            //task.generateWBS(project.getTaskByID(Integer.parseInt(idH)));
                        }
                        else if(lag.contains("m")){
                            task.addPredecessor( project.getTaskByID(Integer.parseInt(idH)), relationType,  Duration.getInstance(Double.parseDouble(lag.replace("m", "")),TimeUnit.MINUTES));
                            //task.generateWBS(project.getTaskByID(Integer.parseInt(idH)));
                        }
                        else if(lag.contains("M")){
                            task.addPredecessor( project.getTaskByID(Integer.parseInt(idH)), relationType,  Duration.getInstance(Double.parseDouble(lag.replace("M", "")),TimeUnit.MONTHS));
                            //task.generateWBS(project.getTaskByID(Integer.parseInt(idH)));
                        }
                        else{
                            task.addPredecessor( project.getTaskByID(Integer.parseInt(idH)), relationType,  Duration.getInstance(Double.parseDouble(lag.replace("p", "")),TimeUnit.PERCENT));
                            //task.generateWBS(project.getTaskByID(Integer.parseInt(idH)));
                        }
                    }
                    
                }*/


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
                System.out.println(resource);
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
        JSONArray calendariosJson = ((JSONArray)(jsonObject.get("calendarios")));

        /*JSONArray recursosJson = ((JSONArray)(jsonObject.get("recursos")));
        for (int i=0; i<recursosJson.length(); i++){
            net.sf.mpxj.Resource resource = project.addResource();
            JSONObject j = recursosJson.getJSONObject(i);
            resource.setName(j.getString("name"));
            resource.setID(j.getInt("id"));
            
        }*/

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




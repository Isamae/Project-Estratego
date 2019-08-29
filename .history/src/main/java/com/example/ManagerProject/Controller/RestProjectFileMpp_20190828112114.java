package com.example.ManagerProject.Controller;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

//import org.apache.poi.sl.usermodel.TextRun.FieldType;
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
import net.sf.mpxj.common.FieldTypeHelper;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.reader.UniversalProjectReader;

import java.util.Iterator;
import java.util.List;
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

        //projectObj = addColumnas(projectObj,jsonObject);
        projectObj = addCamposPersonalizados(projectObj,jsonObject);
        projectObj = addDuracionProyecto(projectObj,jsonObject);
        projectObj =  addRecursos(projectObj,jsonObject);
        projectObj = addTarea(projectObj,jsonObject);
        projectObj = addCalendario(projectObj,jsonObject);
        projectObj = addPredecesoras(projectObj,jsonObject);
        projectObj = addSucesores(projectObj,jsonObject);
        projectObj = addFechasTareas(projectObj,jsonObject);
        projectObj = addDuracionTareas(projectObj,jsonObject);
        
        
        /*ProjectWriter writer = ProjectWriterUtility.getProjectWriter("HOLA.mpx");  
        writer.write(projectObj,"HOLA.mpx");*/

        MSPDIWriter writer = new MSPDIWriter();
        writer.write(projectObj, "hola.xml");
        return "Hola Mundo";
    }
    
    public static ProjectFile addCamposPersonalizados(ProjectFile project,JSONObject jsonObject) throws JSONException{
        
        for(int i=0 ;i< ((JSONArray)(jsonObject.get("CamposPersonalizados"))).length();i++){
            try {
                JSONObject object  = ((JSONArray)(jsonObject.get("CamposPersonalizados"))).getJSONObject(i);
                if(object.getString("Alias Campo").compareToIgnoreCase("null")==1 || object.getInt("FieldTypeID")==-1){}
                else{
                    CustomFieldContainer fields = project.getCustomFields();
                    FieldType fieldType = FieldTypeHelper.getInstance14(object.getInt("FieldTypeID")); 
                    if(fieldType.getName() == null){}
                    else{
                        //CustomFieldValueItem item = new CustomFieldValueItem(object.getInt("FieldTypeValue"));
                        CustomField customField = new CustomField( fieldType,  fields);
                        customField.setAlias(object.getString("Alias Campo"));
                        fields.getCustomField(fieldType);
                        //fields.registerValue(item);
                    }
                   
                }
                
            } catch (JSONException e) {
                e.printStackTrace();
            } 
        }
        for (CustomField field : project.getCustomFields())
        {
            System.out.println(field.getFieldType().getName());
        }
        project.updateStructure();
        return project;
    }

    public static ProjectFile addColumnas(ProjectFile project,JSONObject jsonObject) throws Exception
    {
        
        JSONArray json = jsonObject.getJSONArray("ColumnasTabla1");
        if(project.getTables().size() !=0){
            List<Column> iter = project.getTables().get(0).getColumns();
            for(int i = iter.size()-1; i>0 ; i--){
                project.getTables().get(0).getColumns().remove(i);
            }   
        }
        else{
            Table table = new Table();
            table.setName("Tareas");
            table.setID(1);
            project.getTables().add(table);
        }
        for(int i = 0; i < json.length(); i ++ ){
            Column column = new Column(project);
            FieldType fieldType = FieldTypeHelper.getInstance14(json.getJSONObject(i).getInt("FieldTypeID"));
            column.setFieldType(fieldType);
            column.setTitle(json.getJSONObject(i).getString("ColumTitulo"));
            project.getTables().get(0).getColumns().add(column);
        }
  
        return project;
        
    }
    

    public static ProjectFile addDuracionTareas(ProjectFile project,JSONObject jsonObject) throws JSONException{
        for(int i=0 ;i< ((JSONArray)(jsonObject.get("tareas"))).length();i++){
            try {
                JSONObject json = ((JSONArray)(jsonObject.get("tareas"))).getJSONObject(i);
                    String duracion = json.getString("duracion");
                    Duration duracionD = null;
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

                    //System.out.println("ID :" +json.getInt("id") +"-> " + duracionD);
                    project.getTaskByID(json.getInt("id")).setDuration(duracionD);
                    project.getTaskByID(json.getInt("id")).setActualDuration(duracionA);
                    //project.getTaskByID(json.getInt("id")).setActualWork(actualT);
                    //project.getTaskByID(json.getInt("id")).setDurationText(duracion);
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
                
                if(json.getJSONArray("hijos").length() == 0){
                    project.getTaskByID(json.getInt("id")).getStart();
                    project.getTaskByID(json.getInt("id")).getFinish();


                    if(json.getString("AfechaInicio") != "null"){
                        project.getTaskByID(json.getInt("id")).setActualStart(df.parse(json.getString("fechaInicio")));
                    }
                    /*if(json.getString("fechaInicio") != "null"){
                        project.getTaskByID(json.getInt("id")).setStart(df.parse(json.getString("fechaInicio")));
                    }
                    if(json.getString("TfechaInicio") != "null"){
                        project.getTaskByID(json.getInt("id")).setStartText(json.getString("TfechaInicio"));
                    }*/

                    
                    if(json.getString("AfechaFin") != "null"){
                        project.getTaskByID(json.getInt("id")).setActualFinish(df.parse(json.getString("fechaFin")));
                    }


                    /*if(json.getString("fechaFin") != "null"){
                        project.getTaskByID(json.getInt("id")).setFinish(df.parse(json.getString("fechaFin")));
                    }
                    if(json.getString("TfechaFin") != "null"){
                        project.getTaskByID(json.getInt("id")).setFinishText(json.getString("TfechaFin"));
                    }*/
                }
                else{
                    project.getTaskByID(json.getInt("id")).getStart();
                    project.getTaskByID(json.getInt("id")).getFinish();
                    if(json.getString("AfechaInicio") != "null"){
                        project.getTaskByID(json.getInt("id")).setActualStart(df.parse(json.getString("fechaInicio")));
                    }
                }
                
                
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
                            /*if(project.getTaskByID(json.getInt("id"))!=null){
                                taskhijo.generateWBS(project.getTaskByID(json.getInt("id")));
                            }
                            else{}*/
                            
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
                            /*if(project.getTaskByID(json.getInt("id"))!=null){
                                taskhijo.generateWBS(project.getTaskByID(json.getInt("id")));
                            }
                            else{}*/
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

                    if(json.getString("id").compareToIgnoreCase(idH) ==1) {}
                    else{
                        if(lag.contains("d")){
                        
                            Task targetTask = project.getTaskByID(Integer.parseInt(idH));
                            Relation arg0 = new Relation(task, targetTask, relationType, Duration.getInstance(Double.parseDouble(lag.replace("d", "")),TimeUnit.DAYS));
                            task.getSuccessors().add(arg0);
                        }
                        else if(lag.contains("h")){
                            Task targetTask = project.getTaskByID(Integer.parseInt(idH));
                            Relation arg0 = new Relation(task, targetTask, relationType, Duration.getInstance(Double.parseDouble(lag.replace("h", "")),TimeUnit.HOURS));
                            task.getSuccessors().add(arg0);
                        }
                        else if(lag.contains("y")){
                            Task targetTask = project.getTaskByID(Integer.parseInt(idH));
                            Relation arg0 = new Relation(task, targetTask, relationType, Duration.getInstance(Double.parseDouble(lag.replace("y", "")),TimeUnit.YEARS));
                            task.getSuccessors().add(arg0);
                        }
                        else if(lag.contains("w")){
                            Task targetTask = project.getTaskByID(Integer.parseInt(idH));
                            Relation arg0 = new Relation(task, targetTask, relationType, Duration.getInstance(Double.parseDouble(lag.replace("w", "")),TimeUnit.WEEKS));
                            task.getSuccessors().add(arg0);
                        }
                        else if(lag.contains("m")){
                            Task targetTask = project.getTaskByID(Integer.parseInt(idH));
                            Relation arg0 = new Relation(task, targetTask, relationType,  Duration.getInstance(Double.parseDouble(lag.replace("m", "")),TimeUnit.MINUTES));
                            task.getSuccessors().add(arg0);
                        }
                        else if(lag.contains("M")){
                            Task targetTask = project.getTaskByID(Integer.parseInt(idH));
                            Relation arg0 = new Relation(task, targetTask, relationType, Duration.getInstance(Double.parseDouble(lag.replace("M", "")),TimeUnit.MONTHS));
                            task.getSuccessors().add(arg0);
                        }
                        else{
                            Task targetTask = project.getTaskByID(Integer.parseInt(idH));
                            Relation arg0 = new Relation(task, targetTask, relationType, Duration.getInstance(Double.parseDouble(lag.replace("p", "")),TimeUnit.PERCENT));
                            task.getSuccessors().add(arg0);
                        }
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
        project.getTaskByID(0).setStart(df.parse(jsonObject.getString("StartDate")));
       
        System.out.println( project.getProjectProperties().getDefaultOvertimeRate());
        project.getProjectProperties().getDefaultStartTime().setHours(6);
        project.getProjectProperties().getDefaultEndTime().setHours(19);
        System.out.println( project.getProjectProperties().getDefaultStartTime());
        System.out.println( project.getProjectProperties().getDefaultEndTime());

        if(jsonObject.getString("StartDate")!="null"){
            
            /*project.getStartDate().setTime(df.parse(jsonObject.getString("StartDate")).getTime());
            project.getStartDate().setSeconds(df.parse(jsonObject.getString("StartDate")).getSeconds());
            project.getStartDate().setMinutes(df.parse(jsonObject.getString("StartDate")).getMinutes());
            project.getStartDate().setHours(df.parse(jsonObject.getString("StartDate")).getHours());
            project.getStartDate().setMonth(df.parse(jsonObject.getString("StartDate")).getMonth());
            project.getStartDate().setYear(Integer.parseInt(jsonObject.getString("StartDate").split(" ")[5]));*/            

        }
        if(jsonObject.getString("FinishDate")!="null"){
            /*project.getFinishDate().setTime(df.parse(jsonObject.getString("FinishDate")).getTime());
            project.getFinishDate().setSeconds(df.parse(jsonObject.getString("FinishDate")).getSeconds());
            project.getFinishDate().setMinutes(df.parse(jsonObject.getString("FinishDate")).getMinutes());
            project.getFinishDate().setHours(df.parse(jsonObject.getString("FinishDate")).getHours());
            project.getFinishDate().setMonth(df.parse(jsonObject.getString("FinishDate")).getMonth());
            project.getFinishDate().setYear(df.parse(jsonObject.getString("FinishDate")).getYear());*/
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
        project.updateStructure();
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

        JSONArray calendariosJson = jsonObject.getJSONArray("calendarios");
        // set default calendar
        project = setDefaultCalendario (project, calendariosJson);

        // add calendars
        project = addCalendarios(project, calendariosJson);

        // link calendars to resources
        project = linkCalendarioRecurso (project, calendariosJson);
        
        // set base calendars
        project = setCalendariosBase (project, calendariosJson);

        // set calendarios
        project = setCalendarios (project, calendariosJson);
        return project;
    }

    public ProjectFile setDefaultCalendario (ProjectFile project, JSONArray calendariosJson) throws Exception {
        //System.out.println("\n\t SET CALENDARIO DEFAULT\n"); 
        ProjectCalendar defaultCalendario = project.getDefaultCalendar();
        //System.out.println("DEFAULT CALENDAR NAME --> " + defaultCalendario.getName());
        JSONObject defCalendarioJson = getDefaultCalendario(calendariosJson, defaultCalendario.getName());
        JSONObject parentJson = null;
        setCalendario(defaultCalendario, defCalendarioJson, parentJson);
        project.setDefaultCalendar(defaultCalendario);
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

    public ProjectFile addCalendarios(ProjectFile project, JSONArray calendariosJson) throws JSONException {
        //System.out.println("\n\t ADD CALENDARIOS"); 
        //ProjectCalendarContainer projectCalendars = project.getCalendars();
        ProjectCalendar defaultCalendario = project.getDefaultCalendar();
        for(int i=0; i< calendariosJson.length(); i++){
            JSONObject json = calendariosJson.getJSONObject(i);
            if (!json.getString("nombre").equalsIgnoreCase(defaultCalendario.getName())){
                //System.out.println("\n\t add new calendar");
                //System.out.println("\n" + json.getString("nombre"));
                ProjectCalendar calendar = project.addCalendar();
                //addCalendario(calendar, calJson);
                try {
                    // set calenderName
                    calendar.setName(json.getString("nombre"));
                    //System.out.println("setName " + calendar.getName());
                    //set calender id
                    calendar.setUniqueID(json.getInt("calenderID"));
                    //System.out.println("setUniqueID " + calendar.getUniqueID());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return project;
    }

    public ProjectFile linkCalendarioRecurso (ProjectFile project, JSONArray calendariosJson) throws JSONException {
        //System.out.println("\n\t ADD RESOURCES"); 
        ProjectCalendarContainer calendarContainer = project.getCalendars();
        String id = "";
        for (int i=0; i<calendarContainer.size(); i++){
            ProjectCalendar calendar = calendarContainer.get(i);
            for (int j=0; j<calendariosJson.length(); j++){
                JSONObject json = calendariosJson.getJSONObject(j);
                if (calendar.getName().equalsIgnoreCase(json.getString("nombre"))){
                    id = json.getString("recursoID");
                }
            }
            Resource resource = null;
            if (!id.equalsIgnoreCase("null")){
                resource = project.getResourceByID(Integer.parseInt(id));
                if (resource != null){
                    calendar.setResource(resource);
                    //System.out.println("set resource: " + calendar.getResource().getName());
                }else{
                    ///System.out.println("resource null");
                }
            }
        }
        return project;
    }

    public ProjectFile setCalendariosBase (ProjectFile project, JSONArray calendariosJson) throws JSONException {
        //System.out.println("\n\t SET CALENDARIOS BASE\n");
        ProjectCalendarContainer calendarContainer = project.getCalendars();
        ProjectCalendar calendarParent = null;
        for (int i=0; i<calendarContainer.size(); i++){
            ProjectCalendar calendar = calendarContainer.get(i);
            //System.out.println("calendar name: " + calendar.getName());
            for (int j=0; j<calendariosJson.length(); j++){
                JSONObject json = calendariosJson.getJSONObject(j);
                if (calendar.getName().equalsIgnoreCase(json.getString("nombre"))){
                    calendarParent = calendarContainer.getByName(json.getString("calenderBase"));
                    calendar.setParent(calendarParent);
                    
                    if (calendar.getParent() != null){
                        System.out.println(calendar.getParent().getName());
                    }else{
                        //System.out.println("calendar.getParent().getName() = null");
                    }
                }
            }
            calendarContainer.set(i, calendar);
            //System.out.println("calendar name: " + calendarContainer.get(i).getName());
        }
        return project;
    }

    public ProjectFile setCalendarios (ProjectFile project, JSONArray calendariosJson) throws Exception {
        //System.out.println("\n\t SET CALENDARIOS\n");
        ProjectCalendarContainer calendarContainer = project.getCalendars();
        JSONObject jsonParent = null;
        for (int i=0; i<calendarContainer.size(); i++){
            //System.out.println("\n\t set calendar");
            ProjectCalendar calendar = calendarContainer.get(i);
            for (int j=0; j<calendariosJson.length(); j++){
                JSONObject json = calendariosJson.getJSONObject(j);
                if (calendar.getName().equalsIgnoreCase(json.getString("nombre"))){
                    if (json.getString("nombre").equalsIgnoreCase(json.getString("calenderBase"))){
                        setCalendario(calendar, json, jsonParent);
                    }else{
                        jsonParent = getDefaultCalendario(calendariosJson, calendar.getParent().getName());
                        ///System.out.println("jsonParent: " + jsonParent.getString("nombre"));
                        setCalendario(calendar, json, jsonParent);
                    }
                }
            }
            calendarContainer.set(i, calendar);
            //System.out.println("calendar name: " + calendarContainer.get(i).getName());
        }
        return project;
    }

    public void setCalendario(ProjectCalendar calendar,JSONObject json, JSONObject jsonParent) throws Exception{
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
                //System.out.println(jsonParent.getString("nombre"));
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
        //System.out.println("=========== setDayType ===========");
        for (int i=0; i<dias.length(); i++){
            Day day = Day.valueOf(dias.get(i).toString());
            calendario.setWorkingDay(day, dT);         
        }
        //System.out.println(dT);
        return calendario;      
    }

    public ProjectCalendar setHorario (JSONArray horarioCalendario, ProjectCalendar calendario) throws JSONException, ParseException {
        //System.out.println("=========== setHorario ===========");
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
                //System.out.println(hora.getDay().toString() + "\n" + hora.getParentCalendar());
            }
        }
        /* ** */

        return calendario;
    }

    public ProjectCalendar setExcepciones (JSONArray excepcionesCalendario, ProjectCalendar calendario) throws JSONException, ParseException {
        //System.out.println("=========== setExcepciones ===========");
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




package com.example.ManagerProject.Controller;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
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
import net.sf.mpxj.mspdi.MSPDIWriter;
//import net.sf.mpxj.writer.*;  
import net.sf.mpxj.Task;
import net.sf.mpxj.common.FieldTypeHelper;
import net.sf.mpxj.reader.UniversalProjectReader;

import java.util.Date;
import java.util.List;


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
        
        projectObj = setPropiedades(projectObj, jsonObject);        
        projectObj = addCamposPersonalizados(projectObj,jsonObject);
        projectObj = addDuracionProyecto(projectObj,jsonObject);
        projectObj = addRecursos(projectObj,jsonObject);

        projectObj = addTarea(projectObj,jsonObject);
        //projectObj = addCalendarioTarea(projectObj,jsonObject);
        projectObj = addCalendario(projectObj,jsonObject);
        projectObj = addPredecesoras(projectObj,jsonObject);
        projectObj = addSucesores(projectObj,jsonObject);
        projectObj = addFechasTareas(projectObj,jsonObject);
        projectObj = addCalendarioTarea(projectObj,jsonObject);
        projectObj = addValoresCamposPersonalizados(projectObj,jsonObject);
        projectObj = addAsignacionesRecursos(projectObj,jsonObject);
        

        String nombFile = "archivo";
        // if (projectObj.getProjectProperties().getName() != null){
        //     nombFile = projectObj.getProjectProperties().getName();
        // }else if (projectObj.getProjectProperties().getProjectTitle() != null) {
        //     nombFile = projectObj.getProjectProperties().getProjectTitle();
        // }
        nombFile = nombFile + ".xml";
        System.out.println(nombFile);

        MSPDIWriter writer = new MSPDIWriter();
        writer.write(projectObj, nombFile);
        
        return "Hola Mundo";
    }

    public ProjectFile setPropiedades (ProjectFile project, JSONObject json) throws JSONException, ParseException {
        // set propiedades iniciales del proyecto

        //set start date
        Date startDate = new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(json.getString("PStartDate"));
        project.getProjectProperties().setStartDate(startDate);
        //set finish date
        Date finishDate = new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(json.getString("PFinishDate"));
        project.getProjectProperties().setFinishDate(finishDate);
        // set project name y project tittle
        if (json.getString("projectName").equalsIgnoreCase("null") && !json.getString("projectTittle").equalsIgnoreCase("null")){
            project.getProjectProperties().setName(json.getString("projectTittle"));
            project.getProjectProperties().setProjectTitle(json.getString("projectTittle"));
        }
        //set company
        project.getProjectProperties().setCompany(json.getString("projectCompany"));
        // set author
        project.getProjectProperties().setAuthor(json.getString("projectAuthor"));

        return project;
    }
    
    public static ProjectFile addCamposPersonalizados(ProjectFile project,JSONObject jsonObject) throws JSONException{

        for(int i=0 ;i< ((JSONArray)(jsonObject.get("CamposPersonalizados"))).length();i++){
            try {
                JSONObject object  = ((JSONArray)(jsonObject.get("CamposPersonalizados"))).getJSONObject(i);
                if(object.getString("AliasCampo").compareToIgnoreCase("null")==0 || object.getInt("FieldTypeID")==-1){

                }
                else{
                    
                    FieldType fieldType = FieldTypeHelper.getInstance14(object.getInt("FieldTypeID")); 
                    //CustomFieldContainer fields = project.getCustomFields();
                    if(fieldType.getName() == null){}
                    else{
                        //fields.getCustomField(fieldType);
                        project.getCustomFields().getCustomField(fieldType).setAlias(object.getString("AliasCampo"));
                    }
                   
                }
                
            } catch (JSONException e) {
                e.printStackTrace();
            } 
        }
        return project;
    }

    public static ProjectFile addColumnas(ProjectFile project,JSONObject jsonObject) throws Exception{
        
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
        return project;
    }

    public static ProjectFile addFechasTareas(ProjectFile project,JSONObject jsonObject) throws JSONException{
       
        SimpleDateFormat df = new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy");
        for(int i=0;i< ((JSONArray)(jsonObject.get("tareas"))).length();i++){
            try {
                
                JSONObject json = ((JSONArray)(jsonObject.get("tareas"))).getJSONObject(i);
                
                /*if(json.getJSONArray("hijos").length() == 0){*/
                    Task task =  project.getTaskByID(json.getInt("id"));
                    task.getStart();
                    task.getFinish();
                    task.getStartText();
                    task.getFinishText();
                    task.getActualStart();
                    task.getActualFinish();

                    if(json.getString("AfechaInicio").compareToIgnoreCase("null")!=0 ){
                        task.setActualStart(df.parse(json.getString("AfechaInicio")));
                    }
                    if(json.getString("fechaInicio").compareToIgnoreCase("null")!=0){
                        task.setStart(df.parse(json.getString("fechaInicio")));
                    }
                    if(json.getString("TfechaInicio").compareToIgnoreCase("null")!=0){
                        task.setStartText(json.getString("TfechaInicio"));
                    }
                    if(json.getString("AfechaFin").compareToIgnoreCase("null")!=0){
                        task.setActualFinish(df.parse(json.getString("AfechaFin")));
                    }
                    if(json.getString("fechaFin").compareToIgnoreCase("null")!=0){
                        task.setFinish(df.parse(json.getString("fechaFin")));
                    }
                    if(json.getString("TfechaFin").compareToIgnoreCase("null")!=0){
                        task.setFinishText(json.getString("TfechaFin"));
                    }
                /*}
                else{
                    project.getTaskByID(json.getInt("id")).getStart();
                    project.getTaskByID(json.getInt("id")).getFinish();
                    if(json.getString("AfechaInicio") != "null"){
                        project.getTaskByID(json.getInt("id")).setActualStart(df.parse(json.getString("fechaInicio")));
                    }
                }*/
                
                
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e){
                e.printStackTrace();
            }
        }
        project.updateStructure();
        return project;
    }

    public static ProjectFile addValoresCamposPersonalizados(ProjectFile project,JSONObject jsonObject) throws JSONException, ParseException {

        for(int i=0 ;i< ((JSONArray)(jsonObject.get("tareas"))).length();i++){
            try {
                JSONObject json = ((JSONArray)(jsonObject.get("tareas"))).getJSONObject(i);
                JSONArray columnas = json.getJSONArray("Columnas");
                for(int x = 0;  x < columnas.length(); x++){
                    JSONObject datoObject  = columnas.getJSONObject(x);
                    if(datoObject.getInt("FieldTypeID") == -1){}
                    else{
                        Task tarea = project.getTaskByID(json.getInt("id"));
                        String tipodato = datoObject.getString("FieldTypeDataTypeString");
                        Object seteadoValue = null;
                        
                        
                        if(datoObject.getString("ValorCampo").compareToIgnoreCase("null") ==0){
                            seteadoValue = null;
                        }
                        else{
                            if(tipodato.compareToIgnoreCase("STRING")==0 || tipodato.compareToIgnoreCase("ASCII_STRING")==0){
                                seteadoValue = datoObject.getString("ValorCampo");
                            }
                            else if(tipodato.compareToIgnoreCase("PERCENTAGE")==0 || tipodato.compareToIgnoreCase("SHORT")==0
                            || tipodato.compareToIgnoreCase("SHORT")==0 || tipodato.compareToIgnoreCase("NUMERIC")==0
                            || tipodato.compareToIgnoreCase("CURRENCY")==0 ){
                                seteadoValue =  Double.parseDouble(datoObject.getString("ValorCampo")) ;
                            }
                            else if(tipodato.compareToIgnoreCase("DURATION")==0 || tipodato.compareToIgnoreCase("WORK")==0){
                                Duration duracionD = null;
                                if(datoObject.getString("ValorCampo").contains("h")){
                                    duracionD = Duration.getInstance(Double.parseDouble(datoObject.getString("ValorCampo").replace("h", "")), TimeUnit.HOURS);
                                }

                                else if(datoObject.getString("ValorCampo").contains("m")){
                                    duracionD = Duration.getInstance(Double.parseDouble(datoObject.getString("ValorCampo").replace("m", "")), TimeUnit.MINUTES);
                                }

                                else if(datoObject.getString("ValorCampo").contains("d")){
                                    duracionD = Duration.getInstance(Double.parseDouble(datoObject.getString("ValorCampo").replace("d", "")), TimeUnit.DAYS);
                                }
                                else if(datoObject.getString("ValorCampo").contains("w")){
                                    duracionD = Duration.getInstance(Double.parseDouble(datoObject.getString("ValorCampo").replace("w", "")), TimeUnit.WEEKS);
                                }

                                else if(datoObject.getString("ValorCampo").contains("M")){
                                    duracionD = Duration.getInstance(Double.parseDouble(datoObject.getString("ValorCampo").replace("M", "")), TimeUnit.MONTHS);
                                }
                                else if(datoObject.getString("ValorCampo").contains("y")){
                                    duracionD = Duration.getInstance(Double.parseDouble(datoObject.getString("ValorCampo").replace("y", "")), TimeUnit.YEARS);
                                }
                                else{
                                    duracionD = Duration.getInstance(Double.parseDouble(datoObject.getString("ValorCampo").replace("p", "")), TimeUnit.PERCENT);
                                }
                                seteadoValue = duracionD;

                            }
                            else if(tipodato.compareToIgnoreCase("CONSTRAINT")==0){
                                seteadoValue = DataType.CONSTRAINT.valueOf(datoObject.getString("ValorCampo"));
                            }
                            else if(tipodato.compareToIgnoreCase("BOOLEAN")==0){
                                seteadoValue = Boolean.parseBoolean(datoObject.getString("ValorCampo"));
                            }
                            else if(tipodato.compareToIgnoreCase("TASK_TYPE")==0 || tipodato.compareToIgnoreCase("RELATION_LIST")==0){
                            }
                            else if(tipodato.compareToIgnoreCase("DATE")==0){
                                SimpleDateFormat df = new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy");
                                seteadoValue = df.parse(json.getString("ValorCampo"));
                            }
                            else if(tipodato.compareToIgnoreCase("EARNED_VALUE_METHOD")==0){
                                seteadoValue = DataType.EARNED_VALUE_METHOD.valueOf(json.getString("ValorCampo"));
                            }
                            else if(tipodato.compareToIgnoreCase("INTEGER")==0){
                                seteadoValue = Integer.parseInt(json.getString("ValorCampo"));
                            }
                            else if(tipodato.compareToIgnoreCase("GUID")==0){
                                seteadoValue = DataType.GUID.valueOf(json.getString("ValorCampo"));
                            }
                            else if(tipodato.compareToIgnoreCase("ACCRUE")==0){
                                seteadoValue = DataType.ACCRUE.valueOf(json.getString("ValorCampo"));
                            }
                            else{
                                seteadoValue = null;
                            }
                        }       
                        FieldType fieldType = FieldTypeHelper.getInstance14(datoObject.getInt("FieldTypeID"));
                        tarea.getCurrentValue(fieldType);
                        tarea.set(fieldType,seteadoValue);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClassCastException e) {
                e.printStackTrace();
            } 
        }
        project.updateStructure();
        return project;
    }

    public static ProjectFile addTarea(ProjectFile project,JSONObject jsonObject) throws JSONException, ParseException {
        Task tarea = project.getTasks().get(0);
        tarea.setName(jsonObject.getJSONArray("tareas").getJSONObject(0).getString("name"));
        tarea.setUniqueID(jsonObject.getJSONArray("tareas").getJSONObject(0).getInt("uniqueID"));
        tarea.setActive(jsonObject.getJSONArray("tareas").getJSONObject(0).getBoolean("estado"));
        tarea.setOutlineNumber(jsonObject.getJSONArray("tareas").getJSONObject(0).getString("OutlineNumber"));
        tarea.setOutlineLevel(jsonObject.getJSONArray("tareas").getJSONObject(0).getInt("OutlineLevel"));
        SimpleDateFormat df = new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy");
        
       
        for(int i=1 ;i< ((JSONArray)(jsonObject.get("tareas"))).length();i++){
            try {
                JSONObject json = ((JSONArray)(jsonObject.get("tareas"))).getJSONObject(i);
                if( project.getTaskByID(json.getInt("id")) ==  null){

                    (project.addTask()).setID(json.getInt("id"));
                    (project.addTask()).disableEvents();
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
                            taskhijo.generateOutlineNumber(project.getTaskByID(json.getInt("id")));
                        }
                        project.getTaskByID(json.getInt("id")).addChildTask(taskhijo);

                    }
                }
                project.getTaskByID(json.getInt("id")).setEstimated(json.getBoolean("estimada"));
                project.getTaskByID(json.getInt("id")).setActive(json.getBoolean("estado"));
                project.getTaskByID(json.getInt("id")).setPercentageComplete(json.getDouble("porcentajeCompletado"));
                project.getTaskByID(json.getInt("id")).setNotes(json.getString("notas"));
                project.getTaskByID(json.getInt("id")).setHideBar(json.getBoolean("ocultarBarra"));
                project.getTaskByID(json.getInt("id")).setMilestone(json.getBoolean("hito"));
                project.getTaskByID(json.getInt("id")).setPriority(Priority.getInstance(json.getInt("priority")));
                project.getTaskByID(json.getInt("id")).set(FieldTypeHelper.getInstance(188743812), json.getBoolean("condicionadaEsfuerzo"));
                project.getTaskByID(json.getInt("id")).set(FieldTypeHelper.getInstance(188743762), json.getBoolean("resumida"));
                if(json.getString("modoProgramacion").compareToIgnoreCase("AUTO_SCHEDULED")==0){
                    project.getTaskByID(json.getInt("id")).setTaskMode(TaskMode.AUTO_SCHEDULED);
                }
                else{
                    project.getTaskByID(json.getInt("id")).setTaskMode(TaskMode.MANUALLY_SCHEDULED);
                }

                
                project.getTaskByID(json.getInt("id")).set(FieldTypeHelper.getInstance(188743697),ConstraintType.valueOf(json.getString("tipoRestricion")));
                project.getTaskByID(json.getInt("id")).set(FieldTypeHelper.getInstance(188744802), EarnedValueMethod.valueOf(json.getString("metodoValorAcumulado")));
                if(json.getString("propetarioAsignacion").compareToIgnoreCase("null")==0){
                }
                else{
                    project.getTaskByID(json.getInt("id")).set(FieldTypeHelper.getInstance(188744850), json.getString("propetarioAsignacion"));
                }
                
                if(json.getString("fechaRestriccion").compareToIgnoreCase("null")==0){
                }
                else{
                    project.getTaskByID(json.getInt("id")).set(FieldTypeHelper.getInstance(188743698), df.parse(json.getString("fechaRestriccion")));
                }

                if(json.getString("fechaLimite").compareToIgnoreCase("null")==0){
                }
                else{
                    project.getTaskByID(json.getInt("id")).set(FieldTypeHelper.getInstance(188744117), df.parse(json.getString("fechaLimite")));
                }



            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClassCastException e) {
                e.printStackTrace();
            } 
        }
        project.updateStructure();
        return project;
    }

    public static ProjectFile addCalendarioTarea(ProjectFile project,JSONObject jsonObject) throws JSONException, ParseException {
       
        for(int i=0 ;i< ((JSONArray)(jsonObject.get("tareas"))).length();i++){
            try {
                JSONObject json = ((JSONArray)(jsonObject.get("tareas"))).getJSONObject(i);
                if(json.getInt("CalendarioUniqueID") ==-1){}
                else{
                    project.getTaskByID(json.getInt("id")).setCalendarUniqueID(json.getInt("CalendarioUniqueID"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClassCastException e) {
                e.printStackTrace();
            } 
        }
        project.updateStructure();
        return project;
    }

    public static ProjectFile addAsignacionesRecursos(ProjectFile project,JSONObject jsonObject) throws JSONException, ParseException {
        SimpleDateFormat df = new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy");
       
        for(int i=0 ;i< ((JSONArray)(jsonObject.get("asigRecursos"))).length();i++){
            try {
                JSONObject json = ((JSONArray)(jsonObject.get("asigRecursos"))).getJSONObject(i);
                Task task = project.getTaskByID(json.getInt("idTask"));
                Resource resource = project.getResourceByID(json.getInt("idResource"));
                ResourceAssignment assignment =  task.addResourceAssignment(resource);
                assignment.setCost(json.getDouble("Cost"));

                assignment.getActualStart();
                assignment.getActualFinish();
                assignment.getStart();
                assignment.getFinish();
                if(json.getString("ActualStart").compareToIgnoreCase("null")==0){}
                else{
                    
                    assignment.setActualStart(df.parse(json.getString("ActualStart")));
                }
                if(json.getString("ActualFinish").compareToIgnoreCase("null")==0){}
                else{
                    
                    assignment.setActualFinish(df.parse(json.getString("ActualFinish")));
                }


                if(json.getString("Start").compareToIgnoreCase("null")==0){}
                else{
                    
                    assignment.setStart(df.parse(json.getString("Start")));
                }
                if(json.getString("Finish").compareToIgnoreCase("null")==0){}
                else{
                    
                    assignment.setFinish(df.parse(json.getString("Finish")));
                }
            
                
                assignment.setUnits(json.getDouble("Units"));
                assignment.setUniqueID(json.getInt("UniqueID"));

                /*if(json.getString("ActualWork").contains("d")){
                    assignment.setActualWork(Duration.getInstance(Double.parseDouble(json.getString("ActualWork").replace("d", "")),TimeUnit.DAYS));
                    if(json.getString("Delay").compareToIgnoreCase("null")!=0){
                        assignment.setDelay(Duration.getInstance(Double.parseDouble(json.getString("Delay").replace("d", "")),TimeUnit.DAYS));
                    }
                    
                }
                else if(json.getString("ActualWork").contains("h")){
                    assignment.setActualWork(Duration.getInstance(Double.parseDouble(json.getString("ActualWork").replace("h", "")),TimeUnit.HOURS));
                    if(json.getString("Delay").compareToIgnoreCase("null")!=0){
                        assignment.setDelay(Duration.getInstance(Double.parseDouble(json.getString("Delay").replace("h", "")),TimeUnit.HOURS));
                    }
                }
                else if(json.getString("ActualWork").contains("y")){
                    assignment.setActualWork(Duration.getInstance(Double.parseDouble(json.getString("ActualWork").replace("y", "")),TimeUnit.YEARS));
                    if(json.getString("Delay").compareToIgnoreCase("null")!=0){
                        assignment.setDelay(Duration.getInstance(Double.parseDouble(json.getString("Delay").replace("y", "")),TimeUnit.YEARS));
                    }
                }
                else if(json.getString("ActualWork").contains("w")){
                    assignment.setActualWork(Duration.getInstance(Double.parseDouble(json.getString("ActualWork").replace("w", "")),TimeUnit.WEEKS));
                    if(json.getString("Delay").compareToIgnoreCase("null")!=0){
                        assignment.setDelay(Duration.getInstance(Double.parseDouble(json.getString("Delay").replace("w", "")),TimeUnit.WEEKS));
                    }
                }
                else if(json.getString("ActualWork").contains("m")){
                    assignment.setActualWork(Duration.getInstance(Double.parseDouble(json.getString("ActualWork").replace("m", "")),TimeUnit.MINUTES));
                    if(json.getString("Delay").compareToIgnoreCase("null")!=0){
                        assignment.setDelay(Duration.getInstance(Double.parseDouble(json.getString("Delay").replace("m", "")),TimeUnit.MINUTES));
                    }
                }
                else if(json.getString("ActualWork").contains("M")){
                    assignment.setActualWork(Duration.getInstance(Double.parseDouble(json.getString("ActualWork").replace("M", "")),TimeUnit.MONTHS));
                    if(json.getString("Delay").compareToIgnoreCase("null")!=0){
                        assignment.setDelay(Duration.getInstance(Double.parseDouble(json.getString("Delay").replace("M", "")),TimeUnit.MONTHS));
                    }
                }
                else{
                    assignment.setActualWork(Duration.getInstance(Double.parseDouble(json.getString("ActualWork").replace("p", "")),TimeUnit.PERCENT));
                    if(json.getString("Delay").compareToIgnoreCase("null")!=0){
                        assignment.setDelay(Duration.getInstance(Double.parseDouble(json.getString("Delay").replace("p", "")),TimeUnit.PERCENT));
                    }
                }*/
                
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClassCastException e) {
                e.printStackTrace();
            } 
        }
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

                    if(json.getString("id").compareToIgnoreCase(idH) == 0) {}
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
       
        //project.getProjectProperties().getDefaultStartTime().setHours(6);
        //project.getProjectProperties().getDefaultEndTime().setHours(19);

        if(jsonObject.getString("PStartDate").compareToIgnoreCase("null")==0){}
        else{
            project.getProjectProperties().getStartDate();
            project.getProjectProperties().getStartDate().setTime(df.parse(jsonObject.getString("StartDate")).getTime());
            project.getProjectProperties().getStartDate().setSeconds(df.parse(jsonObject.getString("StartDate")).getSeconds());
            project.getProjectProperties().getStartDate().setMinutes(df.parse(jsonObject.getString("StartDate")).getMinutes());
            project.getProjectProperties().getStartDate().setHours(df.parse(jsonObject.getString("StartDate")).getHours());
            project.getProjectProperties().getStartDate().setMonth(df.parse(jsonObject.getString("StartDate")).getMonth());
            project.getProjectProperties().getStartDate().setYear(df.parse(jsonObject.getString("StartDate")).getYear());
        }

        if(jsonObject.getString("PFinishDate").compareToIgnoreCase("null")==0){}
        else{
            project.getProjectProperties().getFinishDate();
            project.getProjectProperties().getFinishDate().setTime(df.parse(jsonObject.getString("FinishDate")).getTime());
            project.getProjectProperties().getFinishDate().setSeconds(df.parse(jsonObject.getString("FinishDate")).getSeconds());
            project.getProjectProperties().getFinishDate().setMinutes(df.parse(jsonObject.getString("FinishDate")).getMinutes());
            project.getProjectProperties().getFinishDate().setHours(df.parse(jsonObject.getString("FinishDate")).getHours());
            project.getProjectProperties().getFinishDate().setMonth(df.parse(jsonObject.getString("FinishDate")).getMonth());
            project.getProjectProperties().getFinishDate().setYear(df.parse(jsonObject.getString("FinishDate")).getYear());
        }
        return project;
    }

    public static ProjectFile addRecursos(ProjectFile project,JSONObject jsonObject){
        ResourceContainer rContainer = project.getResources();
        for (int i=0; i<rContainer.size(); i++){
            rContainer.remove(i);
        }
        try {
            JSONArray array = (JSONArray) jsonObject.get("recursos");
            System.out.println(array.length());
            for(int i=0; i< array.length();i++){
                Resource resource = project.addResource();
                resource.setName(((JSONObject)array.get(i)).getString("name"));
                resource.setID(((JSONObject)array.get(i)).getInt("id"));
                resource.setUniqueID(((JSONObject)array.get(i)).getInt("idUni"));
                resource.disableEvents();

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
        project = setDefaultCalendario (project, jsonObject);

        // add calendars
        project = addCalendarios(project, calendariosJson);

        for (int i=0; i<project.getCalendars().size(); i++){
            System.out.println(project.getCalendars().get(i).getName());
        }

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
        JSONObject calendarioJson = json.getJSONArray("defaultCalendario").getJSONObject(0);
        ProjectCalendar defaultCalendario = project.getDefaultCalendar();
        // set calendar name, set calendar unique id
        defaultCalendario.setName(calendarioJson.getString("nombre"));
        defaultCalendario.setUniqueID(calendarioJson.getInt("calenderID"));
        setCalendario(defaultCalendario, calendarioJson, null);
        project.setDefaultCalendar(defaultCalendario);
        return project;
    }

    public ProjectFile addCalendarios(ProjectFile project, JSONArray calendariosJson) throws JSONException {
        ProjectCalendar defaultCalendario = project.getDefaultCalendar();
        JSONObject json = null;
        for(int i=0; i< calendariosJson.length(); i++){
            json = calendariosJson.getJSONObject(i);
            if (!json.getString("nombre").equalsIgnoreCase(defaultCalendario.getName())){
                // add mew calendar
                ProjectCalendar calendar = project.addCalendar();
                //set name, set unique id
                calendar.setName(json.getString("nombre"));
                calendar.setUniqueID(json.getInt("calenderID"));
            }
        }
        return project;
    }

    public ProjectFile linkCalendarioRecurso (ProjectFile project, JSONArray calendariosJson) throws JSONException {
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
                        // link calendar to resource
                        calendar.setResource(resource);
                        resource.setResourceCalendar(calendar);
                    }
                }
            }
        }
        return project;
    }

    public ProjectFile setCalendariosBase (ProjectFile project, JSONArray calendariosJson) throws JSONException {
        ProjectCalendar calendarParent = null;
        JSONObject json = null;
        ProjectCalendar calendar = null;
        for (int i=0; i<calendariosJson.length(); i++){
            json = calendariosJson.getJSONObject(i);
            calendar = project.getCalendarByName(json.getString("nombre"));
            if (calendar != null){
                // get base calendar
                calendarParent = project.getCalendarByName(json.getString("calenderBase"));
                // set base calendar
                calendar.setParent(calendarParent);
            }
        }
        return project;
    }

    public ProjectFile setCalendarios (ProjectFile project, JSONArray calendariosJson) throws Exception {
        JSONObject jsonParent = null;
        JSONObject json = null;
        ProjectCalendar calendar = null;
        for (int i=0; i<calendariosJson.length(); i++){
            json = calendariosJson.getJSONObject(i);
            calendar = project.getCalendarByName(json.getString("nombre"));
            if (json.getString("nombre").equalsIgnoreCase(json.getString("calenderBase"))){
                // if calendar name = base calendar name: is a base calendar
                // set calendario
                calendar = setCalendario(calendar, json, jsonParent);
            }else{
                // get base calendar
                jsonParent = getBaseCalendarJson(calendariosJson, calendar.getParent().getName());
                // set caledario
                calendar = setCalendario(calendar, json, jsonParent);
            }
        }
        return project;
    }

    public JSONObject getBaseCalendarJson (JSONArray calendarios, String calendarName) throws JSONException {
        JSONObject json = null;
        for (int i =0; i<calendarios.length(); i++){
            json = calendarios.getJSONObject(i);
            if (json.getString("nombre").equalsIgnoreCase(calendarName)){
                return json;
            }
        }
        //return json base calendar
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
        for (int i=0; i<dias.length(); i++){
            Day day = Day.valueOf(dias.get(i).toString());
            calendario.setWorkingDay(day, dT);         
        }
        return calendario;      
    }

    public ProjectCalendar setHorario (JSONArray horarioCalendario, ProjectCalendar calendario) throws JSONException, ParseException {
        for (int  i=0; i<horarioCalendario.length(); i++){
            /*
            horario [0] = day name
            horario [1] = date range 1
            horario [2] = date range 2
            */
            String[] horario = horarioCalendario.getString(i).split("/");

            // get day
            Day day = Day.valueOf(horario[0]);
            // parse String format "Sat Dec 31 08:00:00 COT 1983" to Date
            DateRange dateRange = new DateRange(new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(horario[1]), new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(horario[2]));
            DateRange dateRange2 = new DateRange(new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(horario[3]), new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(horario[4]));
            
            ProjectCalendarHours hours = calendario.addCalendarHours(day);
            hours.addRange(dateRange);
            hours.addRange(dateRange2);
        }
        return calendario;
    }

    public ProjectCalendar setExcepciones (JSONArray excepcionesCalendario, ProjectCalendar calendario) throws JSONException, ParseException {
        for (int  i=0; i<excepcionesCalendario.length(); i++){
            /*
            excepcion [0] = exception name
            excepcion [1] = date range 1
            excepcion [2] = date range 2
            */
            String[] excepcion = excepcionesCalendario.getString(i).split("/");           
            calendario.addCalendarException(new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(excepcion[1]), new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(excepcion[2]));
            ProjectCalendarException ex = calendario.getException(new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy").parse(excepcion[1]));
            if (!excepcion[0].equals("null")){
                ex.setName(excepcion[0]);
            }
        }
        return calendario;
    }
}






package com.example.ManagerProject.Controller;
import java.io.Console;
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
import net.sf.mpxj.ganttproject.schema.Date;
import net.sf.mpxj.reader.*;  
import net.sf.mpxj.writer.*;  
import net.sf.mpxj.mpp.*;
import net.sf.mpxj.planner.schema.Days;


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
        
        projectObj = addCalendario(projectObj,jsonObject);
        //projectObj = addColumnas(projectObj,jsonObject);
        projectObj =  addRecursos(projectObj,jsonObject);
        projectObj = addTarea(projectObj,jsonObject);

        ProjectWriter writer = ProjectWriterUtility.getProjectWriter("HOLA.mpx");  
        writer.write(projectObj,"HOLA.mpx");
        return "Hola Mundo";
    }
    
    public static ProjectFile addTarea(ProjectFile project,JSONObject jsonObject) throws JSONException {
        JSONObject jsonObject2 = ((JSONObject)(jsonObject.get("allColum")));
        for(int i=0;i< ((JSONArray)(jsonObject.get("tareas"))).length();i++){
            try {
                JSONObject json = ((JSONArray)(jsonObject.get("tareas"))).getJSONObject(i);

                (project.addTask()).setID(json.getInt("id"));
                (project.getTaskByID(json.getInt("id"))).setName(json.getString("name"));
                (project.getTaskByID(json.getInt("id"))).setUniqueID(json.getInt("uniqueID"));
                (project.getTaskByID(json.getInt("id"))).setActive(json.getBoolean("estado"));
                JSONObject jsonObject3 = jsonObject2.get(json.getInt("id"));
                (project.getTaskByID(json.getInt("id"))).setActive(json.getBoolean("estado"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for(int i=0;i< ((JSONArray)(jsonObject.get("tareas"))).length();i++){
            try {
                JSONObject json = ((JSONArray)(jsonObject.get("tareas"))).getJSONObject(i);

                (project.addTask()).setID(json.getInt("id"));
                (project.getTaskByID(json.getInt("id"))).setName(json.getString("name"));
                (project.getTaskByID(json.getInt("id"))).setUniqueID(json.getInt("uniqueID"));
                (project.getTaskByID(json.getInt("id"))).setActive(json.getBoolean("estado"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return project;
    }

    public static ProjectFile addRecursos(ProjectFile project,JSONObject jsonObject){
        try {
            JSONArray array = (JSONArray) jsonObject.get("recursos");
            for(int i=0; i< array.length();i++){
                Resource resource = project.addResource();
                resource.setName(((JSONObject)array.get(i)).getString("name"));
                resource.setID(((JSONObject)array.get(i)).getInt("id"));
                
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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




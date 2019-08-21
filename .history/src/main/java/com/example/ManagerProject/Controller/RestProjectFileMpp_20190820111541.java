

package com.example.ManagerProject.Controller;
import java.io.Console;
import java.io.File;

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
    
    @GetMapping(value = "/dato")
    public String postJson(@RequestBody String payload) throws Exception{
     
		File file = ResourceUtils.getFile("classpath:"+"010101010010101001010101001010100101.mpp");
   
        ProjectReader reader = new MPPReader();  
        ProjectFile projectObj = reader.read(file);
        JSONObject jsonObject= new JSONObject(payload);
        System.out.print("Filtros:" + projectObj.getFilters());
        FilterContainer filtros = projectObj.getFilters();
        for(Filter filter:filtros.getResourceFilters() ){
            System.out.println("Filter name : " + filter.getName());

        }
        
        
        
    
        
        //fields.getCustomField(field.getFieldType()).setAlias("hola");
        //System.out.println("Estas son las clases que se muestran: " + field.getFieldType().getDataType());
        //System.out.println("Estas son las clases que se : " + field.getFieldType().getName());
        
        
        
        System.out.println("Agregado Todas la Columans: "+ addColumnas(projectObj,jsonObject).getTables().get(0).getColumns());
        
        for(int i=0;i< ((JSONArray)(jsonObject.get("tareas"))).length();i++){
            try {

                JSONObject json = ((JSONArray)(jsonObject.get("tareas"))).getJSONObject(i);

                (projectObj.addTask()).setID(json.getInt("id"));
                (projectObj.getTaskByID(json.getInt("id"))).setName(json.getString("name"));
                (projectObj.getTaskByID(json.getInt("id"))).setUniqueID(json.getInt("uniqueID"));
                (projectObj.getTaskByID(json.getInt("id"))).setActive(json.getBoolean("estado"));
                
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
       
       
        //addDataTablas(projectObj,jsonObject);
        System.out.println("Agregado Recursos "+ addResursos(projectObj,jsonObject).getAllResources());
        //System.out.println("Este es dato agregado:"+projectObj.getAllTasks());
        ProjectWriter writer = ProjectWriterUtility.getProjectWriter("HOLA.mpx");  
        writer.write(projectObj,"HOLA.mpx");

        return "Hola Mundo";
    }
    public static ProjectFile addResursos(ProjectFile project,JSONObject jsonObject){
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
        
        FieldType fieldType = new FieldType(){
        
            @Override
            public int getValue() {
                return 11;
            }
        
            @Override
            public String name() {
                return "INDICATORS";
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
                return "Prueba";
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
        CustomField ad = new CustomField(fieldType, fields);
        CustomFieldValueItem item = new CustomFieldValueItem(2131232);
        fields.registerValue(item);
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

        Table table = project.getTables().get(0);
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
        project.getTables().add(table2);
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

  

}




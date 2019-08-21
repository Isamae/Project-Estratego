

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
        
        /*System.out.print("Filtros:" + projectObj.getFilters());
        FilterContainer filtros = projectObj.getFilters();
        for(Filter filter:filtros.getResourceFilters() ){
            System.out.println("Filter name : " + filter.getName());

        }*/
        //fields.getCustomField(field.getFieldType()).setAlias("hola");
        //System.out.println("Estas son las clases que se muestran: " + field.getFieldType().getDataType());
        //System.out.println("Estas son las clases que se : " + field.getFieldType().getName());
        System.out.println("Agregado Todas la Columans: "+ addColumnas(projectObj,jsonObject).getTables().get(0).getColumns());
       
        //addDataTablas(projectObj,jsonObject);
        System.out.println("Agregado Recursos "+ addResursos(projectObj,jsonObject).getAllResources());
        System.out.println("Tareas:"+addTarea(projectObj,jsonObject).getAllTasks());
        ProjectWriter writer = ProjectWriterUtility.getProjectWriter("HOLA.mpx");  
        writer.write(projectObj,"HOLA.mpx");

        return "Hola Mundo";
    }

    public static ProjectFile addTarea(ProjectFile project,JSONObject jsonObject) throws JSONException {
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
                return 617;
            }
        
            @Override
            public String name() {
                return "DURATION_TEXT";
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
                return "Duration";
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
        CustomField customField = new CustomField(fieldType, fields);
        
        Column column = new Column(project);
        System.out.println("ViSTA: "+ project.getViews().get(0));

        column.setFieldType(customField.getFieldType());
        column.setTitle("Hola Mundo");
        column.setWidth(14);
        project.getTables().get(0).getColumns().add(column);
        
        CustomFieldValueItem item = new CustomFieldValueItem(2131232);
        item.setDescription("hola Mundo");
        item.setParent(200);
        fields.registerValue(item);
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

  

}




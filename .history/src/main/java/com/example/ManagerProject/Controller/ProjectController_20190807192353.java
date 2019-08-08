package com.example.ManagerProject.Controller;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Day;
import net.sf.mpxj.DayType;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.UniversalProjectReader;

import java.util.List;

import com.example.ManagerProject.Object.Project;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.MalformedJsonException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import org.springframework.util.ResourceUtils;


public class ProjectController{
    
    public JsonObject getjsonProject() throws Exception, IOException, com.google.gson.JsonSyntaxException, com.google.gson.stream.MalformedJsonException{

        Project project = new Project();
        File file = ResourceUtils.getFile("classpath:"+"Casa6.mpp");
        ProjectReader reader = new UniversalProjectReader();
		ProjectFile archivo = reader.read(file); 

		String jsonString = "{ ";
		jsonString = jsonString + //" calendarios : " +getCalendarios(project,archivo)
		 " recursos : " + project.getRecursos(archivo)
		//+ " , " + " tareas : " + project.getTareas(archivo)
		///+ " , " + " asigRecursos : " + project.asignacionesRecursos(archivo)
		//+ " , " + " allColum : " + project.columnasTask(archivo)
		+ "}";
		
		///JSONObject jsonObject= new JSONObject(jsonString);
		System.out.println(jsonString);
		Gson gson = new Gson();
		Name name = gson.fromJson(jsonString, Name.class);
		System.out.println("Name is "+name.getname());
		JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
		
        return jsonObject;
        
	}
	
	public String getCalendarios(Project project, ProjectFile archivo){
		String calendarios = "[ ";
		List < ProjectCalendar > calendars = archivo.getCalendars ();
		for(ProjectCalendar calendar : calendars){

			calendarios = calendarios 
			+ " { " + " nombre : " + calendar.getName() 
			+ " , " + "diaslab : " + (project.getDiasCalendario (calendar, DayType.WORKING)).toString() 
			+ " , " + "diasnolab : " + (project.getDiasCalendario (calendar, DayType.NON_WORKING)).toString() 
			+ " , " + "calenderDefault :" + (project.getDiasCalendario (calendar, DayType.DEFAULT)).toString() 
			+ " , " + "calenderHorario :" + (project.getHorasCalendario (calendar, project.getDiasCalendario (calendar, DayType.WORKING))).toString() 
			+ " , " + "calenderExcepciones :" + (project.getExcepcionesCalendario (calendar)).toString()
			+ " } " + " ,";

		}
		if(calendarios.length()>1){
            calendarios = calendarios.substring(0, calendarios.length()-1) ;
        }
        else{}

        calendarios = calendarios + "]" ;
        return calendarios;
	}
	
}
package com.example.ManagerProject.Controller;

import net.sf.mpxj.ProjectFile;

import net.sf.mpxj.DayType;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.UniversalProjectReader;

import java.util.List;

import com.example.ManagerProject.Object.Project;
import com.fasterxml.jackson.core.JsonParseException;


import java.io.File;


import org.springframework.boot.configurationprocessor.json.JSONObject;



public class ProjectController{
    
    public JSONObject getjsonProject(File file) throws Exception, JsonParseException{

        Project project = new Project();
		//File file = ResourceUtils.getFile("classpath:"+"Casa6.mpp");
		System.out.print("Este es el archivo usado:"+ file);
        ProjectReader reader = new UniversalProjectReader();
		ProjectFile archivo = reader.read(file); 
		System.out.println(archivo.getProjectConfig().getNextTaskUniqueID());
        System.out.println(archivo.getProjectProperties().getName());
        System.out.println(archivo.getProjectProperties().getActualFinish());
        System.out.println(archivo.getProjectProperties().getActualStart());
        System.out.println(archivo.getProjectProperties().getBaselineDate());
        System.out.println(archivo.getProjectProperties().getBaselineStart());
        System.out.println(archivo.getProjectProperties().getStartDate());
        System.out.println(archivo.getProjectProperties().getFinishDate());
        System.out.println(archivo.getFinishDate());
        System.out.println(archivo.getStartDate());
		
		String jsonString = "{ ";
		jsonString = jsonString + " 'calendarios' : " +getCalendarios(project,archivo)
		+ " , " + " 'recursos' : " + project.getRecursos(archivo)
		+ " , " + " 'tareas' : " + project.getTareas(archivo)
		+ " , " + " asigRecursos : " + project.asignacionesRecursos(archivo)
		+ " , " + " allColum : " + project.columnasTask(archivo)
		+ " , " + " FinishDate : " + "'" +archivo.getFinishDate() +"'"
		+ "}";
		
		JSONObject jsonObject= new JSONObject(jsonString);
		
		//JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
        return jsonObject;
        
	}
	
	public String getCalendarios(Project project, ProjectFile archivo){
		String calendarios = "[ ";
		List < ProjectCalendar > calendars = archivo.getCalendars ();
		for(ProjectCalendar calendar : calendars){

			calendarios = calendarios 
			+ " { " + " 'nombre' : " + " ' " + calendar.getName() + " ' "
			+ " , " + " 'diaslab' : " + (project.getDiasCalendario (calendar, DayType.WORKING)).toString() 
			+ " , " + " 'diasnolab' : " + (project.getDiasCalendario (calendar, DayType.NON_WORKING)).toString() 
			+ " , " + " 'calenderDefault' :" + (project.getDiasCalendario (calendar, DayType.DEFAULT)).toString() 
			+ " , " + " 'calenderHorario' :" + (project.getHorasCalendario (calendar, project.getDiasCalendario (calendar, DayType.WORKING))).toString() 
			+ " , " + " 'calenderExcepciones' :" + (project.getExcepcionesCalendario (calendar)).toString()
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
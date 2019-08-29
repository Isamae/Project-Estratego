package com.example.ManagerProject.Controller;

import net.sf.mpxj.ProjectFile;

import net.sf.mpxj.DayType;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarContainer;
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
		System.out.println("Este es el archivo usado:"+ file);
        ProjectReader reader = new UniversalProjectReader();
		ProjectFile archivo = reader.read(file); 
		
		String jsonString = "{ ";
		jsonString = jsonString + " 'calendarios' : " +getCalendarios(project,archivo)
		+ " , " + " 'defaultCalendario' : " + getDefaultCalendario(project,archivo)
		+ " , " + " 'baselineCalendario' : " + getBaselineCalendario(project,archivo)
		+ " , " + " 'recursos' : " + project.getRecursos(archivo)
		+ " , " + " 'tareas' : " + project.getTareas(archivo)
		+ " , " + " asigRecursos : " + project.asignacionesRecursos(archivo)
		+ " , " + " ColumnasTabla1 : " + project.getColumnaTable1(archivo)
		+ " , " + " CamposPersonalizados : " + project.getCamposPersonalizados(archivo)
		+ " , " + " FinishDate : " + "'" +archivo.getFinishDate() +"'"
		+ " , " + " StartDate : " + "'" +archivo.getStartDate() +"'"
		+ " , " + " PStartDate : " + "'" +archivo.getProjectProperties().getStartDate() +"'"
		+ " , " + " PFinishDate : " + "'" +archivo.getProjectProperties().getFinishDate() +"'"
		+ " , " + " projectName : " + "'" + archivo.getProjectProperties().getName() +"'"
		+ " , " + " projectTittle : " + "'" + archivo.getProjectProperties().getProjectTitle() +"'"
		+ " , " + " projectCompany : " + "'" + archivo.getProjectProperties().getCompany() +"'"
		+ " , " + " projectAuthor : " + "'" + archivo.getProjectProperties().getAuthor() +"'"
		+ "}";

		JSONObject jsonObject= new JSONObject(jsonString);
		
		//JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
        return jsonObject;
        
	}
	
	public String getCalendarios(Project project, ProjectFile archivo){
		String calendarios = "[ ";
		List < ProjectCalendar > calendars = archivo.getCalendars ();
		
		for(ProjectCalendar calendar : calendars){
			if (calendar != null){
				calendarios = calendarios 
				+ " { " + " 'nombre' : " + "'" + calendar.getName() + "'"
				+ " , " + " 'diaslab' : " + (project.getDiasCalendario (calendar, DayType.WORKING)).toString() 
				+ " , " + " 'diasnolab' : " + (project.getDiasCalendario (calendar, DayType.NON_WORKING)).toString() 
				+ " , " + " 'calenderDefault' :" + (project.getDiasCalendario (calendar, DayType.DEFAULT)).toString() 
				+ " , " + " 'calenderHorario' :" + (project.getHorasCalendario (calendar, project.getDiasCalendario (calendar, DayType.WORKING))).toString() 
				+ " , " + " 'calenderExcepciones' :" + (project.getExcepcionesCalendario (calendar)).toString()
				+ " , " + " 'calenderBase' : " + "'" + (project.getCalendarioBase(calendar, archivo)).toString() + "'"
				+ " , " + " 'calenderID' : " + "'" + (calendar.getUniqueID().toString()) + "'"
				+ " , " + " 'recursoID' : " + "'" + (project.getRecursoCalendario(calendar, archivo)).toString() + "'"
				+ " } " + " ,";
			}
			
		}
		if(calendarios.length()>1){
            calendarios = calendarios.substring(0, calendarios.length()-1) ;
        }
        calendarios = calendarios + "]" ;
        return calendarios;
	}

	public String getDefaultCalendario(Project project, ProjectFile archivo){
		String calendario = "[ ";
		ProjectCalendar calendar = archivo.getDefaultCalendar();
		if (calendar != null){
			calendario = calendario 
			+ " { " + " 'nombre' : " + "'" + calendar.getName() + "'"
			+ " , " + " 'diaslab' : " + (project.getDiasCalendario (calendar, DayType.WORKING)).toString() 
			+ " , " + " 'diasnolab' : " + (project.getDiasCalendario (calendar, DayType.NON_WORKING)).toString() 
			+ " , " + " 'calenderDefault' :" + (project.getDiasCalendario (calendar, DayType.DEFAULT)).toString() 
			+ " , " + " 'calenderHorario' :" + (project.getHorasCalendario (calendar, project.getDiasCalendario (calendar, DayType.WORKING))).toString() 
			+ " , " + " 'calenderExcepciones' :" + (project.getExcepcionesCalendario (calendar)).toString()
			+ " , " + " 'calenderBase' : " + "'" + (project.getCalendarioBase(calendar, archivo)).toString() + "'"
			+ " , " + " 'calenderID' : " + "'" + (calendar.getUniqueID().toString()) + "'"
			+ " , " + " 'recursoID' : " + "'" + (project.getRecursoCalendario(calendar, archivo)).toString() + "'"
			+ " } " + " ,";
		}
			
		
		if(calendario.length()>1){
            calendario = calendario.substring(0, calendario.length()-1) ;
        }
        calendario = calendario + "]" ;
        return calendario;
	}

	public String getBaselineCalendario(Project project, ProjectFile archivo){
		String calendario = "[ ";
		ProjectCalendar calendar = archivo.getBaselineCalendar();
		if (calendar != null){
			calendario = calendario 
			+ " { " + " 'nombre' : " + "'" + calendar.getName() + "'"
			+ " , " + " 'diaslab' : " + (project.getDiasCalendario (calendar, DayType.WORKING)).toString() 
			+ " , " + " 'diasnolab' : " + (project.getDiasCalendario (calendar, DayType.NON_WORKING)).toString() 
			+ " , " + " 'calenderDefault' :" + (project.getDiasCalendario (calendar, DayType.DEFAULT)).toString() 
			+ " , " + " 'calenderHorario' :" + (project.getHorasCalendario (calendar, project.getDiasCalendario (calendar, DayType.WORKING))).toString() 
			+ " , " + " 'calenderExcepciones' :" + (project.getExcepcionesCalendario (calendar)).toString()
			+ " , " + " 'calenderBase' : " + "'" + (project.getCalendarioBase(calendar, archivo)).toString() + "'"
			+ " , " + " 'calenderID' : " + "'" + (calendar.getUniqueID().toString()) + "'"
			+ " , " + " 'recursoID' : " + "'" + (project.getRecursoCalendario(calendar, archivo)).toString() + "'"
			+ " } " + " ,";
		}
			
		
		if(calendario.length()>1){
            calendario = calendario.substring(0, calendario.length()-1) ;
        }
        calendario = calendario + "]" ;
        return calendario;
	}
	
}
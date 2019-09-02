package com.example.ManagerProject.Controller;

import net.sf.mpxj.ProjectFile;

import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.UniversalProjectReader;

import com.example.ManagerProject.Object.Project;
import com.fasterxml.jackson.core.JsonParseException;

import java.io.File;

import org.springframework.boot.configurationprocessor.json.JSONObject;

public class ProjectController{
    
    public JSONObject getjsonProject(File file) throws Exception, JsonParseException{

        Project project = new Project();
        ProjectReader reader = new UniversalProjectReader();
		ProjectFile archivo = reader.read(file); 
		
		String jsonString = "{ ";
		jsonString = jsonString + " 'calendarios' : " + project.getCalendarios(project,archivo)
		+ " , " + " 'defaultCalendario' : " + project.getDefaultCalendario(project,archivo)
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
	
}
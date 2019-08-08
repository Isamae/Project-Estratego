package com.example.ManagerProject.Controller;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Day;
import net.sf.mpxj.DayType;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.UniversalProjectReader;

import java.util.List;
import java.io.File;
import java.util.ArrayList;

import com.example.ManagerProject.Object.Project;

import org.springframework.util.ResourceUtils;


public class ProjectController{
    
    public String getjsonProject() throws Exception{

        Project project = new Project();
        File file = ResourceUtils.getFile("classpath:"+"Casa6.mpp");
        ProjectReader reader = new UniversalProjectReader();
		ProjectFile archivo = reader.read(file); 
		  
        return getCalendarios(project,archivo);
        
	}
	
	public String getCalendarios(Project project, ProjectFile archivo){
		String calendarios = "[ ";
		List < ProjectCalendar > calendars = archivo.getCalendars ();
		for(ProjectCalendar calendar : calendars){
			calendarios = calendarios 
			+ " { " + " nombre : " + calendar.getName() + " , "
			+
			ArrayList <Day> diasLaborables = project.getDiasCalendario (calendar, DayType.WORKING);
			ArrayList <Day> noLaborables = project.getDiasCalendario (calendar, DayType.NON_WORKING);
			ArrayList <Day> diasDefault = project.getDiasCalendario (calendar, DayType.DEFAULT);
			
			System.out.println("\nDias laborables: " + diasLaborables.toString());
			System.out.println("\nDias no laborables: " + noLaborables.toString());
			System.out.println("\nDias default: " + diasDefault.toString());

			ArrayList <String> horario = project.getHorasCalendario (calendar, diasLaborables);
			System.out.println("\nHoras calendario:" + horario.toString());	
			
			ArrayList <String> excepciones = project.getExcepcionesCalendario (calendar);
			System.out.println("\nExcepciones calendario: Feriados" + excepciones.toString());

        }
		return "";
	}
	
}
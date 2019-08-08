
    
package com.example.ManagerProject.Controller;

import org.springframework.web.bind.annotation.RestController;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.UniversalProjectReader;

import java.awt.List;
import java.io.File;

import com.example.ManagerProject.Object.Project;

import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * RestProject
 */
@RestController
@RequestMapping("/Project")
public class RestProject {
    
    /*@GetMapping
    public String getjsonProject() throws Exception{
        Project project = new Project();
        return project.getTareas("Casa3.mpp");
    }*/
    @GetMapping
    public String getjsonProject() throws Exception{
        Project project2 = new Project();
        File file = ResourceUtils.getFile("classpath:"+"Casa6.mpp");
        ProjectReader reader = new UniversalProjectReader();
        ProjectFile project = reader.read(file);
        

        List < ProjectCalendar > calendars = project.getCalendars ();
		for(ProjectCalendar calendar : calendars){
			System.out.println("\nNombre calendario: " + calendar.getName());
			ArrayList <Day> diasLaborables = project2.getDiasCalendario (calendar, DayType.WORKING);
			ArrayList <Day> noLaborables = project2.getDiasCalendario (calendar, DayType.NON_WORKING);
			ArrayList <Day> diasDefault = project2.getDiasCalendario (calendar, DayType.DEFAULT);
			
			System.out.println("\nDias laborables: " + diasLaborables.toString());
			System.out.println("\nDias no laborables: " + noLaborables.toString());
			System.out.println("\nDias default: " + diasDefault.toString());

			ArrayList <String> horario = getHorasCalendario (calendar, diasLaborables);
			System.out.println("\nHoras calendario:" + horario.toString());	
			
			ArrayList <String> excepciones = getExcepcionesCalendario (calendar);
			System.out.println("\nExcepciones calendario: Feriados" + excepciones.toString());

        }
        return ""
        //return  project2.columnasTask(project);
    }
    /*@PostMapping
    public String postjsonProject() throws Exception{
        Project project = new Project();
        return project.getTareas("Casa3.mpp");
    }*/


    
    
}
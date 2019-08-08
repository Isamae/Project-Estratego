package com.example.ManagerProject.Controller;
public class ProjectController{
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

			ArrayList <String> horario = project2.getHorasCalendario (calendar, diasLaborables);
			System.out.println("\nHoras calendario:" + horario.toString());	
			
			ArrayList <String> excepciones = project2.getExcepcionesCalendario (calendar);
			System.out.println("\nExcepciones calendario: Feriados" + excepciones.toString());

        }
 
}
package com.example.ManagerProject.Object;



import net.sf.mpxj.Column;
import net.sf.mpxj.CustomField;
import net.sf.mpxj.Day;
import net.sf.mpxj.DayType;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import net.sf.mpxj.Task;
public class Project  {

    public static String getRecursos(ProjectFile project) throws Exception{
        String recursos ="[";

        for (Resource resource : project.getAllResources()){

            if(resource.getName() ==null) {}
            else{
                recursos = recursos + "{ " + " 'name' :'" + resource.getName() 
                +"' , "+ " 'id' : '" + resource.getID()  
                +"' , "+ " 'idUni' : '" + resource.getUniqueID() 
                +"' }"  + ",";
            }
            
        }

        if(recursos.length()>1){
            recursos = recursos.substring(0, recursos.length()-1) ;
        }
        else{}

        recursos = recursos + "]" ;
        return recursos;
    }
    
    public static String getTareas(ProjectFile project)  throws Exception{
        String tareas ="[";
    
        for (Task task : project.getAllTasks())
        {
            Iterator<CustomField> iter =  project.getCustomFields().iterator();
            while(iter.hasNext() != null) {
                System.out.println( iter.next().getAlias());
            }
            
            if(task.getName() ==null || task.getID() ==null) {}
            else{
                tareas = tareas  
                + "{ " + " 'name' :" +  "'"+task.getName()+"'" +" , "+ " id : " +task.getID() +" , "+ " uniqueID : " +task.getUniqueID()+" , "+ " estado : " +task.getActive()
                + " , " + " 'recursos': " + asignacionesRecursosTarea(project,task.getID())  
                + " , " + " 'predecesoras': " + relacionesPredecesoraTareas(project,task.getID())
                + " , " + " 'duracion': " + "'" + task.getDuration()+"'" 
                + " , " + " 'AfechaInicio': " + "'" +task.getActualStart() +"'" 
                + " , " + " 'AfechaFin': " + "'" + task.getActualFinish() + "'" 
                + " , " + " 'TfechaInicio': " + "'" +task.getStartText() +"'" 
                + " , " + " 'TfechaFin': " + "'" + task.getFinishText() + "'" 
                + " , " + " 'fechaInicio': " + "'" +task.getStart() +"'" 
                + " , " + " 'fechaFin': " + "'" + task.getFinish() + "'" 
                + " , " + " 'hijos': " +  getHijos(task) 
                + " , " + " 'LevelAssignments': " + "'"+ task.getLevelAssignments() +"'"
                + " , " + " 'OutlineLevel': " + "'"+ task.getOutlineLevel() +"'"
                + " , " + " 'OutlineNumber': " + "'"+ task.getOutlineNumber() +"'"
                + " , " + " 'Priority': " + "'"+ task.getPriority() +"'"
                + " , " + " 'Sucesores': " + relacionesSucesorasTareas(project,task.getID())
                + " , " + " 'Type': " + "'"+ task.getType() +"'"
                + " , " + " 'ActualDuration': " + "'"+ task.getActualDuration() +"'"
                + " , " + " 'ActualWork': " + "'"+ task.getActualWork() +"'"
                + " , " + " 'BaselineDuration': " + "'"+ task.getBaselineDurationText() +"'"
                + " , " + " 'DurationText': " + "'"+ task.getDurationText() +"'"
                + " , " + " 'BaselineFinish': " + "'"+ task.getBaselineFinish() +"'"
                + " , " + " 'BaselineStart': " + "'"+ task.getBaselineStart() +"'"

                + " }"  + " ,";
           
            }
        }

        if(tareas.length()>1){
            tareas = tareas.substring(0, tareas.length()-1) ;
        }
        else{}

        tareas = tareas + "]" ;

        return tareas;
    }
    public static String getHijos(Task task){
        String tareas = "[";

        List<Task> hijos = task.getChildTasks();
        for (int i = 0 ; i< hijos.size(); i++){
            Task tarea = hijos.get(i);
            tareas = tareas + "{ " 
            + "'id'" + ":" + tarea.getID() + ","
            + "'idUnique'" + ":" + tarea.getUniqueID() +  "," 
            + "'name'" + ":" + "'"+ tarea.getName()+ "'" +  ","
            + " 'LevelAssignments': " + "'"+ task.getLevelAssignments()+"'" +  "," 
            + " 'OutlineLevel': " + "'"+ task.getOutlineLevel() +"'" +  ","
            + " 'OutlineNumber': " + "'"+ task.getOutlineNumber() +"'" +  ","
            + " 'Priority': " + "'"+ task.getPriority() +  "'"+ ","
            + " 'Successors': " + "'"+ task.getSuccessors() + "'"+   ","
            + " 'Type': " +  "'" +task.getType() +"'"
            + "}" +",";


        }
        if(tareas.length()>1){
            tareas = tareas.substring(0, tareas.length()-1) ;
        }
        else{}
        tareas = tareas + "]";
        return tareas;

    }

    public static String asignacionesRecursos(ProjectFile project)  throws Exception{
        String asignaciones ="[";

        for (ResourceAssignment assignment : project.getAllResourceAssignments()){
            Task task = assignment.getTask();
            int taskId;
            if (task == null){
                taskId = -1;
            }
            else{
                taskId = task.getID();
            }
            
            Resource resource = assignment.getResource();
            int resourceId;
            if (resource == null){
                resourceId = -1;
            }
            else{
                resourceId = resource.getID();
            }

            if(taskId == -1 || resourceId == -1) {}
            else{
                asignaciones = asignaciones  + "{ " + "'idTask' :" +  taskId +" , "+ " 'idResource' : " + resourceId +" }"  + ",";
            }

        }
        if(asignaciones.length()>1){
            asignaciones = asignaciones.substring(0, asignaciones.length()-1) ;
        }
        else{}

        asignaciones = asignaciones + "]" ;
        return asignaciones;
    }

    public static String asignacionesRecursosTarea(ProjectFile project, int  idTask)  throws Exception{
       
        String asignaciones ="[";
        Task task = project.getTaskByID(idTask);
        if(task == null) {}
        else{
            for (ResourceAssignment assignment : task.getResourceAssignments())
            {
                Resource resource = assignment.getResource();
            

                if (resource == null){}
                else
                {
                    asignaciones = asignaciones +  resource.getID()  + ",";
                    
                }

                
            }
            if(asignaciones.length()>1){
                asignaciones = asignaciones.substring(0, asignaciones.length()-1) ;
            }
            else{}
        }
        

        asignaciones = asignaciones + "]" ;
        return asignaciones;
    }

    public static String listHierarchyTask(ProjectFile file)
    {
        String tareasM ="["; 
        for (Task task : file.getChildTasks())
        {
            if(task.getName() ==null) {}
            else{
                tareasM = tareasM + "{ " + task.getID()  +  " : " + listHierarchy(task, " ") + " }" + " ," ;
            }
            
        }

        if(tareasM.length()>1){
            tareasM = tareasM.substring(0, tareasM.length()-1) ;
        }
        else{}

        tareasM = tareasM + "]" ;
        return tareasM;
    }

    private static String listHierarchy(Task task, String indent)
    {
        String tareasH ="[";
        for (Task child : task.getChildTasks())
        {
            tareasH = tareasH + "{ " +  child.getID() +":"+ listHierarchy(child, indent + " ")+ "}"+","; 
            
        }

        if(tareasH.length()>1){
            tareasH = tareasH.substring(0, tareasH.length()-1) ;
        }
        else{}

        tareasH = tareasH + "]" ;
        return tareasH;
    }

    public static String relacionesSucesorasTareas(ProjectFile project, int  idTask) throws Exception
    {
        String relacionSucesora = "[";

        Task task = project.getTaskByID(idTask);
        if(task == null){}
        else{
            List<Relation> sucesoras = task.getSuccessors();
            if (sucesoras != null && sucesoras.isEmpty() == false)
            {
                for (Relation relation : sucesoras)
                {
                    relacionSucesora = relacionSucesora + 
                    " { " + " 'taskId': " + project.getTaskByID((relation.getTargetTask()).getID()).getID()
                    +" , " + " 'type':" + "'"+ relation.getType()+ "'"
                    + " , " + " 'lag': " + "'"+ relation.getTargetTask().getDuration()+"'"
                    + " } " + " ,";
                }
            }
            else{}
            
            
            if(relacionSucesora.length()>1){
                relacionSucesora = relacionSucesora.substring(0, relacionSucesora.length()-1) ;
            }
            else{}
        }
        relacionSucesora = relacionSucesora + "]" ;

        return relacionSucesora;
    }

    public static String relacionesPredecesoraTareas(ProjectFile project, int  idTask) throws Exception
    {
        String relacionPrecedecesora = "[";

        Task task = project.getTaskByID(idTask);
        if(task == null){}
        else{
            List<Relation> predecessors = task.getPredecessors();
            if (predecessors != null && predecessors.isEmpty() == false)
            {
                for (Relation relation : predecessors)
                {
                    relacionPrecedecesora = relacionPrecedecesora + 
                    " { " + " 'taskId': " + project.getTaskByID((relation.getTargetTask()).getID()).getID()
                    +" , " + " 'type':" + "'"+ relation.getType()+ "'"
                    + " , " + " 'lag': " + "'"+ relation.getTargetTask().getDuration()+"'"
                    + " } " + " ,";
                }
            }
            else{}
            
            
            if(relacionPrecedecesora.length()>1){
                relacionPrecedecesora = relacionPrecedecesora.substring(0, relacionPrecedecesora.length()-1) ;
            }
            else{}
        }
        relacionPrecedecesora = relacionPrecedecesora + "]" ;

        return relacionPrecedecesora;
    }

    public static String columnasTask(ProjectFile project) throws Exception
    {
        List<Table> tables= project.getTables();
        Iterator iter = tables.iterator();
        Table table = (Table)iter.next();
        String dataTable = " { " ;
        List tasks = project.getAllTasks();
        Iterator resourceIter = tasks.iterator();
        while (resourceIter.hasNext()){
            Task tarea = (Task)resourceIter.next();
            List columns = table.getColumns();
			Iterator columnIter = columns.iterator();
            Object columnValue = null;
            dataTable = dataTable + "'"+ tarea.getID() +"'"+ ":" +  " { " ; 
            while (columnIter.hasNext()){
                
                Column column = (Column)columnIter.next();
                
				if (column.getFieldType().toString().equalsIgnoreCase("Duration")){
					columnValue = tarea.getDuration();
				}else if (column.getFieldType().toString().equalsIgnoreCase("Start")){
					columnValue = tarea.getStart();
				}else if (column.getFieldType().toString().equalsIgnoreCase("Finish")){
					columnValue = tarea.getFinish();
				}else {
					columnValue = tarea.getCachedValue(column.getFieldType());
                }
                dataTable = dataTable  + "'"+column.getFieldType().toString()+"'" + " : " + "'"+columnValue+"'" + " ,"
                + "'"+"idTarea"+"'" + " : " + "'"+tarea.getID()+"'" + " ,";
            
            }
            if(dataTable.length()>1){
                dataTable = dataTable.substring(0, dataTable.length()-1) ;
            }
            else{}

            dataTable = dataTable  + " } " + " ," ; 
        }
        if(dataTable.length()>1){
            dataTable = dataTable.substring(0, dataTable.length()-1) ;
        }
        else{}

        dataTable = dataTable + "}" ;
        return dataTable;
            
    }

    public String getRecursos (ProjectCalendar calendar){
        String recursos = "";
        System.out.println( "RECURSOS");
        if (calendar.getResource() != null){
            recursos =  calendar.getResource().getID().toString();
        }else{
            recursos = "null";
        }

        return recursos;
    }

	public ArrayList <Day> getDiasCalendario (ProjectCalendar calendar, DayType dT){
		DayType days [] = calendar.getDays();
		ArrayList <Day> dias = new ArrayList<Day>();
		for (int i=1; i<8; i++){
			if (days[i-1].equals(dT)){
				dias.add(Day.getInstance(i));
			}
		}
		return dias;
	}

	public ArrayList <String> getHorasCalendario (ProjectCalendar calendar, ArrayList <Day> diasLaborables){
		ArrayList <String> horarioCalendario = new ArrayList<String>();
		String horario = "'";
		ProjectCalendarHours horas [] = calendar.getHours();
		for (ProjectCalendarHours hora : horas){
			if (hora!= null){
				ProjectCalendarHours c = hora.getParentCalendar().getCalendarHours(diasLaborables.get(0));
				horario = horario + hora.getDay();
				for (int i=0; i<c.getRangeCount(); i++){
					horario = horario + "/" + c.getRange(i).getStart()+"/" + c.getRange(i).getEnd();
                }
                horario = horario + "'";
				horarioCalendario.add(horario);
			}
			horario = "'";
		}
		return horarioCalendario;
    }
    public String getCalendarioBase (ProjectCalendar calendar, ProjectFile file){
        
        System.out.println(calendar.getName() + ", " + file.getDefaultCalendar().getName());
        
        String nombreCalendario = "";
        if (calendar.getParent() == null){
            nombreCalendario = calendar.getName();
            System.out.println(nombreCalendario);
        }else{
            nombreCalendario = file.getDefaultCalendar().getName();
            System.out.println(nombreCalendario);
        }
        return nombreCalendario;
    }

	public ArrayList <String> getExcepcionesCalendario (ProjectCalendar calendar){
		ArrayList <String> excepcionesCalendario = new ArrayList<String>();
		List <ProjectCalendarException> excepciones = calendar.getCalendarExceptions();
		String ex = "'";
		for (ProjectCalendarException exception : excepciones){
			ex = ex + exception.getName() + "/" + exception.getFromDate() + "/" + exception.getToDate() + " '";
			excepcionesCalendario.add(ex);
			ex = "'";
		}
		return excepcionesCalendario;
	}

}
 
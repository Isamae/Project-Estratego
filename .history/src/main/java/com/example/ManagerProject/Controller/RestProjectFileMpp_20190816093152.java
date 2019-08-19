

package com.example.ManagerProject.Controller;
import net.sf.mpxj;   
import net.sf.mpxj.reader;  
import net.sf.mpxj.writer;  
import net.sf.mpxj.mpp; 

/**
 * RestProject
 */
@RestController
@RequestMapping(value = "/project")
public class RestProjectFileMpp {
    

    @GetMapping(value = "/dato")
    public String postJson() throws Exception{
        
        File file2 =  multipartToFile(file,"file");
        ProjectController controller = new  ProjectController();
        return controller.getjsonProject(file2).toString();
    }

    

    
    
}




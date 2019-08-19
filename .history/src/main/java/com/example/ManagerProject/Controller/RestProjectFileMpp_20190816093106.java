

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
public class RestProjecta {
    
    @PostMapping(value = "/dato", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getJson(@RequestParam(value = "file", required = true) final MultipartFile file) throws Exception{
        
        File file2 =  multipartToFile(file,"file");
        ProjectController controller = new  ProjectController();
        return controller.getjsonProject(file2).toString();
    }

    public static File multipartToFile(MultipartFile multipart, String fileName) throws IllegalStateException, IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+fileName);
        multipart.transferTo(convFile);
        return convFile;
    }

    @GetMapping(value = "/dato")
    public String postJson() throws Exception{
        
        File file2 =  multipartToFile(file,"file");
        ProjectController controller = new  ProjectController();
        return controller.getjsonProject(file2).toString();
    }

    

    
    
}




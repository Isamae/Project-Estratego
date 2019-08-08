
    
package com.example.ManagerProject.Controller;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * RestProject
 */
@RestController
@RequestMapping("/Project")
public class RestProject {
    
    @GetMapping
    public @ResponseBody getJson() throws Exception{
        ProjectController controller = new  ProjectController();
        
        return controller.getjsonProject();
    }
    
    @RequestMapping(value="/find/city={city}", method=RequestMethod.GET)
    public  String getCity(@PathVariable String city) throws JsonParseException, IOException
    {      
    ObjectMapper mapper = new ObjectMapper();
    SimpleBeanPropertyFilter theFilter = SimpleBeanPropertyFilter.serializeAllExcept("id","miscellaneous","country","foundin","code","latlong","state");
    FilterProvider filters = new SimpleFilterProvider().addFilter("myFilter", theFilter);
    String content = "";
    StringBuilder builder = new StringBuilder();
    List<Master_City> list = City_Repository.findByCityLikeIgnoreCase(city);
    for (Master_City json : list)
    {
        builder.append( mapper.writer(filters).writeValueAsString(json));
        }
    content = builder.toString();
    return content;
    }

    
    
}


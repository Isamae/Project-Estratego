
    
package com.example.ManagerProject.Controller;

import org.springframework.web.bind.annotation.RestController;



import com.fasterxml.jackson.databind.JsonMappingException;
///import com.google.gson.JsonObject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * RestProject
 */
@RestController
@RequestMapping(value = "/project")
public class RestProject {
    
    @PostMapping(value = "/dato", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getJson() throws Exception{
        SpringApplication.run(RestProject.class, args);
        ProjectController controller = new  ProjectController();

        
        return controller.getjsonProject().toString();
    }
    
    //private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
		return args -> {
			String quote = restTemplate.getForObject(
					"http://localhost:9898/Project/dato", String.class);
			//log.info(quote.toString());
			System.out.println(quote.toString());
		};
		
	}
    

    
    
}


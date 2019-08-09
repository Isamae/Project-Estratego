package com.example.ManagerProject;

import java.util.Arrays;

import com.example.ManagerProject.Object.Project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;



//@SpringBootApplication
@RestController
public class Consumidor {

    // private static final Logger log = LoggerFactory.getLogger(Consumidor.class);

	// public static void main(String args[]) {
	// 	SpringApplication.run(Consumidor.class);
	// }

	// @Bean
	// public RestTemplate restTemplate(RestTemplateBuilder builder) {
	// 	return builder.build();
	// }

	// @Bean
	// public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
	// 	return args -> {
	// 		Project project = restTemplate.getForObject(
	// 				"http://localhost:9898/Project", Project.class);
	// 		log.info(project.toString());
	// 	};
    // }
    
    /**/ 

    RestTemplate restTemplate = new RestTemplate();

    @RequestMapping (value = "/Project/ver", method = RequestMethod.GET)
    public String getProject () {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity entity = new HttpEntity<String>(headers);
        return restTemplate.exchange("http://localhost:9898/Project", HttpMethod.GET, entity, String.class).getBody();
    }
}
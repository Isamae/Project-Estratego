package com.example.ManagerProject.Client;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;



@SpringBootApplication
@RestController
public class Consumidor {

    RestTemplate restTemplate = new RestTemplate();

    @RequestMapping (value = "/dato", method = RequestMethod.GET)
    public String getProject () {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity entity = new HttpEntity<String>(headers);
        return restTemplate.exchange("http://localhost:9898/Project/", HttpMethod.GET, entity, String.class).getBody();
    }

}
	
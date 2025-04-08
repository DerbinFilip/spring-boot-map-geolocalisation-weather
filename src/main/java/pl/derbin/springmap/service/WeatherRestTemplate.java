package pl.derbin.springmap.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
@Component
public class WeatherRestTemplate extends RestTemplate {
    public WeatherRestTemplate() {
        restTemplate();
    }

    public static void restTemplate() {
        new RestTemplate();
    }
}
package pl.derbin.springmap.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.derbin.springmap.model.Point;
import pl.derbin.springmap.model.temperatureModels.TemperatureModel;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class WeatherService {
    private static final String coordinateUrl = "https://raw.githubusercontent.com/albertyw/avenews/master/old/data/average-latitude-longitude-countries.csv";
    private int count = 0;
    public List<Point> getCoordinates() throws IOException {
        List<Point> points = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        RestTemplate restTemplate1 = new RestTemplate();
        String values = restTemplate1.getForObject(coordinateUrl, String.class);
        StringReader stringReader = new StringReader(values);
        CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(stringReader);
        getPointsWithTemperatureAndCountryName(points, restTemplate, parser);
        return points;
    }

    private void getPointsWithTemperatureAndCountryName(List<Point> points, RestTemplate restTemplate, CSVParser parser) {
        for (CSVRecord strings : parser) {
            count++;
            if(count == 20) break;
            String name = strings.get("Country");
            double lat = Double.parseDouble(strings.get("Latitude"));
            double lon = Double.parseDouble(strings.get("Longitude"));
            String temperatureUrl = "https://api.openweathermap.org/data/2.5/weather?units=metric&exclude=current&appid=c31f7965ccc33ae68ce85d475c477f81&lat=" + lat + "&lon=" + lon;
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("", "");
            HttpEntity httpEntity = new HttpEntity(httpHeaders);
            ResponseEntity<TemperatureModel> entity = getTemperatureModelResponseEntity(restTemplate, temperatureUrl, httpEntity);
            addParticularValuesToPointList(points, strings, entity, name);
        }
    }

    private void addParticularValuesToPointList(List<Point> points, CSVRecord strings, ResponseEntity<TemperatureModel> entity, String name) {
        Stream.of(entity.getBody()).filter(Objects::nonNull).map(
                temperature ->
                        new Point(
                                Double.parseDouble(strings.get("Latitude")),
                                Double.parseDouble(strings.get("Longitude")),
                                temperature.getMain().getTemp().toString(),
                                name.trim()
                        )).forEach(points::add);
    }

    private ResponseEntity<TemperatureModel> getTemperatureModelResponseEntity(RestTemplate restTemplate, String temperatureUrl, HttpEntity httpEntity) {
        ResponseEntity<TemperatureModel> entity = restTemplate
                .exchange(
                        temperatureUrl,
                        HttpMethod.GET,
                        httpEntity, TemperatureModel.class
                );
        return entity;
    }
}

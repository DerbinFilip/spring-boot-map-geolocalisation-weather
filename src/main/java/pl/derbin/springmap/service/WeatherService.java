package pl.derbin.springmap.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.derbin.springmap.model.Point;
import pl.derbin.springmap.model.temperatureModels.TemperatureModel;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeatherService {
    private static final int MAX_NUMBER_OF_POINTS = 20;
    private final RestTemplate weatherRestTemplate;
    private final String coordinateUrl;
    private final String weatherUrl;


    protected WeatherService(WeatherRestTemplate weatherRestTemplate,
                             @Value("${country_url}") String coordinateUrl,
                             @Value("${weather_url}") String weatherUrl) {
        this.weatherRestTemplate = weatherRestTemplate;
        this.coordinateUrl = coordinateUrl;
        this.weatherUrl = weatherUrl;
    }

    public List<Point> getCoordinates() throws IOException {
        String values = weatherRestTemplate.getForObject(coordinateUrl, String.class);
        StringReader stringReader = new StringReader(values);
        CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(stringReader);
        return getPointsWithTemperatureAndCountryName(parser);
    }

    private List<Point> getPointsWithTemperatureAndCountryName(CSVParser parser) throws IOException {
        return parser.getRecords().stream()
                .limit(MAX_NUMBER_OF_POINTS)
                .map(this::mapToPoint)
                .collect(Collectors.toList());
    }

    private Point mapToPoint(CSVRecord strings) {
        String name = strings.get("Country");
        double lat = Double.parseDouble(strings.get("Latitude"));
        double lon = Double.parseDouble(strings.get("Longitude"));
        final TemperatureModel temperatureModel = getTemperatureModel(weatherRestTemplate, lat, lon);
        return createParticularValuesToPointList(lat, lon, temperatureModel, name);
    }

    private TemperatureModel getTemperatureModel(RestTemplate weatherRestTemplate, double lat, double lon) {
        String temperatureUrl = weatherUrl + lat + "&lon=" + lon;
        ResponseEntity<TemperatureModel> entity = getTemperatureModelResponseEntity(temperatureUrl);
        return entity.getBody();
    }

    private Point createParticularValuesToPointList(double lat, double lon, TemperatureModel temperatureModel, String name) {
        return new Point(lat, lon, temperatureModel.getMain().getTemp().toString(), name.trim());
    }

    private ResponseEntity<TemperatureModel> getTemperatureModelResponseEntity(String temperatureUrl) {
        return weatherRestTemplate.exchange(temperatureUrl, HttpMethod.GET, null, TemperatureModel.class);
    }
}

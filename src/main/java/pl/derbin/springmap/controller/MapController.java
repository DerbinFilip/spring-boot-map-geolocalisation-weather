package pl.derbin.springmap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.derbin.springmap.service.WeatherService;

@Controller
public class MapController {
    private WeatherService weatherService;

    public MapController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public String getMap(Model model) throws Exception {
        model.addAttribute("points", weatherService.getCoordinates());
        return "map";
    }
}

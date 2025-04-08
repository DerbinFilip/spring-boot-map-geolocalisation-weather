package pl.derbin.springmap.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Point {
    private double lat;
    private double lon;
    private Double temperature;
    private String countryName;
}
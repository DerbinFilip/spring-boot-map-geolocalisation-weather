package pl.derbin.springmap.model;

public class Point {
    private double lat;
    private double lon;
    private String text;
    private String countryName;

    public Point(double lat, double lon, String text, String countryName) {
        this.lat = lat;
        this.lon = lon;
        this.text = text;
        this.countryName = countryName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}

package com.example.weather.models;

public class Temperature {
    public Float temperature;
    public String ts;

    @Override
    public String toString() {
        return this.temperature.toString();
    }
}

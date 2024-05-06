package com.example.weather.models;

public class City {
    public Integer identifier;
    public String description;

    @Override
    public String toString() {
        return this.description;
    }
}

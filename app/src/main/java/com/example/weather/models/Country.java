package com.example.weather.models;

public class Country {
    public Integer identifier;
    public String description;

    @Override
    public String toString() {
        return this.description;
    }
}

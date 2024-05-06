package com.example.weather.models;

public class Region {
    public Integer identifier;
    public String description;

    @Override
    public String toString() {
        return this.description;
    }
}

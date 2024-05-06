package com.example.weather;

import com.example.weather.models.City;
import com.example.weather.models.Country;
import com.example.weather.models.Region;
import com.example.weather.models.Temperature;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {
    @GET("rpc/get_regions")
    Call<List<Region>> getRegions();

    @GET("rpc/get_countries")
    Call<List<Country>> getCountries(
        @Query("region") Integer regionId
    );

    @GET("rpc/get_cities")
    Call<List<City>> getCities(
        @Query("country") Integer countryId
    );

    @GET("rpc/get_daily_temperatures")
    Call<List<Temperature>> getDailyTemperature(
        @Query("city") Integer cityId,
        @Query("ts_from") String from,
        @Query("ts_to") String to
    );
}

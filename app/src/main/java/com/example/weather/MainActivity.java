package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.weather.models.City;
import com.example.weather.models.Country;
import com.example.weather.models.Region;
import com.example.weather.models.Temperature;
import com.example.weather.view.ChartView;
import com.google.android.material.slider.Slider;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private DateTimeFormatter tsFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00");

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Retrofit retrofit;
    private Api api;

    private ChartView chartView;
    private Spinner spRegion;
    private Spinner spCountry;
    private Spinner spCity;
    private Slider slFrom;
    private Slider slTo;
    private TextView tvDate;

    private Integer currentCityId = -1;
    private LocalDate from = LocalDate.now().minusDays(2000);
    private LocalDate to = LocalDate.now().minusDays(2000);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofit = new Retrofit.Builder()
                .baseUrl("http://194.87.68.149:5021/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);

        chartView = findViewById(R.id.chart);

        spRegion = findViewById(R.id.sp_region);
        spRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Region region = (Region) parent.getSelectedItem();
                updateCountries(region.identifier);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spCountry = findViewById(R.id.sp_country);
        spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Country country = (Country) parent.getSelectedItem();
                updateCities(country.identifier);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spCity = findViewById(R.id.sp_city);
        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                City city = (City) parent.getSelectedItem();
                currentCityId = city.identifier;

                updateChart();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        slFrom = findViewById(R.id.sl_from);
        slFrom.setLabelFormatter( value -> {
            LocalDate date = getDateFromValue((int) value);
            return date.format(dateFormatter);
        });
        slFrom.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(Slider slider) {}

            @Override
            public void onStopTrackingTouch(Slider slider) {
                from = getDateFromValue((int) slider.getValue());
                updateChart();
            }
        });

        slTo = findViewById(R.id.sl_to);
        slTo.setLabelFormatter( value -> {
            LocalDate date = getDateFromValue((int) value);
            return date.format(dateFormatter);
        });
        slTo.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(Slider slider) {}

            @Override
            public void onStopTrackingTouch(Slider slider) {
                to = getDateFromValue((int) slider.getValue());
                updateChart();
            }
        });

        tvDate = findViewById(R.id.tv_date);

        updateRegions();
    }




    private void updateRegions() {
        api.getRegions().enqueue(new Callback<List<Region>>() {
            @Override
            public void onResponse(Call<List<Region>> call, Response<List<Region>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayAdapter<Region> adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_spinner_item, response.body());
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spRegion.setAdapter(adapter);
                    spRegion.setSelection(0);
                }
            }

            @Override
            public void onFailure(Call<List<Region>> call, Throwable t) { }
        });
    }

    private void updateCountries(Integer regionId) {
        api.getCountries(regionId).enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayAdapter<Country> adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_spinner_item, response.body());
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spCountry.setAdapter(adapter);
                    spCountry.setSelection(0);
                }
            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) { }
        });
    }

    private void updateCities(Integer countryId) {
        api.getCities(countryId).enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, Response<List<City>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayAdapter<City> adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_spinner_item, response.body());
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spCity.setAdapter(adapter);
                    spCity.setSelection(0);
                }
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable t) { }
        });
    }


    private void updateChart() {
        tvDate.setText("Диапазон дат: " + from.format(dateFormatter) + " - " + to.format(dateFormatter));

        api.getDailyTemperature(currentCityId, from.format(tsFormatter), to.format(tsFormatter)).enqueue(new Callback<List<Temperature>>() {
            @Override
            public void onResponse(Call<List<Temperature>> call, Response<List<Temperature>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    System.out.println(response.body());
                    chartView.update(response.body(), from, to);
                }
            }

            @Override
            public void onFailure(Call<List<Temperature>> call, Throwable t) { }
        });

    }

    private LocalDate getDateFromValue(int value) {
        int dif = 2000 - value;

        return LocalDate.now().minusDays(dif);
    }
}
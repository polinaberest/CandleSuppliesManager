package com.example.candlesuppliesmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;

import java.util.List;

public class MakeRequestActivity extends AppCompatActivity {


    private static final String API_KEY = "14fcb20eeab9cf6e88c304d9002efc58";

    private Spinner citiesSpinner, warehousesSpinner;
    private SearchView citiesSearch;

    private NovaPoshtaApi novaPoshtaApi;

    private String userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_request);

        userPhone = getIntent().getExtras().get("phone").toString();
        citiesSpinner = (Spinner) findViewById(R.id.cities_spinner);
        citiesSearch = findViewById(R.id.cities_search);
        warehousesSpinner = (Spinner) findViewById(R.id.branches_spinner);

        novaPoshtaApi = new NovaPoshtaApi(this, API_KEY);
        novaPoshtaApi.getCities(citiesSpinner, warehousesSpinner, citiesSearch);













        citiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedCity = citiesSpinner.getSelectedItem().toString();
                novaPoshtaApi.updateWarehouse(warehousesSpinner, selectedCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
//        new OnCitySelectedListener(citiesSpinner, warehousesSpinner, )
//        warehousesSpinner.setAdapter();

    }
}
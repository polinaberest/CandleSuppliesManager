package com.example.candlesuppliesmanager;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.candlesuppliesmanager.City;
import com.example.candlesuppliesmanager.Warehouse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NovaPoshtaApi {

    private static final String TAG = "NovaPoshtaApi";
    private static final String API_URL = "https://api.novaposhta.ua/v2.0/json/";

    private final String apiKey;
    private final RequestQueue requestQueue;

    private final Context mContext; // Добавляем поле mContext

    public NovaPoshtaApi(Context context, String apiKey) { // Добавляем параметр Context
        this.apiKey = apiKey;
        this.mContext = context; // Сохраняем значение контекста
        this.requestQueue = Volley.newRequestQueue(mContext); // Используем mContext для создания requestQueue
    }

    public void getCities(final Spinner citiesSpinner, final Spinner warehousesSpinner, final SearchView searchView) {
        String url = API_URL;// + "?apiKey=" + apiKey + "&modelName=Address&calledMethod=getCities";

        Map<String, Object> params = new HashMap<>();
        params.put("apiKey", apiKey);
        params.put("modelName", "Address");
        params.put("calledMethod", "getCities");
        params.put("methodProperties", new JSONObject());

        JSONObject requestBody = new JSONObject(params);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray citiesArray = response.getJSONArray("data");
                            List<String> cityNames = new ArrayList<>();
                            for (int i = 0; i < citiesArray.length(); i++) {
                                JSONObject city = citiesArray.getJSONObject(i);
                                String cityName = city.getString("Description");
                                cityNames.add(cityName);
                            }
                            // Передаем список городов в адаптер
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, cityNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            citiesSpinner.setAdapter(adapter);


                            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    return false;
                                }

                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    adapter.getFilter().filter(newText);
                                    return false;
                                }
                            });

                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing cities response", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error getting cities", error);
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    public void updateWarehouse(final Spinner warehousesSpinner, final String selectedCity) {
        String url = API_URL; /* + "?apiKey=" + apiKey + "&modelName=AddressGeneral&calledMethod=getWarehouses&methodProperties="
                    + "{\"CityRef\":\"" + selectedCity.getRef() + "\"}";*/

        Map<String, Object> params = new HashMap<>();
        params.put("apiKey", apiKey);
        params.put("modelName", "Address");
        params.put("calledMethod", "getWarehouses");

        Map<String, String> methodProps = new HashMap<>();
        methodProps.put("CityName", selectedCity);
        params.put("methodProperties", methodProps);

        JSONObject requestBody = new JSONObject(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray warehousesArray = response.getJSONArray("data");
                            List<Warehouse> warehousesList = new ArrayList<>();

                            for (int i = 0; i < warehousesArray.length(); i++) {
                                JSONObject warehouseObject = warehousesArray.getJSONObject(i);
                                Warehouse warehouse = new Warehouse();
                                warehouse.setDescription(warehouseObject.getString("Description"));
                                warehouse.setRef(warehouseObject.getString("Ref"));
                                warehousesList.add(warehouse);
                            }

                            ArrayAdapter<Warehouse> warehousesAdapter = new ArrayAdapter<>(warehousesSpinner.getContext(),
                                    android.R.layout.simple_spinner_item, warehousesList);
                            warehousesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            warehousesSpinner.setAdapter(warehousesAdapter);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing warehouses response", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error getting warehouses", error);
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private static class OnCitySelectedListener implements AdapterView.OnItemSelectedListener {

        private final Spinner citiesSpinner;
        private final Spinner warehousesSpinner;
        private final String apiKey;
        private final RequestQueue requestQueue;

        public OnCitySelectedListener(Spinner citiesSpinner, Spinner warehousesSpinner, String apiKey, RequestQueue requestQueue) {
            this.citiesSpinner = citiesSpinner;
            this.warehousesSpinner = warehousesSpinner;
            this.apiKey = apiKey;
            this.requestQueue = requestQueue;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            City selectedCity = (City) parent.getSelectedItem();

            String url = API_URL; /* + "?apiKey=" + apiKey + "&modelName=AddressGeneral&calledMethod=getWarehouses&methodProperties="
                    + "{\"CityRef\":\"" + selectedCity.getRef() + "\"}";*/

            Map<String, Object> params = new HashMap<>();
            params.put("apiKey", apiKey);
            params.put("modelName", "Address");
            params.put("calledMethod", "getWarehouses");

            params.put("methodProperties", new JSONObject());

            JSONObject requestBody = new JSONObject(params);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST, url, requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray warehousesArray = response.getJSONObject("data").getJSONArray("warehouses");
                                List<Warehouse> warehousesList = new ArrayList<>();

                                for (int i = 0; i < warehousesArray.length(); i++) {
                                    JSONObject warehouseObject = warehousesArray.getJSONObject(i);
                                    Warehouse warehouse = new Warehouse();
                                    warehouse.setDescription(warehouseObject.getString("Description"));
                                    warehouse.setRef(warehouseObject.getString("Ref"));
                                    warehousesList.add(warehouse);
                                }

                                ArrayAdapter<Warehouse> warehousesAdapter = new ArrayAdapter<>(warehousesSpinner.getContext(),
                                        android.R.layout.simple_spinner_item, warehousesList);
                                warehousesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                warehousesSpinner.setAdapter(warehousesAdapter);
                            } catch (JSONException e) {
                                Log.e(TAG, "Error parsing warehouses response", e);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Error getting warehouses", error);
                        }
                    });

            requestQueue.add(jsonObjectRequest);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing
        }
    }
}


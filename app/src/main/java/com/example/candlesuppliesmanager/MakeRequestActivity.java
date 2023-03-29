package com.example.candlesuppliesmanager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MakeRequestActivity extends AppCompatActivity {


    private static final String API_KEY = "14fcb20eeab9cf6e88c304d9002efc58";
    private final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private DatabaseReference requestsReference;
    private Spinner citiesSpinner, warehousesSpinner;

    private RadioGroup radioGroupRoles;
    private EditText recieverPhoneET, recieverNameET, smallET, midET, bigET;
    private SearchView citiesSearch;
    private NovaPoshtaApi novaPoshtaApi;
    private String userPhone, recieverPhone, recieverName, selectedCity, selectedWarehouse, formattedDateTime, role, key;
    private boolean isSeen = false, isApproved = false;
    private int amountBig = 0, amountMid = 0, amountSmall = 0, total = 0;
    private Button makeRequestBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_request);

        initFieldsByID();

        initNovaPoshtaSpinners();

        radioGroupRoles.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                // Get the selected RadioButton's text
                RadioButton selectedRadioButton = findViewById(checkedId);
                role = selectedRadioButton.getText().toString();
                // Do something with the selected text
                // System.out.println(role);
            }
        });

        makeRequestBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillFieldsByValue();
                if(checkIfRequestCompleted()){
                    Toast.makeText(view.getContext(), "ОК!", Toast.LENGTH_SHORT).show();

                    MakeRequest();
                }
                else{
                    Toast.makeText(view.getContext(), "Неповну заявку не прийнято до розгляду!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void MakeRequest() {

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("ddMMyyyy");
        String saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HHmmss");
        String saveCurrentTime = currentTime.format(calendar.getTime());

        key = userPhone + " " + saveCurrentTime + " " + saveCurrentDate;

        HashMap<String, Object> requestMap = new HashMap<>();

        requestMap.put("requestID", key);
        requestMap.put("date", formattedDateTime);
        requestMap.put("userPhone", userPhone);
        requestMap.put("recieverPhone", recieverPhone);
        requestMap.put("recieverName", recieverName);
        requestMap.put("city", selectedCity);
        requestMap.put("warehouse", selectedWarehouse);
        requestMap.put("amountBig", amountBig);
        requestMap.put("amountMiddle", amountMid);
        requestMap.put("amountSmall", amountSmall);
        requestMap.put("isSeen", isSeen);
        requestMap.put("isApproved", isApproved);
        requestMap.put("role", role);

        requestsReference.child(key).updateChildren(requestMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    //тут повідомлення користувачеві з підрахунком ваги
                    Toast.makeText(MakeRequestActivity.this, "ЗАЯВКУ ДОДАНО!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MakeRequestActivity.this, "Щось не за планом", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkIfRequestCompleted() {
        //по свічках
        if(total>0
                &&recieverName!=null && recieverName.length()>10
        &&recieverPhone!=null && recieverPhone.length()==10
        &&selectedCity!=null && selectedCity.length()>2
        &&selectedWarehouse!=null && selectedWarehouse.length()>2
        &&role!=null){
            return true;
        }

        return false;
    }

    private void fillFieldsByValue() {

        //дата та час
        ZoneId kyivZone = ZoneId.of("Europe/Kiev");

        // Get the current date and time in Kyiv time zone
        LocalDateTime kyivDateTime = LocalDateTime.now(kyivZone);

        formattedDateTime = kyivDateTime.format(dtFormatter);

        //телефон отримувача
        recieverPhone = recieverPhoneET.getText().toString();

        //ім'я отримувача
        recieverName = recieverNameET.getText().toString();

        //кількість свічок
        try{
            amountSmall = Integer.parseInt(smallET.getText().toString());
            amountMid = Integer.parseInt(midET.getText().toString());
            amountBig = Integer.parseInt(bigET.getText().toString());
            total = amountBig+amountSmall+amountMid;
        }
        catch (Exception e){
            Toast.makeText(this, "Зазначте необхідну кількість свічок", Toast.LENGTH_SHORT).show();
            total = 0;
        }


        requestsReference = FirebaseDatabase.getInstance().getReference().child("Requests");
    }

    private void initNovaPoshtaSpinners() {
        novaPoshtaApi = new NovaPoshtaApi(this, API_KEY);
        novaPoshtaApi.getCities(citiesSpinner, warehousesSpinner, citiesSearch);

        citiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCity = citiesSpinner.getSelectedItem().toString();
                novaPoshtaApi.updateWarehouse(warehousesSpinner, selectedCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedCity = "";
            }
        });

        warehousesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedWarehouse = warehousesSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedWarehouse = "";
            }
        });

    }

    private void initFieldsByID() {
        userPhone = getIntent().getExtras().get("phone").toString();
        citiesSpinner = (Spinner) findViewById(R.id.cities_spinner);
        citiesSearch = findViewById(R.id.cities_search);
        warehousesSpinner = (Spinner) findViewById(R.id.branches_spinner);
        recieverPhoneET = (EditText) findViewById(R.id.reciever_phone_input);
        recieverNameET = (EditText) findViewById(R.id.reciever_name);

        smallET = (EditText)findViewById(R.id.amount_small_candles);
        midET = (EditText)findViewById(R.id.amount_mid_candles);
        bigET = (EditText)findViewById(R.id.amount_big_candles);

        radioGroupRoles = (RadioGroup) findViewById(R.id.role_group);

        makeRequestBTN = (Button) findViewById(R.id.make_request_btn);
    }




}
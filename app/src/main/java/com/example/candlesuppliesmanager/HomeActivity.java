package com.example.candlesuppliesmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {

    private String userPhone;
    private ImageView logoutBtn;
    private ImageView plusRequestBtn;

    private DatabaseReference databaseRef;

    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userPhone = getIntent().getExtras().get("phone").toString();
        logoutBtn = (ImageView) findViewById(R.id.logout_btn);
        plusRequestBtn = (ImageView) findViewById(R.id.plus_request_btn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Paper.book().destroy();
                Intent logoutInt = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(logoutInt);
            }
        });

        plusRequestBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Intent makeRequest = new Intent(HomeActivity.this, MakeRequestActivity.class);
                makeRequest.putExtra("phone", userPhone);
                startActivity(makeRequest);
            }
        });
    }
}
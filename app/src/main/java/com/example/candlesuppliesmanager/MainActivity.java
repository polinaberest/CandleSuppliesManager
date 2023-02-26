package com.example.candlesuppliesmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button joinButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinButton = (Button) findViewById(R.id.main_join_btn);
        loginButton = (Button) findViewById(R.id.main_login_btn);
    }
}
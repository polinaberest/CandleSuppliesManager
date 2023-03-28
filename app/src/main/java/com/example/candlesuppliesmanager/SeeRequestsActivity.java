package com.example.candlesuppliesmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import io.paperdb.Paper;

public class SeeRequestsActivity extends AppCompatActivity {

    private Button logoutAdminBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_requests);

        logoutAdminBtn = (Button) findViewById(R.id.logout_admin_btn);

        logoutAdminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Paper.book().destroy();
                Intent logoutInt = new Intent(SeeRequestsActivity.this, MainActivity.class);
                startActivity(logoutInt);
            }
        });
    }
}
package com.example.candlesuppliesmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.candlesuppliesmanager.Model.User;
import com.example.candlesuppliesmanager.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button joinButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinButton = (Button) findViewById(R.id.main_join_btn);
        loginButton = (Button) findViewById(R.id.main_login_btn);

        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent joinIntent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(joinIntent);
            }
        });

        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPassKey = Paper.book().read(Prevalent.UserPassKey);

        if(UserPhoneKey != "" && UserPassKey != "")
        {
            if(!TextUtils.isEmpty(UserPhoneKey)&&!TextUtils.isEmpty(UserPassKey))
            {
                ValidateUser(UserPhoneKey, UserPassKey);
            }
        }
    }

    private void ValidateUser(String userPhone, String userPass) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Users").child(userPhone).exists())
                {
                    User userData = snapshot.child("Users").child(userPhone).getValue(User.class);

                    if(userData.getPhone().equals(userPhone)&&userData.getPassword().equals(userPass))
                    {
                        Toast.makeText(MainActivity.this, "Авторизація успішна!", Toast.LENGTH_SHORT).show();
                        Intent homeInt = new Intent(MainActivity.this, HomeActivity.class);
                        homeInt.putExtra("phone", userPhone);
                        startActivity(homeInt);
                    }
                    else{

                    }
                }
                else if(snapshot.child("Admins").child(userPhone).exists())
                {
                    User userData = snapshot.child("Admins").child(userPhone).getValue(User.class);

                    if(userData.getPhone().equals(userPhone)&&userData.getPassword().equals(userPass))
                    {
                        Toast.makeText(MainActivity.this, "Ви ввійшли як адміністратор", Toast.LENGTH_SHORT).show();
                        Intent homeInt = new Intent(MainActivity.this, SeeRequestsActivity.class);
                        startActivity(homeInt);
                    }
                    else{

                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Акаунту з цим номером не існує.", Toast.LENGTH_SHORT).show();

                    Intent regIntent = new Intent(MainActivity.this, RegistrationActivity.class);
                    startActivity(regIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
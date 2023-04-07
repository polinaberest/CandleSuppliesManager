package com.example.candlesuppliesmanager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.biometric.BiometricManager;
//import android.hardware.biometrics.BiometricManager;
import android.os.Build;
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

import java.util.concurrent.Executor;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private BiometricManager biometricManager;
    private Button joinButton, loginButton;
    //private boolean canLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinButton = (Button) findViewById(R.id.main_join_btn);
        loginButton = (Button) findViewById(R.id.main_login_btn);

        Paper.init(this);

       // BiometricManager biometricManager = BiometricManager.from(this);

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


    private void ValidateBiometrics(String userPhone)
    {
        //boolean canLogin = false;
/*
        AlertDialog.Builder alertD = new AlertDialog.Builder(MainActivity.this);

        alertD.setTitle("Авторизація");
        alertD.setMessage("Відскануйте відбиток, щоб продовжити");
*/
        biometricManager = BiometricManager.from(this);

        switch (biometricManager.canAuthenticate()){
            case BiometricManager.BIOMETRIC_SUCCESS:
                break;
            case  BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(MainActivity.this, "Немає сенсора!", Toast.LENGTH_SHORT).show();
                break;
            case  BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(MainActivity.this, "BIOMETRIC_ERROR_HW_UNAVAILABLE!", Toast.LENGTH_SHORT).show();
                break;
            case  BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(MainActivity.this, "BIOMETRIC_ERROR_NONE_ENROLLED!", Toast.LENGTH_SHORT).show();
                break;
        }

        Executor executor = ContextCompat.getMainExecutor(this);
        androidx.biometric.BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(MainActivity.this, "Авторизація успішна!", Toast.LENGTH_SHORT).show();
                Intent homeInt = new Intent(MainActivity.this, HomeActivity.class);
                homeInt.putExtra("phone", userPhone);
                startActivity(homeInt);
            }

            @Override
            public void onAuthenticationFailed() {

                super.onAuthenticationFailed();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Авторизація")
                .setDescription("Відскануйте відбиток, щоб продовжити")
                .setNegativeButtonText("Скасувати")
                .build();

        biometricPrompt.authenticate(promptInfo);
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
                        //ТУТ ПЕРЕВІРКА ВІДБИТКА ПАЛЬЦЯ!
                        ValidateBiometrics(userPhone);
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
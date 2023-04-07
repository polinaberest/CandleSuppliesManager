package com.example.candlesuppliesmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    private Button regButton;
    private EditText regPhoneInput, regPasswordInput;

   //private boolean canUseBiometrics = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        regButton = (Button) findViewById(R.id.register_btn);
        regPhoneInput = (EditText) findViewById(R.id.reg_phone_input);
        regPasswordInput = (EditText) findViewById(R.id.reg_password_input);

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {
        String userPass = regPasswordInput.getText().toString();
        String userPhone = regPhoneInput.getText().toString();

        if(TextUtils.isEmpty(userPhone)||userPhone.length()!=10||!(StringUtils.isNumeric(userPhone)))
        {
            Toast.makeText(this, "Введений номер не є дійсним. Введіть номер у форматі 0XX XXX XX XX.", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userPass)||userPass.length()<8)
        {
            Toast.makeText(this, "Пароль ненадійний. Пароль має складатися мінімум з 8 символів.", Toast.LENGTH_SHORT).show();
        }
        else{
            ValidatePhone(userPhone, userPass);
        }
    }

    private void ValidatePhone(String phone, String pass) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!(snapshot.child("Users").child(phone).exists()))
                {
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("phone", phone);
                    userDataMap.put("password", pass);

                    rootRef.child("Users").child(phone).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(RegistrationActivity.this, "Ви успішно зареєструвалися!", Toast.LENGTH_SHORT);

                                        Intent loginIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                        //loginIntent.putExtra("useBiometrics", canUseBiometrics);
                                        startActivity(loginIntent);
                                    }
                                    else {
                                        Toast.makeText(RegistrationActivity.this, "Виникла помилка!", Toast.LENGTH_SHORT);
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(RegistrationActivity.this, "Користувач із таким номером телефону вже зареєстрований у додатку.", Toast.LENGTH_SHORT).show();

                    Intent loginIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
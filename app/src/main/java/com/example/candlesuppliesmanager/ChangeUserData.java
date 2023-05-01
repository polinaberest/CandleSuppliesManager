package com.example.candlesuppliesmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import io.paperdb.Paper;

public class ChangeUserData extends AppCompatActivity {

    private String phone, newPass;
    private ImageView backBtn;
    private LinearLayout changePassBtn, changePhoneBtn;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_data);

        phone = getIntent().getExtras().get("phone").toString();
        backBtn = (ImageView) findViewById(R.id.user_back_btn);
        changePassBtn = (LinearLayout) findViewById(R.id.change_pass_btn);
        passwordEditText = (EditText) findViewById(R.id.change_password);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeInt = new Intent(ChangeUserData.this, HomeActivity.class);
                homeInt.putExtra("phone", phone);
                if(!passwordEditText.getText().toString().isEmpty())
                    Toast.makeText(ChangeUserData.this, "Дані не буде збережено!", Toast.LENGTH_SHORT).show();

                startActivity(homeInt);
            }
        });

        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePass();
            }
        });
    }

    private void ChangePass() {
        newPass = passwordEditText.getText().toString();

        if(TextUtils.isEmpty(newPass)||newPass.length()<8)
        {
            Toast.makeText(this, "Пароль ненадійний. Пароль має складатися мінімум з 8 символів.", Toast.LENGTH_SHORT).show();
            passwordEditText.setText("");
        }
        else{
            SaveChanges(newPass);
        }
    }

    private void SaveChanges(String pass) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(phone);

        // Обновляем поле password
        ref.child("password").setValue(pass);

        passwordEditText.setText("");

        //затираем значения в paper
        Paper.book().destroy();

        Toast.makeText(this, "Пароль змінено! При наступному вході Вам треба ввести пароль.", Toast.LENGTH_SHORT).show();

    }
}
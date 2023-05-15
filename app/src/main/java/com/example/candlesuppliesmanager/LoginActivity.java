package com.example.candlesuppliesmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.candlesuppliesmanager.Model.User;
import com.example.candlesuppliesmanager.Prevalent.Prevalent;
import com.example.candlesuppliesmanager.Statics.CandleInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText loginPhoneInput, loginPasswordInput;
    private CheckBox chBoxRememberMe;
    private boolean canUseBiometrics;
    private TextView adminLink, clientLink;
    private  String parentDBName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.login_btn);
        loginPhoneInput = (EditText) findViewById(R.id.login_phone_input);
        loginPasswordInput = (EditText) findViewById(R.id.login_password_input);
        chBoxRememberMe = (CheckBox) findViewById(R.id.login_checkbox);
        adminLink = (TextView) findViewById(R.id.admin_panel);
        clientLink = (TextView) findViewById(R.id.client_panel);

        Paper.init(this);

        //
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My notification", "My notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        //

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adminLink.setVisibility(View.INVISIBLE);
                clientLink.setVisibility(View.VISIBLE);
                loginButton.setText("Увійти як адміністратор");
                parentDBName = "Admins";


            }
        });

        clientLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clientLink.setVisibility(View.INVISIBLE);
                adminLink.setVisibility(View.VISIBLE);
                loginButton.setText("Увійти в акаунт");
                parentDBName = "Users";
            }
        });
    }


    private void loginUser()
    {
        String userPass = loginPasswordInput.getText().toString();
        String userPhone = loginPhoneInput.getText().toString();

        if(TextUtils.isEmpty(userPhone)||userPhone.length()!=10||!(StringUtils.isNumeric(userPhone)))
        {
            Toast.makeText(this, "Введіть номер у форматі 0XX XXX XX XX.", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userPass))
        {
            Toast.makeText(this, "Введіть пароль.", Toast.LENGTH_SHORT).show();
        }
        else{
            ValidateUser(userPhone, userPass);
        }
    }

    private void ValidateUser(String userPhone, String userPass) {

        if(chBoxRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.UserPhoneKey, userPhone);
            Paper.book().write(Prevalent.UserPassKey, userPass);
        }


        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(parentDBName).child(userPhone).exists())
                {
                    User userData = snapshot.child(parentDBName).child(userPhone).getValue(User.class);

                    if(userData.getPhone().equals(userPhone)&&userData.getPassword().equals(userPass))
                    {
                        if(parentDBName.equals("Users"))
                        {
                            Toast.makeText(LoginActivity.this, "Авторизація успішна!", Toast.LENGTH_SHORT).show();
                            Intent homeInt = new Intent(LoginActivity.this, HomeActivity.class);
                            homeInt.putExtra("phone", userPhone);
                            //заявки переглянуто
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(LoginActivity.this, "My notification");
                            builder.setContentTitle("Окопні свічки");
                            builder.setContentText("Наявні нові розглянуті заявки");
                            builder.setSmallIcon(R.drawable.flame);
                            builder.setAutoCancel(true);

                            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(LoginActivity.this);
                            managerCompat.notify(1, builder.build());

                            startActivity(homeInt);
                        }
                        else if (parentDBName.equals("Admins")) {
                            Toast.makeText(LoginActivity.this, "Авторизація адміністратора успішна!", Toast.LENGTH_SHORT).show();
                            Intent admInt = new Intent(LoginActivity.this, SeeRequestsActivity.class);
                            //admInt.putExtra("phone", userPhone);
                            //наявні нові заявки
                            startActivity(admInt);
                        }
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Неправильний пароль!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(LoginActivity.this, "Акаунту з цим номером не існує.", Toast.LENGTH_SHORT).show();

                    Intent regIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                    startActivity(regIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
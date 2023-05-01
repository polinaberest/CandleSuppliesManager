package com.example.candlesuppliesmanager;

import androidx.annotation.RequiresApi;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {

    private String userPhone;
    private ImageView logoutBtn, plusRequestBtn, changeData;

    private TextView noRequestsText;
    List<Order> orderList;
    private DatabaseReference databaseRef;

    private LinearLayout layout;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userPhone = getIntent().getExtras().get("phone").toString();
        logoutBtn = (ImageView) findViewById(R.id.logout_btn);
        changeData = (ImageView) findViewById(R.id.change_data_btn);
        plusRequestBtn = (ImageView) findViewById(R.id.plus_request_btn);
        layout = findViewById(R.id.see_requests_layout);
        noRequestsText = (TextView) findViewById(R.id.no_requests_made_textView);

        ShowUserRequests();
        //InsertListItemstoLayout();
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Paper.book().destroy();
                Intent logoutInt = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(logoutInt);
            }
        });

        changeData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeInt = new Intent(HomeActivity.this, ChangeUserData.class);
                changeInt.putExtra("phone", userPhone);
                startActivity(changeInt);
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

    private void ShowUserRequests()
    {
       databaseRef = FirebaseDatabase.getInstance().getReference("Requests");
       query = databaseRef.orderByChild("userPhone").equalTo(userPhone);
        orderList = new ArrayList<>();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String recieverName = ds.child("recieverName").getValue(String.class);
                    String recieverPhone = ds.child("recieverPhone").getValue(String.class);
                    String requestID = ds.child("requestID").getValue(String.class);
                    String role = ds.child("role").getValue(String.class);
                    String city = ds.child("city").getValue(String.class);
                    String warehouse = ds.child("warehouse").getValue(String.class);
                    String dateTime = ds.child("date").getValue(String.class);
                    int amountBig = ds.child("amountBig").getValue(Integer.class);
                    int amountMid = ds.child("amountMiddle").getValue(Integer.class);
                    int amountSmall = ds.child("amountSmall").getValue(Integer.class);
                    boolean isSeen = ds.child("isSeen").getValue(Boolean.class);
                    boolean isApproved = ds.child("isApproved").getValue(Boolean.class);
                   // System.out.println(isSeen);


                    Order order = new Order(requestID, userPhone, recieverName, recieverPhone, role, city, warehouse, dateTime, amountBig, amountMid, amountSmall, isSeen, isApproved);
                    orderList.add(order);
                }

                InsertListItemstoLayout();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // обработка ошибок
            }
        });


    }

    private void InsertListItemstoLayout() {
        if(orderList.size() == 0){
            noRequestsText.setVisibility(View.VISIBLE);
            return;
        }

        Collections.reverse(orderList);

        for (Order order : orderList) {
            // создаем новый TextView и устанавливаем текст
            TextView textView = new TextView(this);

            String text = order.toString();
            textView.setText(text);

            // устанавливаем идентификатор для TextView, используя id объекта
            String id = order.getRequestID(); // предполагается, что у объекта есть метод getId()
            textView.setTag(id);

            int color = R.color.ivory;
            if(order.isSeen()){
                color = order.isApproved() ? R.color.green : R.color.red;
            }

            textView.setBackgroundResource(color);
            textView.setPadding(20, 10, 20, 10);

            int fontResId = R.font.comfortaa;

            // Загружаем шрифт из ресурсов
            Typeface font = ResourcesCompat.getFont(this, fontResId);

            // Устанавливаем шрифт и цвет в TextView
            textView.setTypeface(font);

            // создаем объект LayoutParams и устанавливаем отступы
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(20, 20, 20, 0); // устанавливаем отступы в px (левый, верхний, правый, нижний)

            // устанавливаем параметры макета для TextView
            textView.setLayoutParams(params);

            textView.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    int radius = 15;
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
                }
            });
            textView.setClipToOutline(true);

            // добавляем TextView в LinearLayout
            layout.addView(textView);
        }
    }
}
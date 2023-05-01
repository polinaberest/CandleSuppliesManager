package com.example.candlesuppliesmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Outline;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
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
import java.util.List;


public class SeeApprovedActivity extends AppCompatActivity {

    private ImageView backBtn;
    private TextView noRequestsText;
    private DatabaseReference databaseRef;
    private LinearLayout requestsLayout;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_approved);

        backBtn = (ImageView) findViewById(R.id.back_btn);
        requestsLayout = findViewById(R.id.see_approved_layout);
        noRequestsText = (TextView) findViewById(R.id.no_approved_textView);

        ShowApprovedRequests();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent seeReqInt = new Intent(SeeApprovedActivity.this, SeeRequestsActivity.class);
                startActivity(seeReqInt);
            }
        });
    }

    private void ShowApprovedRequests() {
        databaseRef = FirebaseDatabase.getInstance().getReference("Requests");
        query = databaseRef.orderByChild("isApproved").equalTo(true);
        List<Order> orderList = new ArrayList<>();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String recieverName = ds.child("recieverName").getValue(String.class);
                    String recieverPhone = ds.child("recieverPhone").getValue(String.class);
                    String userPhone = ds.child("userPhone").getValue(String.class);
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

                clearLayout();
                InsertListItemstoLayout(orderList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // обработка ошибок
            }
        });
    }

    private void InsertListItemstoLayout(List<Order> orderList) {
        if (orderList.size() == 0) {
            noRequestsText.setVisibility(View.VISIBLE);
            return;
        }

        noRequestsText.setVisibility(View.GONE);

        //Collections.reverse(orderList);

        for (Order order : orderList) {
            // создаем новый TextView и устанавливаем текст
            TextView textView = new TextView(this);

            String text = order.toStringAdmins(); //
            textView.setText(text);

            // устанавливаем идентификатор для TextView, используя id объекта
            String id = order.getRequestID();
            textView.setTag(id);

            int color = R.color.green;
            if (order.getRole().trim().equals("Військовослужбовець")) {
                color = R.color.light_orange;
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

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Получаем id заказа
                    String reqId = textView.getTag().toString();
                    String title = "Перегляд заявки";
                    String description = order.toStringFull();

                    // Отображаем диалоговое окно с информацией о заказе
                    AlertDialog.Builder builder = new AlertDialog.Builder(SeeApprovedActivity.this);
                    builder.setTitle(title);
                    builder.setMessage(description);

                    builder.setNeutralButton("Закрити", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Обработка нажатия кнопки "Закрыть"
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();

                    dialog.show();
                }
            });

            // добавляем TextView в LinearLayout
            requestsLayout.addView(textView);
        }

    }

    private void clearLayout(){
        requestsLayout.removeAllViews();
    }
}
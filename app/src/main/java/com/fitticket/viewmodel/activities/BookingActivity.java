package com.fitticket.viewmodel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fitticket.R;

/**
 * Created by NiravShrimali on 6/2/2016.
 */
public class BookingActivity extends AppCompatActivity {


    TextView activityName;
    TextView activityDate;
    TextView activityTime;
    TextView activityPrice;
    TextView activityCancelDeadline;
    TextView activityPlace;
    TextView gymName;

    String stractivityName;
    String stractivityDate;
    String strctivityTime;
    String stractivityPrice;
    String stractivityCancelDeadline;
    String stractivityPlace;
    String strgymName;
    String strpaytm;
    Button buttonbook;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_for_non_member);


        Intent intent = getIntent();
        stractivityName = intent.getStringExtra("activityName");
        stractivityDate = intent.getStringExtra("activityDate");
        strctivityTime = intent.getStringExtra("activityTime");
        stractivityPrice = intent.getStringExtra("activityPrice");
        stractivityPlace = intent.getStringExtra("activityPlace");
        strgymName = intent.getStringExtra("gymName");
        strpaytm = intent.getStringExtra("payTmUrl");

        activityName = (TextView) findViewById(R.id.activity_name);
        activityDate = (TextView) findViewById(R.id.activity_date);
        activityTime = (TextView) findViewById(R.id.activity_time);
        activityPrice = (TextView) findViewById(R.id.activity_price);
        activityPlace = (TextView) findViewById(R.id.activity_place);
        gymName = (TextView) findViewById(R.id.gym_name);
        buttonbook = (Button) findViewById(R.id.buttonbook);
        activityCancelDeadline = (TextView) findViewById(R.id.activity_cancel_deadline);


        activityName.setText(stractivityName);
        activityDate.setText(stractivityDate);
        activityTime.setText(strctivityTime);
        activityPrice.setText(stractivityPrice);
        activityPlace.setText(stractivityPlace);
        gymName.setText(strgymName);
        activityCancelDeadline.setText("");


        buttonbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent payTmIntent = new Intent(BookingActivity.this, PayTmWebViewActivity.class);
                payTmIntent.putExtra("URL TO LOAD", strpaytm);
                startActivity(payTmIntent);
            }
        });


    }
}

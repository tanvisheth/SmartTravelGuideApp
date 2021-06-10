package com.example.smarttravelguide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashBoardActivity extends AppCompatActivity {

    private CardView cd1, cd2, cd3, cd4, cd5, cd6, cd7, cd8;
    private SharedPreferences file;
    private FloatingActionButton _fab;
    private Intent intent = new Intent();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        cd1 = findViewById(R.id.UI_cardview1);
        cd2 = findViewById(R.id.UI_cardview2);
        cd3 = findViewById(R.id.UI_cardview3);
        cd4 = findViewById(R.id.UI_cardview4);
        cd5 = findViewById(R.id.UI_cardview5);
        cd6 = findViewById(R.id.UI_cardview6);
        cd7 = findViewById(R.id.UI_cardview7);
        cd8 = findViewById(R.id.UI_cardview8);
        _fab = (FloatingActionButton) findViewById(R.id._fab);
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);

        if (file.getString("emailid", "").equals("admin@gmail.com")) {
            _fab.setVisibility(View.VISIBLE);
        } else {
            _fab.setVisibility(View.GONE);
        }

        _fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(getApplicationContext(), AddSpotActivity.class);
                startActivity(intent);
            }
        });
        cd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BeachActivity.class);
                startActivity(intent);
            }
        });

        cd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HillActivity.class);
                startActivity(intent);
            }
        });

        cd3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WaterfallActivity.class);
                startActivity(intent);
            }
        });

        cd4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MonumentActivity.class);
                startActivity(intent);
            }
        });

        cd5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReligiousActivity.class);
                startActivity(intent);
            }
        });

        cd6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), IslandActivity.class);
                startActivity(intent);
            }
        });

        cd7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WildLifeActivity.class);
                startActivity(intent);
            }
        });

        cd8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EducationalActivity.class);
                startActivity(intent);
            }
        });

    }
}
package com.food_donation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomePage extends AppCompatActivity {
    Button ibdonor, ibvolunteer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ibdonor = findViewById(R.id.btnuser);
        ibvolunteer = findViewById(R.id.btnvolunteer);

        ibdonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomePage.this, LoginDonorActivity.class);
                i.putExtra("role", "donor");
                startActivity(i);
            }
        });
        ibvolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomePage.this, LoginActivity.class);
                i.putExtra("role", "volunteer");
                startActivity(i);
            }
        });
    }
}
package com.example.threeactivityapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    TextView ucapan;
    Button tombolKeTiga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        ucapan = findViewById(R.id.txt_ucapan);
        tombolKeTiga = findViewById(R.id.btn_ke_third);

        String nama = getIntent().getStringExtra("nama_user");
        ucapan.setText("Selamat datang, " + nama + "!");

        tombolKeTiga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SecondActivity.this, ThirdActivity.class));
            }
        });
    }
}

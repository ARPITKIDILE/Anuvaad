package com.example.anuvaad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView IP;
    Button submit;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IP = findViewById(R.id.ip_address);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String middle = IP.getText()+"";
                url = "http://"+middle+":5000";
                Intent i = new Intent(MainActivity.this,TesserActivity.class);
                i.putExtra("address",url);
                startActivity(i);
            }
        });

    }
}
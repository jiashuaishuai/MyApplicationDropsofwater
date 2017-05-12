package com.example.jiashuai.myapplicationdropsofwater;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private DropsOfWater dropsOfWater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dropsOfWater = (DropsOfWater) findViewById(R.id.myview);
        findViewById(R.id.but).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropsOfWater.start();
            }
        });
        findViewById(R.id.butend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropsOfWater.end();
            }
        });
    }
}

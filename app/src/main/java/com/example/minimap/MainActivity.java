package com.example.minimap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ThemedSpinnerAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button bRequester , bHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bRequester = (Button) findViewById(R.id.Requester);

        bRequester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , RequesterLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        bHelper = (Button) findViewById(R.id.Helper);

        bHelper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , HelperLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}
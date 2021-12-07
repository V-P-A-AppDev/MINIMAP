package com.v_p_a_appdev.minimap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button customerButton,workerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        workerButton=(Button) findViewById(R.id.worker);
        customerButton=(Button) findViewById(R.id.customer);
        workerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,WorkerLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        customerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CustomerLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}




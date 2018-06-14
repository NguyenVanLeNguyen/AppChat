package com.example.nguyen.appchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Start_Activity extends AppCompatActivity {
    private Button mRegBtn;
    private Button mLogBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_);

        mRegBtn = (Button) findViewById(R.id.start_cre_btn);
        mLogBtn = (Button) findViewById(R.id.start_log_btn);
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent reg_intent = new Intent(Start_Activity.this, RegisterActivity.class);
                startActivity(reg_intent);

            }
        });

        mLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent reg_intent = new Intent(Start_Activity.this, LoginActivity.class);

                startActivity(reg_intent);

            }
        });

    }

}

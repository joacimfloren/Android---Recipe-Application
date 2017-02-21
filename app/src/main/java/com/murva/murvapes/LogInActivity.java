package com.murva.murvapes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import MurvaTools.*;

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in2);

        MurvaTools.LoadToken(this);
        if(GlobalData.tokenEncoded.access_token != null){
            this.startActivity(new Intent(this, MainActivity.class));
            finish();
        }

    }

    public void logInAccount(View view){
        Intent intent = new Intent(this, LogInAccountActivity.class);
        startActivity(intent);
    }

    public void signUpAccount(View view){
        Intent intent = new Intent(this, AccountCreateActivity.class);
        startActivity(intent);
    }

}

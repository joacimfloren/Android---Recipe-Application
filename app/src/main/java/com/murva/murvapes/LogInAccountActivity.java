package com.murva.murvapes;

import android.os.AsyncTask;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import MurvaTools.Validation;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import Models.Account;
import Models.LogInAccount;
import MurvaTools.MurvaTools;


public class LogInAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_account);
    }
    public void logIn(final View view){
        LogInAccount acc = new LogInAccount();
        acc.username = ((EditText) findViewById(R.id.editText_userName)).getText().toString();
        acc.password = ((EditText) findViewById(R.id.editText_password)).getText().toString();
        if (Validation.validateLogIn(acc, view)){

            MurvaTools.LogIn(acc, view, this);
        }
        return;
    }
}

package com.murva.murvapes;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import MurvaTools.GpsActivity;
import MurvaTools.MurvaTools;
import MurvaTools.Validation;


public class AccountCreateActivity extends GpsActivity {

    View theView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_create);

        theView = (View) findViewById(R.id.activity_account_create);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapmapmap);
        mapFragment.getMapAsync(this);
    }

    public void createAccount(final View view){
        Models.Account acc = new Models.Account();
        acc.userName = ((EditText) findViewById(R.id.editText_userName)).getText().toString();
        acc.Password = ((EditText) findViewById(R.id.editText_password)).getText().toString();

        LatLng pos;
        try {
            pos = currLocationMarker.getPosition();
            if (Validation.validateSignUp(acc, view)){
                acc.latitude = pos.latitude;
                acc.longitude = pos.longitude;
                MurvaTools.SignUp(acc, view, this);
            }
        }
        catch (Exception e)
        {
            MurvaTools.showSnackbar(theView, "Du måste aktivera GPS för att skapa ett konto.");
        }

        return;
    }
}

package com.murva.murvapes;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import MurvaTools.*;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import Models.Account;
import MurvaTools.GlobalData;
import MurvaTools.MurvaTools;

/**
 * Created by Johan Rasmussen on 2016-12-01.
 */

public class ProfileFragment extends GpsFragment {
    View myView;

    private Button deleteButton;
    private Button saveButton;
    private EditText longitudeText;
    private EditText latitudeText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_profile, container, false);

        longitudeText = (EditText) myView.findViewById(R.id.profile_longitude);
        latitudeText = (EditText) myView.findViewById(R.id.profile_latitude);

        try {
            longitudeText.setText(Double.toString(GlobalData.User.longitude));
            latitudeText.setText(Double.toString(GlobalData.User.latitude));
        }
        catch(Exception e){
            MurvaTools.showSnackbar(myView, "Connection error");
        }

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.accountMap);
        mapFragment.getMapAsync(this);

        return myView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deleteButton = (Button) myView.findViewById(R.id.profile_btn_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccountClicked();
            }
        });
        saveButton = (Button) myView.findViewById(R.id.profile_btn_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });
    }

    private void deleteAccountClicked(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        MurvaTools.Delete("accounts/" + GlobalData.User.id, new MurvaTools.ErrorCallback() {
                            @Override
                            public void callback(String error) {
                                if (error == null) {
                                    MurvaTools.ClearToken(myView.getContext());
                                    MurvaTools.DeleteRecipeListFromDisk(myView.getContext().getString(R.string.recipe_storage_favourite), myView.getContext());
                                    Intent intent = new Intent(myView.getContext(), LogInActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    myView.getContext().startActivity(intent);
                                }
                                else {
                                    MurvaTools.showSnackbar(myView, error);
                                }
                            }
                        });
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(myView.getContext());
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void saveChanges() {
        try {
            Double longitude = Double.parseDouble(longitudeText.getText().toString());
            Double latitude = Double.parseDouble(latitudeText.getText().toString());

            if (longitude == GlobalData.User.longitude && latitude == GlobalData.User.latitude) {
                MurvaTools.showSnackbar(myView, "Nothing to change.");
            }
            else {
                Gson gson = new Gson();
                Account account = GlobalData.User;
                account.longitude = longitude;
                account.latitude = latitude;
                MurvaTools.Update("accounts/" + GlobalData.User.id.toString(), gson.toJson(account), new MurvaTools.ErrorCallback() {
                    @Override
                    public void callback(String error) {
                        if (error == null) {
                            MurvaTools.showSnackbar(myView, "Success!");
                        }
                        else {
                            MurvaTools.showSnackbar(myView, error);
                        }
                    }
                });
            }
        }
        catch (Exception e) {
            MurvaTools.showSnackbar(myView, "Longitude/Latitude must be a number.");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);

        mGoogleMap = googleMap;
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }

        latLng = new LatLng(GlobalData.User.latitude, GlobalData.User.longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currLocationMarker = mGoogleMap.addMarker(markerOptions);

        if (allowClickToChangePosition) {
            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng point) {
                    latLng = point;
                    mGoogleMap.clear();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Current Position");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                    currLocationMarker = mGoogleMap.addMarker(markerOptions);

                    longitudeText.setText(Double.toString(latLng.longitude));
                    latitudeText.setText(Double.toString(latLng.latitude));
                }
            });
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(14).build();

        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }
}

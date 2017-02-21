package MurvaTools;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.murva.murvapes.AccountCreateActivity;
import com.murva.murvapes.R;

/**
 * Created by joacimfloren on 2016-12-14.
 */

public class GpsActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    protected LocationRequest mLocationRequest;
    protected GoogleApiClient mGoogleApiClient;
    protected SupportMapFragment mapFragment;
    protected LatLng latLng;
    protected GoogleMap mGoogleMap;
    protected Marker currLocationMarker;
    protected boolean allowClickToChangePosition = true;

    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 124;
    private static final int MY_PERMISSION_REQUEST_COURSE_LOCATION = 125;
    private boolean permissionIsGranted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //LatLng marker = new LatLng(-33.867, 151.206);

        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED/* && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED*/) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
                }
            }
            /*if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_REQUEST_COURSE_LOCATION);
                }
            }*/

            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        buildGoogleApiClient();
        mGoogleApiClient.connect();

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
                }
            });
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED/* && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED*/) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
                }
            }
            /*if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_REQUEST_COURSE_LOCATION);
                }
            }*/

            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mGoogleMap.addMarker(markerOptions);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(14).build();

            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, GpsActivity.this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (currLocationMarker != null) {

        }
        else {
            mGoogleMap.clear();
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mGoogleMap.addMarker(markerOptions);

            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(14).build();

            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSION_REQUEST_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    permissionIsGranted = true;
                }
                else {
                    permissionIsGranted = false;
                    Toast.makeText(getApplicationContext(), "This app requires permission to be granted", Toast.LENGTH_SHORT).show();
                }
                break;
            /*case MY_PERMISSION_REQUEST_COURSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    permissionIsGranted = true;
                }
                else {
                    permissionIsGranted = false;
                    Toast.makeText(getApplicationContext(), "This app requires permission to be granted", Toast.LENGTH_SHORT).show();
                }
                break;*/
        }
    }

}

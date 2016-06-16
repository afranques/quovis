package com.afranques.quovis;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SetLocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final int REQUEST_LOCATION_PERMISSION_CODE = 2;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager locationManager;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates;
    private boolean hasLocation = false;

    private GoogleMap mMap;

    private double latitude = 0;
    private double longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);

        //to get the parameters sent from the previous intent
        Intent prevIntent = getIntent();
        //consider that maybe there's no picture
        final Bitmap bmp = (Bitmap) prevIntent.getParcelableExtra("the_picture");
        final int category_id = prevIntent.getIntExtra("category_id", -1);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // https://github.com/dmytrodanylyk/android-process-button/wiki/User-Guide
        ActionProcessButton btnProcess = (ActionProcessButton) findViewById(R.id.btnProcess);
        btnProcess.setMode(ActionProcessButton.Mode.ENDLESS);
        btnProcess.setProgress(1);
        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionProcessButton btn = (ActionProcessButton) view;
                if(btn.getProgress() == 100){
                    Intent intent = new Intent(view.getContext(), NewPlaceSummaryActivity.class);
                    intent.putExtra("the_picture", bmp);
                    intent.putExtra("category_id", category_id);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    startActivity(intent);
                }
            }
        });

//        Button button = new Button(this);
//        button.setText("Save location");
//        addContentView(button, new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                stopLocationUpdates(); //we stop updating maps
//                Intent intent = new Intent(v.getContext(), NewPlaceSummaryActivity.class);
//                intent.putExtra("the_picture", bmp);
//                intent.putExtra("category_id", category_id);
//                intent.putExtra("latitude", latitude);
//                intent.putExtra("longitude", longitude);
//                startActivity(intent);
//            }
//        });
    }


    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //since we make use of maps, we've got to check if we have permission granted
            enableMyLocation();
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //if (mLocation == null) {
        startLocationUpdates();
        //Toast.makeText(this, "Started location updates", Toast.LENGTH_SHORT).show();

        //we put the loading button into loading position (so that it can't be clicked)
        ActionProcessButton btnProcess = (ActionProcessButton) findViewById(R.id.btnProcess);
        btnProcess.setProgress(1);
        //}
        if (mLocation != null) {
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();
            //Toast.makeText(this, "Got initial coordinates", Toast.LENGTH_SHORT).show();
            CameraUpdate center=CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
        }
        else {
            //Toast.makeText(this, "Location not detected", Toast.LENGTH_SHORT).show();
        }
    }

    protected void startLocationUpdates() {
        // Create the location request
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //since we make use of maps, we've got to check if we have permission granted
            enableMyLocation();
        }
        mRequestingLocationUpdates = true;
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000 * 5); //5 seconds requested interval (might be faster if some other app asks)
                //.setFastestInterval(1000*5); this sets amount of time has to occur before updating
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        //Log.d("reque", "--->>>>");
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void stopLocationUpdates() {
        if (mRequestingLocationUpdates) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            mRequestingLocationUpdates = false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.getAccuracy() < 20 || distanceTwoLocations(mLocation, location) < 20) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            //Toast.makeText(this, "New location", Toast.LENGTH_SHORT).show();
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
            hasLocation = true; //do I really need this flag? I think I'm not using it
            ActionProcessButton btnProcess = (ActionProcessButton) findViewById(R.id.btnProcess);
            btnProcess.setProgress(100);
        }
    }

    public double distanceTwoLocations(Location loc1, Location loc2) {
        //this is not an accurate formula, to see a better one search: Haversine formula
        double lat1 = loc1.getLatitude();
        double lat2 = loc2.getLatitude();
        double lon1 = loc1.getLongitude();
        double lon2 = loc2.getLongitude();
        double theDistance = Math.sqrt(Math.pow(lat1-lat2, 2)+Math.pow(lon1-lon2, 2));
        return theDistance;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If we don't have access yet, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION_CODE);
        } else if (mMap != null) {
            // If we already have access, prepare maps
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION_CODE: {
                if (grantResults.length > 0) {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                        enableMyLocation();
                    } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        //Toast.makeText(this, "We need location to get your place", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        //Toast.makeText(this, "The app is not gonna work without location permitions, so go to settings and allow it", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    }
                    //return;
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
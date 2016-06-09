package com.afranques.quovis;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowPlaceInMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DatabaseHelper myDb;
    private int place_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_place_in_maps);

        //to get the parameters sent from the previous intent
        Intent prevIntent = getIntent();
        place_id = prevIntent.getIntExtra("place_id", -1);
        //Toast.makeText(this, Integer.toString(place_id), Toast.LENGTH_SHORT).show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        LatLng placeLocation;
        myDb = new DatabaseHelper(this);
        Cursor res = myDb.getPlace(place_id);
        if (res.getCount() != 0) {
            res.moveToNext();

            // Add a marker in place location and move the camera
            placeLocation = new LatLng(res.getDouble(4), res.getDouble(5));
            //Toast.makeText(this, res.getString(0)+" "+res.getString(4)+" "+res.getString(5), Toast.LENGTH_LONG).show();
            //String snippetString = "Category: "+res.getString(3)+"\nDescription: "+res.getString(2);

            String pic_location = res.getString(6);
            Toast.makeText(this, pic_location, Toast.LENGTH_SHORT).show();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(pic_location, options);

            mMap.addMarker(new MarkerOptions()
                    .position(placeLocation)
                    .title(res.getString(1))
                    .snippet(res.getString(2))
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 16));
        }
    }
}

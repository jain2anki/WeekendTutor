package com.chetan.wt;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    Location locaton;
    Double lat;
    Double lon;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

                lat=12.917;
                lon=74.85603;
                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(lat, lon));
                Toast.makeText(getApplicationContext(),"Latitude="+lat+"\tlongitude="+lon,Toast.LENGTH_LONG).show();
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);
                mMap.clear();

                MarkerOptions mp = new MarkerOptions();

                mp.position(new LatLng(lat, lon));

                mp.title("my position");

                mMap.addMarker(mp);
                mMap.moveCamera(center);
                mMap.animateCamera(zoom);


            }
        }


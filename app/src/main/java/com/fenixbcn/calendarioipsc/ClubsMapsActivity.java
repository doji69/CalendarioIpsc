package com.fenixbcn.calendarioipsc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ClubsMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "Calendario Ipsc";

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static final float DEFAULT_ZOOM = 16;

    private Boolean mLocationPermissionGranted = false;

    private GoogleMap mMap;
    String selectedTitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clubs_maps);

        Bundle clubsMapsActivityVars = getIntent().getExtras();
        selectedTitulo = clubsMapsActivityVars.getString("selectedTitulo");

        getLocationPermission();

    }

    private void getLocationPermission() {

        //Log.d (Tag, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                //Log.d (Tag, "getLocationPermission: permission are granted");
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Log.d (Tag, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;

        switch (requestCode) {

            case LOCATION_PERMISSION_REQUEST_CODE: {
                if ((grantResults.length > 0)) {

                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            //Log.d (Tag, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    //Log.d (Tag, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;
                    // inicialize the map
                    initMap();
                }
            }
        }
    }

    private void initMap() {

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

        // Add a markers
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        //Log.d(Tag,"onMapReady: map is ready");

        mMap = googleMap;

        if (mLocationPermissionGranted) {

            mMap.setMapType(googleMap.MAP_TYPE_HYBRID);

            UiSettings uiSettingsMap = mMap.getUiSettings();
            uiSettingsMap.setZoomControlsEnabled(true);
            uiSettingsMap.setMapToolbarEnabled(true);

            LatLng latPositionSel = Funciones.getLocation(selectedTitulo);

            if (latPositionSel != null) {

                mMap.addMarker(new MarkerOptions().position(latPositionSel).title("Club tiro"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latPositionSel));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latPositionSel,DEFAULT_ZOOM));

            } else {
                Toast.makeText(this, "no hay mapa", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

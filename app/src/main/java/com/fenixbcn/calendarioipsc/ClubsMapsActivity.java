package com.fenixbcn.calendarioipsc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    @SuppressLint("MissingPermission")
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
                    mMap.setMyLocationEnabled(true);
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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        //Log.d(Tag,"onMapReady: map is ready");

        mMap = googleMap;

        if (mLocationPermissionGranted) {

            mMap.setMapType(googleMap.MAP_TYPE_HYBRID);
            mMap.setMyLocationEnabled(true);

            UiSettings uiSettingsMap = mMap.getUiSettings();
            uiSettingsMap.setZoomControlsEnabled(true);
            uiSettingsMap.setMapToolbarEnabled(true);

            LatLng latPositionSel = Funciones.getLocation(selectedTitulo);

            if (latPositionSel != null) {
                // ubicamos el club de tiro
                mMap.addMarker(new MarkerOptions().position(latPositionSel).title("Club tiro"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latPositionSel));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latPositionSel,DEFAULT_ZOOM));

                // modificamos el marker de mi posicion
                LatLng myPosition = changeMyLocationMarker();

                // creamos la url para la ruta entre los dos puntos
                String sLocationUrl = getLocationUrl(myPosition,latPositionSel);

                TakeRequestDirections takeRequestDirections = new TakeRequestDirections();
                takeRequestDirections.execute(sLocationUrl);



            } else {
                Toast.makeText(this, "no hay mapa", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private LatLng changeMyLocationMarker () {

        LatLng myLatLng = null;
        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String s = locationManager.getBestProvider(criteria, false);

        @SuppressLint("MissingPermission") Location myLocation = locationManager.getLastKnownLocation(s);

        if (myLocation != null) {

            myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(myLatLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }


        return myLatLng;
    }

    private String getLocationUrl (LatLng myPosition, LatLng latPositionSel) {

        String origen = "origin=" + myPosition.latitude + "," + myPosition.longitude;
        String destination = "destination=" + latPositionSel.latitude + "," + latPositionSel.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String params = origen + "&" + destination + "&" + sensor + "&" + mode;
        String format = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + format + "?" + params;

        Log.d (TAG, "la url es: " + url);

        return url;
    }

    private String requestDirection (String requestUrl) throws IOException {

        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(requestUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {

                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (inputStream != null) {
                inputStream.close();

            }
            httpURLConnection.disconnect();
        }

        return responseString;
    }

    public class TakeRequestDirections extends AsyncTask<String, Void ,String> {


        @Override
        protected String doInBackground(String... strings) {

            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // hay que pasear el resultado que viene en json

            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> > {


        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {

            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            // obtener lista de rutas y mostrar en el mapa

            ArrayList points = null;
            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path: lists) {

                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point: path) {

                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat,lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions != null) {

                mMap.addPolyline(polylineOptions);
            } else {

                Toast.makeText(ClubsMapsActivity.this, "Direccion no encontrada.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

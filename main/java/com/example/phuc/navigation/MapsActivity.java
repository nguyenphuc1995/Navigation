package com.example.phuc.navigation;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.phuc.navigation.Controller.Detectpolyline;
import com.example.phuc.navigation.Controller.Detectpolyline;
import com.example.phuc.navigation.Controller.DirectionFinder;
import com.example.phuc.navigation.Controller.QueueArray;
import com.example.phuc.navigation.Controller.TTS;
import com.example.phuc.navigation.Model.Route;
import com.example.phuc.navigation.Model.Station;


import com.example.phuc.navigation.View.DirectionFinderListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
//import com.niw.utility.DebugLog;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;



class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient googleApiClient;
    private GoogleMap mMap;
    private int interval = 0;
    private int connect = 0;
    private int mode = 1; //0 Vi, 1 En, 2 maneuver
    private Button btnFindPath,btnStart;
    //private String Start = "Siêu Thị Vitamin Care, Phường 15, Ho Chi Minh, Vietnam", End = "279 Điện Biên Phủ, Phường 15, Bình Thạnh, Hồ Chí Minh, Vietnam";
    //private String Start ="10.778398, 106.682550", End = "10.774649, 106.678962";
    private String Start = "100 nguyen thi minh khai", End = "bao tang chung tich chien tranh";
    private static Location newLocation, preLocation;
    private LatLng myLatLng = null;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Marker> stationMarkers = new ArrayList<>();
    private List<Polyline> Paths = new ArrayList<>();
    private List<Station> stations;
    private List<Detectpolyline> detectWrongWay;
    private QueueArray queueArray = new QueueArray();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.phuc.navigation.R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(com.example.phuc.navigation.R.id.map);
        mapFragment.getMapAsync(this);

        btnFindPath = (Button) findViewById(com.example.phuc.navigation.R.id.btnFindPath);
        btnStart = (Button) findViewById (com.example.phuc.navigation.R.id.btnStart);
        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new DirectionFinder(MapsActivity.this, Start, End, mode).execute();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildGoogleApiClient();

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng home = new LatLng(10.770271, 106.649800);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 13));


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MapsActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                checkLocationPermission();
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void checkInput() {
        Toast.makeText(MapsActivity.this,"Input an Address",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onDirectionFinderSuccess(List<Route> routes, List<Station> stations, List<Detectpolyline> detectpolylines) {
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
            for (Polyline polyline:Paths ) {
                polyline.remove();
            }
        }

        Paths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 18));
            goToLocation(route.startLocation.latitude,route.startLocation.longitude,18,0,0);
            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.RED).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
            {
                polylineOptions.add(route.points.get(i));
//                stationMarkers.add(mMap.addMarker(new MarkerOptions()
//                        .position(route.points.get(i))));
            }
            this.detectWrongWay = detectpolylines;
            this.stations = stations;
            if (mode == 1)
            {
                this.stations.get(this.stations.size()-1).maneuver = "thank you";
            }
            Paths.add(mMap.addPolyline(polylineOptions));
            StopGoogleApiClient();
            preLocation = null;
            LatLng current = route.startLocation;

            //GeoJsonLineString
        }
    }

    public void setArlet(LatLng CurrentLocation, List<Station> stations)
    {
        for (int i = 0; i< stations.size(); i++)
        {
            if (stations.get(i).status == 1)
            {
                if (getDistance(CurrentLocation,stations.get(i).startStation)<50)
                {
                    if (mode == 0 || mode ==1) {
                        navigation(stations.get(i).maneuver);
                    }
                    else if (mode == 2)
                    {
                        navigation((stations.get(i).miniManuever));
                    }
                    stations.get(i).status = 2;
                    //stations.get(i+1).status = 1;
                    if (i<(stations.size()-1))
                    {
                        stations.get(i+1).status = 1;
                    }

                    if (stations.get(stations.size()-1).status == 2)
                    {
                        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
                        StopGoogleApiClient();
                        //Toast.makeText(MapsActivity.this,"mai di hoc",Toast.LENGTH_SHORT).show();
                        goToLocation(stations.get((stations.size()-1)).startStation.latitude,stations.get((stations.size()-1)).startStation.longitude,18,0,0);
                        return;
                    }
                }
            }

        }
        return;
    }
   public void DectectWrongWay(LatLng currentLocation)
    {
        for (int i = 1; i < detectWrongWay.size(); i++)
        {
             if (detectWrongWay.get(i).status == 1)
            {
                System.out.println(i);

                double currentToPre = getDistance(currentLocation,detectWrongWay.get(i-1).polyline);
                double currentoNew = getDistance(currentLocation,detectWrongWay.get(i).polyline);
                double distanceAccept = getDistance(detectWrongWay.get(i).polyline,detectWrongWay.get(i-1).polyline);
                System.out.println(currentToPre);
                System.out.println(currentoNew);
                System.out.println(distanceAccept);
                double tmp = currentoNew + currentToPre;
                if ((tmp - distanceAccept) > 100)
                {
                    //tts.speak(mode,"Wrong way");
                    Toast.makeText(MapsActivity.this,"Wrong way",Toast.LENGTH_SHORT).show();
//                    String start = Double.toString(currentLocation.latitude) + "," + Double.toString(currentLocation.longitude);
//                    StopGoogleApiClient();
//                    try {
//                        new DirectionFinder(MapsActivity.this, start, End, mode).execute();
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                    buildGoogleApiClient();


                }
                if (getDistance(currentLocation,detectWrongWay.get(i).polyline)<20)
                {
                    detectWrongWay.get(i).status = 2;

                    if (i<(detectWrongWay.size()-1))
                    {
                        detectWrongWay.get(i+1).status = 1;
                    }
                }
                return;

            }
        }
    }
    public double getDistance(LatLng latLngFrom,LatLng latLngTo){
        Location loc1 = new Location("");
        Location loc2 = new Location("");

        loc1.setLatitude(latLngFrom.latitude);
        loc1.setLongitude(latLngFrom.longitude);

        loc2.setLatitude(latLngTo.latitude);
        loc2.setLongitude(latLngTo.longitude);

        double distanceInMeters = loc1.distanceTo(loc2);
//        float sm = Float.valueOf(String.format("%.2f", distanceInMeters));
        //DebugLog.loge(distanceInMeters);
        return distanceInMeters;
    }
    public void navigation(String Nav)
    {
        //new TTS(mode,Nav).speak();
        //tts.speak(mode,Nav);
        queueArray.AddQeue(mode,Nav);
        if (queueArray.getTotal() == 1)
        {
            queueArray.dequeue();
        }
    }

    //create by Huy, modify by Phuc
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void goToLocation(double lat, double lng, float zoom, float tilt, float bearing) {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lng))
                .bearing(bearing)
                .zoom(zoom)                   // Sets the zoom
                .tilt(tilt)    // Sets the tilt of the camera to 30 degrees
                .build();    // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                cameraPosition),1000,null);
        //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }
    protected synchronized void buildGoogleApiClient() {

        if (connect == 0){
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
            connect = 1;
        }
        System.out.println(connect);


    }
    protected synchronized void StopGoogleApiClient()
    {
        if (connect ==1 ) {
            googleApiClient.disconnect();
            connect = 0;
        }
        System.out.println(connect);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        interval++;
        if (location == null) {
            Toast.makeText(MapsActivity.this, "Can't get current location!", Toast.LENGTH_SHORT).show();
        }
        else {
            if (preLocation == null) {
                preLocation = location;
            }
            newLocation = location;
            //
            if (stations != null) {
                setArlet(new LatLng(newLocation.getLatitude(), newLocation.getLongitude()), stations);
                DectectWrongWay(new LatLng(newLocation.getLatitude(), newLocation.getLongitude()));
            }
            goToLocation(newLocation.getLatitude(), newLocation.getLongitude(), 19, 70, preLocation.bearingTo(location));
            if (interval % 20 == 0)
            {
                updateMap(newLocation.getLatitude(), newLocation.getLongitude());
            }
            preLocation = location;
        }
    }
    public void updateMap(double lat, double lng) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(ll);
        mMap.moveCamera(cameraUpdate);
    }



}

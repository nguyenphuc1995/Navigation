package com.example.navigation;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.example.navigation.Controller.Detectpolyline;
import com.example.navigation.Controller.DirectionFinder;
import com.example.navigation.Controller.QueueArray;
import com.example.navigation.Model.Route;
import com.example.navigation.Model.Station;
import com.example.navigation.View.DirectionFinderListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

//import com.niw.utility.DebugLog;


public class Navigation extends FragmentActivity implements DirectionFinderListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient googleApiClient;
    private Activity activity;
    private GoogleMap mMap;
    private int interval = 0;
    private int connect = 0;
    private int mode = 1; //0 Vi, 1 En, 2 maneuver
    //private String Start = "Siêu Thị Vitamin Care, Phường 15, Ho Chi Minh, Vietnam", End = "279 Điện Biên Phủ, Phường 15, Bình Thạnh, Hồ Chí Minh, Vietnam";
    //private String Start ="10.780768, 106.684992", End = "10.774649, 106.678962";
    private String Start = "ben xe mien Dong", End = "ben xe mien Tay";
    private static Location newLocation, preLocation;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> Paths = new ArrayList<>();
    private List<Station> stations;
    private List<Detectpolyline> detectWrongWay;
    private QueueArray queueArray = new QueueArray();
    private String googleDirectionAPIKey;
    private Marker mPositionMarker;
    private MarkerOptions markerOptions = new MarkerOptions();


    public void setData(Activity activity, GoogleMap googleMap, int mode, String googleDirectionAPIKey) {
        this.mode = mode;
        this.activity = activity;
        this.mMap = googleMap;
        this.googleDirectionAPIKey = googleDirectionAPIKey;
    }


    public void findDirection(String Start, String End)
    {
        try {
            new DirectionFinder(Navigation.this, Start, End, mode).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public void startNavigation() {
        preLocation = null;
        buildGoogleApiClient();

    }
    public void stopNavigation()
    {
        System.out.println();
        if (googleApiClient!=null)
        {
            if (googleApiClient.isConnected())
            {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                stopGoogleApiClient();
            }
        }
        if (newLocation != null)
        {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(newLocation.getLatitude(), newLocation.getLongitude()), 18));
            goToLocation(newLocation.getLatitude(),newLocation.getLongitude(),18,0,0);
        }
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
            stopGoogleApiClient();
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
                        stopGoogleApiClient();
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

                if (getDistance(currentLocation,detectWrongWay.get(i).polyline)<20)
                {
                    detectWrongWay.get(i).status = 2;

                    if (i<(detectWrongWay.size()-1))
                    {
                        detectWrongWay.get(i+1).status = 1;
                    }
                }

                //System.out.println(i);
                double currentToPre = getDistance(currentLocation,detectWrongWay.get(i-1).polyline);
                double currentoNew = getDistance(currentLocation,detectWrongWay.get(i).polyline);
                double distanceAccept = getDistance(detectWrongWay.get(i).polyline,detectWrongWay.get(i-1).polyline);
//                System.out.println(currentToPre);
//                System.out.println(currentoNew);
//                System.out.println(distanceAccept);
                double tmp = currentoNew + currentToPre;
                if ((tmp - distanceAccept) > 100)
                {
                    //tts.speak(mode,"Wrong way");
                    Toast.makeText(activity,"Wrong way",Toast.LENGTH_SHORT).show();
//                    String start = Double.toString(currentLocation.latitude) + "," + Double.toString(currentLocation.longitude);
//                    StopGoogleApiClient();
//                    try {
//                        new DirectionFinder(MapsActivity.this, start, End, mode).execute();
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                    buildGoogleApiClient();


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
            googleApiClient = new GoogleApiClient.Builder(activity)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
            connect = 1;
        }
        System.out.println(connect);


    }
    protected synchronized void stopGoogleApiClient()
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
        if (ActivityCompat.checkSelfPermission(activity,
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
            Toast.makeText(activity, "Can't get current location!", Toast.LENGTH_SHORT).show();
        }
        else {
            if (preLocation == null) {
                preLocation = location;
            }
            newLocation = location;
            //
            if (mPositionMarker == null) {

                mPositionMarker = mMap.addMarker(new MarkerOptions()
                        .flat(true)
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.gps))
                        .anchor(0.5f, 0.5f)
                        .position(
                                new LatLng(location.getLatitude(), location
                                        .getLongitude())));
            }

            animateMarker(mPositionMarker, location);
            if (stations != null) {
                setArlet(new LatLng(newLocation.getLatitude(), newLocation.getLongitude()), stations);
                DectectWrongWay(new LatLng(newLocation.getLatitude(), newLocation.getLongitude()));
            }
            goToLocation(newLocation.getLatitude(), newLocation.getLongitude(), 19, 70, preLocation.bearingTo(location));
            if (interval % 10 == 0)
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
    public int getConnect()
    {
        return this.connect;
    }
    public void animateMarker(final Marker marker, final Location location) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final LatLng startLatLng = marker.getPosition();
        final double startRotation = marker.getRotation();
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);

                double lng = t * location.getLongitude() + (1 - t)
                        * startLatLng.longitude;
                double lat = t * location.getLatitude() + (1 - t)
                        * startLatLng.latitude;

                float rotation = (float) (t * location.getBearing() + (1 - t)
                        * startRotation);

                marker.setPosition(new LatLng(lat, lng));
                marker.setRotation(rotation);

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }



}
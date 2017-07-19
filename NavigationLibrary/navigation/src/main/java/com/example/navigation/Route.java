package com.example.navigation;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by phuc on 7/16/17.
 */
public class Route {

    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;
    public List<LatLng> points;
}

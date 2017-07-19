package com.example.navigation.Model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by phuc on 7/16/17.
 */

public class Station {
    public LatLng startStation;
    public String maneuver;
    public String miniManuever = "Continue straight";
    public int status = 0; //0 not use,1 wait to turn, 2 uesd
}

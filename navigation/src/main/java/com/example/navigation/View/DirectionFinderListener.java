package com.example.navigation.View;

import android.location.Location;

import com.example.navigation.Controller.Detectpolyline;
import com.example.navigation.Model.Route;
import com.example.navigation.Model.Station;

import java.util.List;

/**
 * Created by phuc on 7/16/17.
 */

public interface DirectionFinderListener {

    void onDirectionFinderSuccess(List<Route> route, List<Station> stations, List<Detectpolyline> detectpolylines);
}

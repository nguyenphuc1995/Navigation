package com.example.phuc.navigation.View;

import com.example.phuc.navigation.Controller.Detectpolyline;
import com.example.phuc.navigation.Model.Route;
import com.example.phuc.navigation.Model.Station;

import java.util.List;

/**
 * Created by phuc on 7/16/17.
 */

public interface DirectionFinderListener {
    void checkInput();
    void onDirectionFinderSuccess(List<Route> route, List<Station> stations, List<Detectpolyline> detectpolylines);

}

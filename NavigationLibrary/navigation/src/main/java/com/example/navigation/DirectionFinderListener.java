package com.example.navigation;

import java.util.List;

/**
 * Created by phuc on 7/16/17.
 */

public interface DirectionFinderListener {

    void onDirectionFinderSuccess(List<Route> route, List<Station> stations, List<Detectpolyline> detectpolylines);

}

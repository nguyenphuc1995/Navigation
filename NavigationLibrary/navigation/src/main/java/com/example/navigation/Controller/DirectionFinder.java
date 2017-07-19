package com.example.navigation.Controller;



import com.example.navigation.Model.DownloadRawData;
import com.example.navigation.View.DirectionFinderListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by phuc on 7/16/17.
 */

public class DirectionFinder {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static String GOOGLE_API_KEY = "AIzaSyCCW7p0e7vJAdXrW9QGW3YvGG4BpnHVW_A";
    private DirectionFinderListener ViewDirection;
    private String Start;
    private String End;
    private int mode;
   // private List<Route> Routes;



    public DirectionFinder(DirectionFinderListener listener, String origin, String destination,int mode) {
        this.mode = mode;
        this.ViewDirection = listener;
        this.Start = origin;
        this.End = destination;
    }
    public void setDirectionFinderAPIKey(String googleDirectionAPIKey) {
        this.GOOGLE_API_KEY = googleDirectionAPIKey;
    }


    public void execute() throws UnsupportedEncodingException {

        new DownloadRawData(ViewDirection,mode).execute(createUrl());

    }

    private String createUrl() throws UnsupportedEncodingException {
        String urlOrigin = URLEncoder.encode(Start, "utf-8");
        String urlDestination = URLEncoder.encode(End, "utf-8");
        if (mode == 0 ){
            return DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&language=vi&key=" + GOOGLE_API_KEY;
        }
        return DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&language=en&key=" + GOOGLE_API_KEY;

    }

}




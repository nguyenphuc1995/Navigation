package com.example.phuc.navigation.Controller;

import com.example.phuc.navigation.Model.Route;
import com.example.phuc.navigation.View.DirectionFinderListener;
import com.example.phuc.navigation.Model.DownloadRawData;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by phuc on 7/16/17.
 */

public class DirectionFinder {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyCCW7p0e7vJAdXrW9QGW3YvGG4BpnHVW_A";
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

    public boolean checkInput()
    {
        if (End == null || (Start == null))
        {
            ViewDirection.checkInput();
            return false;
        }
        return true;

    }
    public void execute() throws UnsupportedEncodingException {
        if (checkInput())
        {
            new DownloadRawData(ViewDirection,mode).execute(createUrl());

        }
        return;
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




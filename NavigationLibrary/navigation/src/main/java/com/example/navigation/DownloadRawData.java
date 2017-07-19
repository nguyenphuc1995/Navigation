package com.example.navigation;

/**
 * Created by phuc on 7/16/17.
 */

import android.os.AsyncTask;


import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DownloadRawData extends AsyncTask<String, Void, String> {
    private int mode;
    private DirectionFinderListener ViewDirection;

    public DownloadRawData(DirectionFinderListener finder, int mode) {
        this.ViewDirection = finder;
        this.mode = mode;
    }

    @Override
    public String doInBackground(String... params) {
        String link = params[0];
        try {
            URL url = new URL(link);
            InputStream is = url.openConnection().getInputStream();
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            return buffer.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String res) {
        try {
            parseJSon(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parseJSon(String data) throws JSONException {
        if (data == null) {
            return;
        }
        List<Route> routes = new ArrayList<Route>();
        List<Station> Stations = new ArrayList<Station>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
        List<Detectpolyline> detectpolylines = new ArrayList<>();
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            Route route = new Route();

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
            JSONObject jsonLeg = jsonLegs.getJSONObject(0);
            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

            JSONArray jsonSteps = jsonLeg.getJSONArray("steps");

            for (int j = 0; j < jsonSteps.length(); j++) {
                Station station = new Station();
                String maneuver = "no data";
                JSONObject jsonStep = jsonSteps.getJSONObject(j);
                JSONObject jsonStepStartLocation = jsonStep.getJSONObject("start_location");
                if (jsonStep.has("maneuver"))
                {
                    station.miniManuever = jsonStep.getString("maneuver");
                }
                station.startStation = new LatLng(jsonStepStartLocation.getDouble("lat"), jsonStepStartLocation.getDouble("lng"));
                if (mode == 0) {
                    maneuver = decodeHTMLvi(jsonStep.getString("html_instructions"));
                }
                if (mode == 1) {
                    maneuver = decodeHTMLen(jsonStep.getString("html_instructions"));
                }
                //maneuver = jsonStep.getString("html_instructions");
                station.maneuver = maneuver;
                Stations.add(station);
            }

            route.endAddress = jsonLeg.getString("end_address");
            route.startAddress = jsonLeg.getString("start_address");
            route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
            route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
            route.points = decodePolyLine(overview_polylineJson.getString("points"));

            for (int k = 0; k< route.points.size(); k++ )
            {
                Detectpolyline detectpolyline = new Detectpolyline();
                detectpolyline.polyline = route.points.get(k);
                detectpolylines.add(detectpolyline);
            }
            Station station = new Station();
            Detectpolyline detectpolyline = new Detectpolyline();
            station.maneuver = "Cảm ơn bạn đã sử dụng chương trình";
            station.miniManuever = "Thank you";
            station.startStation = new LatLng(route.endLocation.latitude,route.endLocation.longitude);
            detectpolyline.polyline = station.startStation;
            detectpolylines.add(detectpolyline);
            Stations.add(station);
            routes.add(route);
        }
        Stations.get(0).status = 1;
        detectpolylines.get(0).status = 2;
        detectpolylines.get(1).status = 1;
        ViewDirection.onDirectionFinderSuccess(routes, Stations, detectpolylines);
    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }

    private String decodeHTMLvi(String tmp) {
        String tmp2;
        tmp = tmp.split("<div")[0];
        tmp = tmp.replace("</b>/<b>", " ");
        tmp = tmp.replace("<b>", "");
        tmp = tmp.replace("</b>", "");
        tmp = tmp.replace("amp;", "");
        if (tmp.startsWith("Đi về hướng Tây"))
        {
            return "Đi về hướng Tây";
        }
        if (tmp.startsWith("Đi về hướng Đông"))
        {
            return "Đi về hướng Đông";
        }
        if (tmp.startsWith("Đi về hướng Nam"))
        {
            return "Đi về hướng Nam";
        }
        if (tmp.startsWith("Đi về hướng Bắc"))
        {
            return "Đi về hướng Bắc";
        }
        if (tmp.startsWith("Đi về hướng Tây Nam"))
        {
            return "Đi về hướng Tây Nam";
        }
        if (tmp.startsWith("Đi về hướng Tây Bắc"))
        {
            return "Đi về hướng Tây Bắc";
        }
        if (tmp.startsWith("Đi về hướng Đông Bắc"))
        {
            return "Đi về hướng Đông Bắc";
        }
        if (tmp.startsWith("Đi về hướng Đông Nam"))
        {
            return "Đi về hướng Đông Nam";
        }
        if (tmp.startsWith("Tại Bùng binh") || tmp.startsWith("Tại Vòng Xoay") || tmp.startsWith("Tại vòng xuyến")){
            return tmp;
        }
        if (tmp.contains("về hướng")) {
            tmp2 = tmp;
            tmp2 = tmp2.split("sang")[0];
            tmp = tmp2 + "sang" + tmp.split("về hướng")[1];
            return tmp;
        }
        if (tmp.startsWith("Rẽ trái") || tmp.startsWith("Rẽ phải")) {
            tmp2 = tmp;
            tmp2 = tmp2.split("tại")[0];
            tmp = tmp2 + " vào" + tmp.split("vào")[1];
            return tmp;
        }
        if ( tmp.startsWith("Chếch sang phải") || tmp.startsWith("Chếch sang trái")){
            tmp2 = tmp;
            tmp2 = tmp2.split("tại")[0];
            tmp = tmp2 + "vào" + tmp.split("vào")[1];
            return tmp;

        }
        if (tmp.startsWith("Tại")) {
            tmp = "Vào" + tmp.split("vào")[1];
            return tmp;
        }
        if (tmp.startsWith("Tiếp tục đi thẳng")) {
            tmp = tmp.split("thẳng")[0] + "thẳng";
            return tmp;
        }
        return tmp;
    }
    private String decodeHTMLen(String tmp) {
        String tmp2;
        tmp = tmp.split("<div")[0];
        tmp = tmp.replace("</b>/<b>", " ");
        tmp = tmp.replace("<b>", "");
        tmp = tmp.replace("</b>", "");
        tmp = tmp.replace("amp;", "");
        if (tmp.contains("U-turn"))
        {
            return tmp.replace("U-turn","turn around");
        }
        if (tmp.startsWith("At Vòng xoay")||tmp.startsWith("At Bùng binh"))
        {
            tmp = "At the roundabout," + tmp.split(",")[1];
            return tmp;
        }
        if (tmp.startsWith("Head south"))
        {
            return "Head south";

        }
        if (tmp.startsWith("Head west"))
        {
            return "Head west";

        }
        if (tmp.startsWith("Head north"))
        {
            return "Head north";

        }

        if (tmp.startsWith("Head east"))
        {
            return "Head east";

        }
        if (tmp.startsWith("Head southeast"))
        {
            return "Head southeast";

        }
        if (tmp.startsWith("Head southwest"))
        {
            return "Head southwest";

        }
        if (tmp.startsWith("Head northeast"))
        {
            return "Head northeast";

        }
        if (tmp.startsWith("Head northwest"))
        {
            return "Head northwest";

        }
        if (tmp.startsWith("At the roundabout")){
            return tmp;
        }
        if (tmp.contains("toward")) {
            tmp2 = tmp;
            tmp2 = tmp2.split("at")[0];
            tmp = tmp2 + "toward" +tmp.split("toward")[1];
            return tmp;
        }
        if (tmp.startsWith("Turn right") || tmp.startsWith("Turn left")) {
            tmp2 = tmp;
            tmp2 = tmp2.split("at")[0];
            tmp = tmp2 + " onto" + tmp.split("onto")[1];
            return tmp;
        }
        if ( tmp.startsWith("Slight left") || tmp.startsWith("Slight right")){
            tmp2 = tmp;
            tmp2 = tmp2.split("at")[0];
            tmp = tmp2 + "onto" + tmp.split("onto")[1];
            return tmp;

        }
        if (tmp.startsWith("At")) {
            tmp = "Continue onto" + tmp.split("onto")[1];
            return tmp;
        }
        if (tmp.startsWith("Continue straight")) {
            tmp = tmp.split("straight")[0] + "straight";
            return tmp;
        }
        return tmp;
    }

}


//package com.journaldev.MapsInAction;
//
///**
// * Created by lorkh on 23/04/2018.
// */
//
//
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.model.Marker;
//import android.view.View;
//
//
//public class Yourcustominfowindowadapter implements GoogleMap.InfoWindowAdapter {
//    private final View mymarkerview;
//
//    Yourcustominfowindowadapter() {
//        mymarkerview = getLayoutInflater()
//                .inflate(R.layout.custominfowindow, null);
//    }
//
//    public View getInfoWindow(Marker marker) {
//        render(marker, mymarkerview);
//        return mymarkerview;
//    }
//
//    public View getInfoContents(Marker marker) {
//        return null;
//    }
//
//    private void render(Marker marker, View view) {
//        // Add the code to set the required values
//        // for each element in your custominfowindow layout file
//    }
//}
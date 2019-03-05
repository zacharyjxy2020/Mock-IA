package com.example.zachary.hktour;

import android.content.Intent;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;

public class Evaluate {
    private static final String TAG = "Evaluate";
    private static final int ITERATE = 0;
    private int startHour;
    private int endHour;
    private int startMinutes;
    private int endMinutes;
    private String remainingTime;
    private int remainingHours;
    private int remainingMinutes;
    private ArrayList<Landmark> landmarks;

    public Evaluate(int startHour, int startMinutes, int endMinutes, int endHour)
    {
        this.startHour = startHour;
        this.startMinutes = startMinutes;
        this.endHour = endHour;
        this.endMinutes = endMinutes;

        remainingHours = 0;
        remainingMinutes = 0;
        remainingTime = "";
    }

    public Evaluate(ArrayList<Landmark> landmarks){
        startMinutes = 0;
        startHour = 0;
        endMinutes = 0;
        endHour = 0;
        this.landmarks = landmarks;

    }


    private void calculateTime()
    {
        Log.d(TAG, "calculateTime: calculating time");
        if(startHour>endHour){
            remainingHours = 24-startHour+endHour;
        }else {
            remainingHours = endHour-startHour;
        }if(startMinutes > endMinutes){
            remainingHours -= 1;
            remainingMinutes = (60-startMinutes)+endMinutes;
        }else{
            remainingMinutes = endMinutes-startMinutes; }
        remainingTime = "" + remainingHours + ":" + remainingMinutes;
    }



    public int getStartMinutes() {
        return startMinutes;
    }

    public int getEndMinutes() {
        return endMinutes;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getStartHour() {
        return startHour;
    }

    public String getRemainingTime() {
        calculateTime();
        return remainingTime;
    }
    public String makeItinerary(){
        double remainingTimeA = remainingHours*60+remainingMinutes;
        double totalTravelTime = calculateTravelTime(calculateDistance());
        remainingTimeA = remainingTimeA - totalTravelTime;
        double remainingTimeB = remainingTimeA/landmarks.size();
        String timetoAdd = minutesToTime((int)remainingTimeB);
        String itinerary = "";
        String times = ""+startHour+":"+startMinutes;
        String tmp = "";
        String tmp2 = "";

        for(int count = 0; count < landmarks.size()-1; count++){
            tmp = minutesToTime((int)calculateTravelTime(getDistance(landmarks.get(count).getLatLng().latitude,
                    landmarks.get(count+1).getLatLng().latitude,
                    landmarks.get(count).getLatLng().longitude,
                    landmarks.get(count+1).getLatLng().longitude)));
            times = addTime(times,tmp);
            tmp2 = addTime(times,timetoAdd);
            itinerary+= "At " + times + "get to " + landmarks.get(count) + ". Stay until " +
                    tmp2 + ". \n";
        }
        tmp = minutesToTime((int)calculateTravelTime(getDistance(landmarks.get(0).getLatLng().latitude,
                landmarks.get(landmarks.size()-1).getLatLng().latitude,
                landmarks.get(0).getLatLng().longitude,
                landmarks.get(landmarks.size()-1).getLatLng().longitude)));
        times = addTime(times, tmp);
        itinerary += "At " + times + "get home. Thank y ou for using HKTour";
        return itinerary;

    }

    public int getRemainingHours() {
        return remainingHours;
    }

    public int getRemainingMinutes() {
        return remainingMinutes;
    }

    private double calculateTravelTime(double distance){
        return distance/30;
    }

    private double calculateDistance(){
        double totalDistance = getDistance(landmarks.get(0).getLatLng().latitude,
                landmarks.get(landmarks.size()-1).getLatLng().latitude,
                landmarks.get(0).getLatLng().longitude,
                landmarks.get(landmarks.size()-1).getLatLng().longitude);
        for(int x = 0; x < landmarks.size()-1; x++){
            totalDistance += getDistance(landmarks.get(x).getLatLng().latitude,
                    landmarks.get(x+1).getLatLng().latitude,
                    landmarks.get(x).getLatLng().longitude,
                    landmarks.get(x+1).getLatLng().longitude);
        }
        return totalDistance;
    }
    //https://readyandroid.wordpress.com/calculate-distance-between-two-latlng-points-using-google-api-or-math-function-android/
    public static double getDistance(double lat_a, double lng_a, double lat_b, double lng_b) {
        // earth radius is in mile
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b - lat_a);
        double lngDiff = Math.toRadians(lng_b - lng_a);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2)
                + Math.cos(Math.toRadians(lat_a))
                * Math.cos(Math.toRadians(lat_b)) * Math.sin(lngDiff / 2)
                * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;
        double kmConvertion = 1.6093;
        // return new Float(distance * meterConversion).floatValue();
        return new Float(distance * kmConvertion).floatValue();
        // return String.format("%.2f", distance)+" m";
    }

    private String addTime(String time, String addtime){
        int timeHour = Integer.parseInt(time.substring(0,time.indexOf(':')));
        int timeMinutes = Integer.parseInt(time.substring(time.indexOf(':')+1,time.length()-1));
        int addHour = Integer.parseInt(addtime.substring(0,addtime.indexOf(':')));
        int addMinutes = Integer.parseInt(addtime.substring(addtime.indexOf(':')+1,addtime.length()-1));
        int finalHour = timeHour + addHour;
        int finalMinutes = timeMinutes + addMinutes;

        if(addMinutes+timeMinutes > 60){
            finalHour+=1;
            finalMinutes%= 60;
        }

        return ""+ finalHour + ":" + finalMinutes;
    }

    private String minutesToTime(int minutes){
        int hours  = minutes/60;
        int minutesR = minutes%60;
        return ""+ hours + ":" + minutesR;
    }

    public void setLandmarks(ArrayList<Landmark> landmarks) {
        this.landmarks = landmarks;
    }
}

package com.example.zachary.hktour;

import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * <h1>Evaluate</h1>
 * The Evaluate class completes all of the calculations used in the application.
 * <p>
 * @author Zachary Yu
 * @since 20/3/19
 * @version 2.0.1
 */
public class Evaluate {
    private static final String TAG = "Evaluate";
    private int startHour;
    private int endHour;
    private int startMinutes;
    private int endMinutes;
    private String remainingTime;
    private int remainingHours;
    private int remainingMinutes;
    private ArrayList<Landmark> landmarks;

    private LatLng currentLocation;

    /**
     * Constructor for Evaluate object.
     * @param startHour Start hour of the tour
     * @param startMinutes Start minute of the tour
     * @param endMinutes End minutes of the tour
     * @param endHour End hour of the tour
     */
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

    /**
     * Method that calculates the remaining time in the tour
     */
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

    /**
     * Returns the remaining time
     * @return instance variable remainingTime
     */
    public String getRemainingTime() {
        calculateTime();
        return remainingTime;
    }

    /**
     * Creates the itinerary
     * @return the itinerary as a string
     */
    public String makeItinerary(){

        double distanceFromLandmark = getDistance(new Landmark(currentLocation), landmarks.get(0));
        double timefromLandmark = distanceFromLandmark/5;
        double remainingTimeA = remainingHours*60+remainingMinutes;
        double totalTravelTime = calculateDistance()/5;


        totalTravelTime += timefromLandmark;
        timefromLandmark = timefromLandmark*60;
        totalTravelTime = totalTravelTime*60;
        remainingTimeA = remainingTimeA - totalTravelTime;


        double remainingTimeB = remainingTimeA/landmarks.size();
        String lengthOfStay = minutesToTime(remainingTimeB);
        String itinerary = "";
        String currtime = ""+startHour+":"+startMinutes;
        Log.d(TAG, "makeItinerary: CurrTime: " + currtime );
        Log.d(TAG, "makeItinerary: LengthOfStay: " + lengthOfStay);

        String timeBetweenLandmarks = "";
        String timetoStayTill = "";
        currtime = addTime(minutesToTime(timefromLandmark), currtime);

        for(int count = 1; count < landmarks.size(); count++){
            timeBetweenLandmarks = minutesToTime(60*calculateTravelTime(landmarks.get(count), landmarks.get(count-1)));
            Log.d(TAG, "makeItinerary: timeBetweenLandmarks " + count + " "+ timeBetweenLandmarks );
            //tmp is set the the time it takes to get from the first landmark to the second

            timetoStayTill = addTime(currtime,lengthOfStay);
            Log.d(TAG, "makeItinerary: TimetoStayTill " + count + " " + timetoStayTill);

            itinerary += "At " + currtime + " get to " + landmarks.get(count-1).getName() + ". Stay until " +
                    timetoStayTill + ". \n";
            Log.d(TAG, "makeItinerary: Itinerary " + count+ " "+ itinerary);

            currtime = timetoStayTill;
            currtime = addTime(currtime, timeBetweenLandmarks);
            Log.d(TAG, "makeItinerary: currTime " + count + " "+ currtime);

            }
        timeBetweenLandmarks = minutesToTime(60*calculateTravelTime(landmarks.get(landmarks.size()-1),landmarks.get(landmarks.size()-2)));
        currtime = addTime(timeBetweenLandmarks,currtime);
        timetoStayTill = addTime(currtime,lengthOfStay);
        itinerary += "At " + currtime + " get to " + landmarks.get(landmarks.size()-1).getName() + ". Stay until " +
                timetoStayTill + ". \n";

        currtime = timetoStayTill;

        timeBetweenLandmarks = minutesToTime(60*calculateTravelTime( landmarks.get(landmarks.size()-1),new Landmark(currentLocation)));
        currtime = addTime(currtime, timeBetweenLandmarks);
        itinerary += "At " + currtime + " get home. Thank you for using HKTour \n" ;

        if(currtime != "" + endHour + ":" + endMinutes){
            itinerary += "There isn't enough time in the tour to reach all these locations, Here is a recommended Itinerary ending time: " +
            currtime;
        }
        return itinerary;
    }


    /**
     * Calculates travel time between two landmarks.
     * @param a First landmark
     * @param b Second landmark
     * @return the distance between the two landmarks divided by the assumed speed of 20 kmph
     */
    private double calculateTravelTime(Landmark a, Landmark b){
        return (getDistance(a,b)/5);
    }

    /**
     * Calculates the total distance in the tour.
     * @return a double which is the total distance in the tour.
     */
    private double calculateDistance(){
        double totalDistance = getDistance(landmarks.get(0), landmarks.get(landmarks.size()-1));
        for(int x = 0; x < landmarks.size()-1; x++){
            totalDistance += getDistance(landmarks.get(x), landmarks.get(x+1));
        }
        return totalDistance;
    }

    /**
     * Calculates the distance between two landmarks
     * @param a First Landmark
     * @param b Second Landmark
     * @return the distance between the two landmarks in Km
     */
    public static double getDistance(Landmark a, Landmark b) {

        double lat_a = a.getLatLng().latitude;
        double lat_b = b.getLatLng().latitude;
        double lng_a = a.getLatLng().longitude;
        double lng_b = b.getLatLng().longitude;
        float[] results = new float[1];
        Location.distanceBetween(lat_a, lng_a, lat_b, lng_b, results);
        return results[0]/1000;
    }

    /**
     * Method that adds two time values together
     * @param time a string of time
     * @param addtime time to be added
     * @return the added time
     */
    private String addTime(String time, String addtime){
        Log.d(TAG, "addTime: adding time");
        int timeHour = Integer.parseInt(time.substring(0,time.indexOf(':')));
        int timeMinutes = Integer.parseInt(time.substring(time.indexOf(':')+1));
        int addHour = Integer.parseInt(addtime.substring(0,addtime.indexOf(':')));
        int addMinutes = Integer.parseInt(addtime.substring(addtime.indexOf(':')+1));
        int finalHour = timeHour + addHour;
        int finalMinutes = timeMinutes + addMinutes;

        if(addMinutes+timeMinutes >= 60){
            finalHour+=1;
            finalMinutes%= 60;
        }

        if(finalHour > 24){
            finalHour = finalHour - 24;
        }

        String finalHourString = String.valueOf(finalHour);
        String finalMinutesString = String.valueOf(finalMinutes);

        if(finalHourString.length() == 1){
            finalHourString = "0"+finalHourString;
        } else if (finalMinutesString.length() == 1) {
            finalMinutesString = "0" + finalMinutesString;
        }

        Log.d(TAG, "addTime: adding successful");
        return finalHourString + ":" + finalMinutesString;
    }

    /**
     * Turns minutes into a string time value.
     * @param minutes integer value of minutes
     * @return String value of time.
     */
    private String minutesToTime(double minutes){
        int hours  = (int)Math.round(minutes/60);
        int minutesR = (int)Math.round(minutes%60);


        String hoursString = String.valueOf(hours);
        String minutesString = String.valueOf(minutesR);

        if(hoursString.length() == 1){
            hoursString = "0"+hoursString;
        }else if(minutesString.length() == 1){
            minutesString = "0" + minutesString;
        }

        return hoursString + ":" + minutesString;
    }

    /**
     * Sets landmarks to the instance variable.
     * @param landmarks arraylist of landmarks that is to be set to instance variable.
     */
    public void setLandmarks(ArrayList<Landmark> landmarks) {
        this.landmarks = landmarks;
    }

    public void setCurrentLocation(LatLng currentLocation) {
        Log.d(TAG, "setCurrentLocation: Current location has been set");
        this.currentLocation = currentLocation;
    }

}

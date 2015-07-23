package com.popalay.bmxspots.models;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.popalay.bmxspots.activities.MainActivity;

public class Spot {

    private String ID;
    private String title;
    private String description;
    private String author;
    private String authorID;
    private ParseGeoPoint position;
    private String address;
    private int rating;
    private double distance;

    private Marker marker;
    private boolean isMy;
    private boolean isFavorite;

    public Spot(String title, String description, String author, String authorID, LatLng position, String address) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.authorID = authorID;
        this.position = new ParseGeoPoint(position.latitude, position.longitude);
        this.address = address;
        this.rating = 0;
        this.distance = getDistanceTo();
        this.isFavorite = false;
        this.isMy = true;
    }

    public Spot(ParseObject newSpot) {
        this.ID = newSpot.getObjectId();
        this.title = newSpot.getString("title");
        this.description = newSpot.getString("description");
        this.author = newSpot.getString("author");
        this.authorID = newSpot.getString("authorID");
        this.position = newSpot.getParseGeoPoint("position");
        this.address = newSpot.getString("address");
        this.rating = newSpot.getInt("rating");
        this.distance = getDistanceTo();
    }

    public void save() {
        ParseObject spot = new ParseObject("Spot");
        this.ID = spot.getObjectId();
        spot.put("title", this.title);
        spot.put("description", this.description);
        spot.put("author", this.author);
        spot.put("authorID", this.authorID);
        spot.put("position", this.position);
        spot.put("address", this.address);
        spot.put("rating", this.rating);
        spot.saveEventually();
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LatLng getPosition() {
        return new LatLng(position.getLatitude(), position.getLongitude());
    }

    public void setPosition(LatLng position) {
        this.position = new ParseGeoPoint(position.latitude, position.longitude);
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating += rating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public double getDistance() {
        return distance = getDistanceTo();
    }

    public void setDistance() {
        this.distance = getDistanceTo();
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public void toFavorite() {
        this.isFavorite = true;
        ParseObject spot = new ParseObject("Favorite");
        spot.put("userID", ParseUser.getCurrentUser().getObjectId());
        spot.put("spotID", ID);
        spot.saveEventually();
    }

    public void intoFavorite() {
        this.isFavorite = false;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Favorite");
        query.whereEqualTo("userID", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("spotID", ID);
        query.getFirstInBackground((parseObject, e) -> {
            if (parseObject != null)
                parseObject.deleteEventually();
        });
    }

    private double getDistanceTo() {
        Location myLocation = MainActivity.getCurrentLocation();
        ParseGeoPoint myPosition = new ParseGeoPoint(myLocation.getLatitude(), myLocation.getLongitude());
        double d = position.distanceInKilometersTo(myPosition);
        d = Math.round(d * 100);
        d /= 100;
        return d;
    }

    public void setIsMy(boolean isMy) {
        this.isMy = isMy;
    }

    public boolean isMy() {
        return isMy;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public boolean isFavorite() {
        return isFavorite;
    }
}

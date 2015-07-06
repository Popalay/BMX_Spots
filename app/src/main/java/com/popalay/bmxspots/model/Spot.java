package com.popalay.bmxspots.model;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

public class Spot {

    private String title;
    private String description;
    private String author;
    private String authorID;
    private LatLng location;
    private String address;
    private int positiveRating;
    private int negativeRating;

    public Spot(String title, String description, String author, String authorID, LatLng location, String address) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.authorID = authorID;
        this.location = location;
        this.address = address;
        this.positiveRating = 0;
        this.negativeRating = 0;
    }

    public Spot(ParseObject newSpot) {
        this.title = newSpot.getString("title");
        this.description = newSpot.getString("description");
        this.author = newSpot.getString("author");
        ParseGeoPoint point = newSpot.getParseGeoPoint("location");
        this.location = new LatLng(point.getLatitude(), point.getLongitude());
        this.address = newSpot.getString("address");
        this.positiveRating = newSpot.getInt("positiveRating");
        this.negativeRating = newSpot.getInt("negativeRating");
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

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public int getNegativeRating() {
        return negativeRating;
    }

    public void setNegativeRating(int negativeRating) {
        this.negativeRating += negativeRating;
    }

    public int getPositiveRating() {
        return positiveRating;
    }

    public void setPositiveRating(int positiveRating) {
        this.positiveRating += positiveRating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean save() {
        ParseObject spot = new ParseObject("Spot");
        spot.put("title", this.title);
        spot.put("description", this.description);
        spot.put("author", this.author);
        spot.put("authorID", this.authorID);
        spot.put("location", new ParseGeoPoint(location.latitude, location.longitude));
        spot.put("address", this.address);
        spot.put("positiveRating", this.positiveRating);
        spot.put("negativeRating", this.negativeRating);
        spot.saveEventually();
        return true;
    }
}

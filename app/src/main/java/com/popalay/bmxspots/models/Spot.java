package com.popalay.bmxspots.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.popalay.bmxspots.MainActivity;
import com.popalay.bmxspots.Repo;

@ParseClassName("Spot")
public class Spot extends ParseObject {

    public Spot() {

    }

    public ParseUser getAuthor() {
        return getParseUser("author");
    }

    public void setAuthor(ParseUser author) {
        put("author", author);
    }

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        put("description", description);
    }

    public ParseGeoPoint getPosition() {
        return getParseGeoPoint("position");
    }

    public void setPosition(ParseGeoPoint position) {
        put("position", position);
    }

    public int getRating() {
        return getInt("rating");
    }

    public void setRating(int rating) {
        put("rating", rating);
    }

    public double getDistanceTo() {
        double d = getPosition().distanceInKilometersTo(MainActivity.getCurrentLocation());
        d = Math.round(d * 100);
        d /= 100;
        return d;
    }

    public static ParseQuery<Spot> getQuery() {
        return ParseQuery.getQuery(Spot.class)
                .orderByAscending("createdAt");
    }

    public void addToFavorite() {
        ParseRelation<Spot> favorites = ParseUser.getCurrentUser().getRelation("favorites");
        favorites.add(this);
        ParseUser.getCurrentUser().saveInBackground();
    }

    public void removeIntoFavorite() {
        ParseRelation<Spot> favorites = ParseUser.getCurrentUser().getRelation("favorites");
        favorites.remove(this);
        ParseUser.getCurrentUser().saveInBackground();
    }

    public boolean isMy() {
        return getAuthor() == ParseUser.getCurrentUser();
    }

    public boolean isFavorite() {
        int count = 0;
        try {
            count = Repo.getFavoriteSpots().whereEqualTo("objectId", getObjectId()).count();
        } catch (ParseException e) {
            Log.d("Spot", "Is favorite: " + e.getMessage());
        }
                //.countInBackground((i, e) -> Log.d("Spot", "favorite: " + (i > 0)));
        return count > 0;
    }


    /*private String ID;
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
    }*/
}

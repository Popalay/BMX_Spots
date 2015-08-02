package com.popalay.bmxspots;

import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.popalay.bmxspots.models.Spot;

public class Repo {

    public static ParseQuery<Spot> loadAllSpots() {
        return Spot.getQuery().setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
    }

    public static ParseQuery<Spot> getAllSpots() {
        ParseQuery<Spot> query = Spot.getQuery();
        query.fromPin();
        return query;
    }

    public static ParseQuery<Spot> getMySpots() {
        ParseQuery<Spot> query = Spot.getQuery().whereEqualTo("author", ParseUser.getCurrentUser());
        query.fromPin();
        return query;
    }

    public static ParseQuery<Spot> getFavoriteSpots() {
        ParseQuery<Spot> query = ParseUser.getCurrentUser().<Spot>getRelation("favorites").getQuery();
        query.fromPin();
        return query;
    }

    public static ParseQuery<Spot> getSurroundingSpots() {
        ParseQuery<Spot> query = Spot.getQuery().whereWithinKilometers("position", MainActivity.getCurrentLocation(), 5.0);
        query.fromPin();
        return query;
    }

    public static void clearCache() {
        Spot.unpinAllInBackground();
    }
}

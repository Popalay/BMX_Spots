package com.popalay.bmxspots;

import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.popalay.bmxspots.models.Spot;

public class Repo {

    public static ParseQuery<Spot> loadAllSpots() {
        MainActivity.showProgress("Load spots...");
        Spot.getQuery().findInBackground((objects, e) -> {
            // Remove the previously cached results.
            Spot.unpinAllInBackground(e1 -> {
                // Cache the new results.
                Spot.pinAllInBackground(objects);
            });
            MainActivity.hideProgress();
        });
        return Spot.getQuery();
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

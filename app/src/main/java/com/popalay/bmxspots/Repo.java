package com.popalay.bmxspots;

import android.text.TextUtils;
import android.util.Log;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.popalay.bmxspots.models.Spot;

import java.util.ArrayList;
import java.util.List;

public class Repo {//TODO синхронизация загрузки даных

    public interface OnLoadAllSpots {
        void onLoadAllSpots();
    }

    private List<Spot> allSpots;
    private List<Spot> mySpots;
    private List<Spot> favoriteSpots;
    private List<Spot> surroundingSpots;
    private ParseUser user;

    private List<OnLoadAllSpots> listeners;

    public void addOnRefreshAllSpotsListeners(OnLoadAllSpots listener) {
        this.listeners.add(listener);
    }

    public Repo() {
        this.listeners = new ArrayList<>();
        this.allSpots = new ArrayList<>();
        this.mySpots = new ArrayList<>();
        this.favoriteSpots = new ArrayList<>();
        this.surroundingSpots = new ArrayList<>();
        this.setUser();
    }

    public boolean load() {
        loadAllSpots();
        return true;
    }

    public List<Spot> loadAllSpots() {
        ParseQuery<ParseObject> query = new ParseQuery<>("Spot");
        query.findInBackground((spots, e) -> {
            if (e == null) {
                // your logic here
                allSpots.clear();
                allSpots.addAll(Stream.of(spots).map(Spot::new).collect(Collectors.<Spot>toList()));
                loadMySpots();
                loadFavoriteSpots();
                loadSurroundingSpots();
            } else {
                // handle Parse Exception here
                Log.d("Repo", "loadAllSpots:" + e.getMessage());
            }
        });
        return allSpots;
    }

    public List<Spot> loadMySpots() {
            Stream.of(allSpots)
                    .filter(spot -> TextUtils.equals(spot.getAuthorID(), user.getObjectId()))
                    .forEach(spot -> spot.setIsMy(true));
            mySpots.clear();
            mySpots.addAll(Stream.of(allSpots)
                    .filter(Spot::isMy)
                    .collect(Collectors.<Spot>toList()));
        return mySpots;
    }

    public List<Spot> loadFavoriteSpots() {
            List<String> spotIDs = new ArrayList<>();
            ParseQuery<ParseObject> query = new ParseQuery<>("Favorite");
            query.findInBackground((favorites, e) -> {
                if (e == null) {
                    // your logic here
                    Stream.of(favorites)
                            .filter(favorite -> TextUtils.equals(favorite.getString("userID"), user.getObjectId()))
                            .forEach(favorite -> spotIDs.add(favorite.getString("spotID")));
                    Stream.of(spotIDs).forEach(id -> Log.d("Repo", id));
                    if (spotIDs.size() > 0) {
                        Stream.of(allSpots)
                                .filter(spot -> spotIDs.contains(spot.getID()))
                                .forEach(spot -> spot.setIsFavorite(true));
                        favoriteSpots.clear();
                        favoriteSpots.addAll(Stream.of(allSpots)
                                .filter(Spot::isFavorite)
                                .collect(Collectors.<Spot>toList()));
                    }
                } else {
                    // handle Parse Exception here
                    Log.d("Repo", "loadFavoriteSpots:" + e.getMessage());
                }
                Stream.of(listeners).forEach(OnLoadAllSpots::onLoadAllSpots);
                Stream.of(allSpots).forEach(spot -> Log.d("Repo", spot.getTitle() + ": IsMy - " + spot.isMy()
                        + ", isFavorite - " + spot.isFavorite()));
            });
        return favoriteSpots;
    }

    public List<Spot> loadSurroundingSpots() {
        surroundingSpots.clear();
        surroundingSpots.addAll(Stream.of(allSpots)
                .filter(spot -> spot.getDistance() <= 5)
                .collect(Collectors.<Spot>toList()));
        return surroundingSpots;
    }

    public void addSpot(Spot spot) {
        allSpots.add(spot);
        mySpots.add(spot);
    }

    public List<Spot> getFavoriteSpots() {
        return favoriteSpots;
    }

    public List<Spot> getMySpots() {
        return mySpots;
    }

    public List<Spot> getAllSpots() {
        return allSpots;
    }

    public List<Spot> getSurroundingSpots() {
        return surroundingSpots;
    }

    public ParseUser getUser() {
        return user;
    }

    public void setUser() {
        this.user = ParseUser.getCurrentUser();
    }

    public void clearMySpots() {
        this.mySpots.clear();
    }
}

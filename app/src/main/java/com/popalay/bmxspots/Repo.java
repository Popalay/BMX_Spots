package com.popalay.bmxspots;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.popalay.bmxspots.model.Spot;

import java.util.ArrayList;
import java.util.List;

//TODO
public class Repo {
    private ArrayList<Spot> allSpots;
    private ArrayList<Spot> mySpots;
    private ArrayList<Spot> favoriteSpots;
    private ParseUser user;

    public Repo() {
        this.allSpots = new ArrayList<>();
        this.mySpots = new ArrayList<>();
        this.favoriteSpots = new ArrayList<>();
        this.setUser();
    }

    public boolean load() {
        loadAllSpots();
        loadMySpots();
        loadFavoriteSpots();
        return true;
    }

    public void loadAllSpots() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Spot");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> spots, ParseException e) {
                if (e == null) {
                    // your logic here
                    for (ParseObject spot : spots) {
                        allSpots.add(new Spot(spot));
                    }
                } else {
                    // handle Parse Exception here
                }
            }
        });
    }

    public void loadMySpots() {

    }

    public void loadFavoriteSpots() {

    }

    public void addSpot(Spot spot) {
        allSpots.add(spot);
        mySpots.add(spot);
    }

    public void addFavoriteSpot(Spot spot) {
        favoriteSpots.add(spot);
    }

    public void synhSpots() {

    }

    public ArrayList<Spot> getFavoriteSpots() {
        return favoriteSpots;
    }

    public ArrayList<Spot> getMySpots() {
        return mySpots;
    }

    public ArrayList<Spot> getAllSpots() {
        return allSpots;
    }

    public ParseUser getUser() {
        return user;
    }

    public void setUser() {
        this.user = ParseUser.getCurrentUser();
    }
}

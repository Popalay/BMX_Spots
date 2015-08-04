package com.popalay.bmxspots.lists;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseQueryAdapter;
import com.popalay.bmxspots.R;
import com.popalay.bmxspots.models.Spot;

public class FavoriteAdapter extends ParseQueryAdapter<Spot> {

    public interface OnMapClickListener {
        void onMapClick(Spot spot);
    }

    private OnMapClickListener onMapClickListener;

    public void setOnMapClickListener(OnMapClickListener listener) {
        this.onMapClickListener = listener;
    }

    public FavoriteAdapter(Context context, QueryFactory<Spot> queryFactory) {
        super(context, queryFactory);
    }

    @Override
    public View getItemView(Spot spot, View v, ViewGroup parent) {

        if (v == null) {
            if (spot.isMy()) {
                v = View.inflate(getContext(), R.layout.card_my, null);
            } else {
                v = View.inflate(getContext(), R.layout.card_normal, null);
            }
        }

        super.getItemView(spot, v, parent);

        TextView spotTitle = (TextView) v.findViewById(R.id.spot_title);
        spotTitle.setText(spot.getTitle());
        TextView spotAuthor = (TextView) v.findViewById(R.id.spot_author);
        spotAuthor.setText(spot.getAuthor().getUsername());
        TextView spotDistance = (TextView) v.findViewById(R.id.spot_distance);
        spotDistance.setText(spot.getDistanceTo() + "km");
        TextView spotDescription = (TextView) v.findViewById(R.id.spot_description);
        spotDescription.setText(spot.getDescription());
        Button btnFavorite = (Button) v.findViewById(R.id.card_btn_favorite);
        btnFavorite.setOnClickListener(v1 -> {
            spot.removeIntoFavorite();
            loadObjects();
        });
        Button btnMap = (Button)v.findViewById(R.id.card_btn_map);
        btnMap.setOnClickListener(v1 -> onMapClickListener.onMapClick(spot));
        btnFavorite.setText("Into favorite");
        return v;
    }

}

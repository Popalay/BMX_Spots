package com.popalay.bmxspots.fragment.mainfragment.tabs;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.popalay.bmxspots.R;
import com.popalay.bmxspots.model.Spot;

import java.util.List;

public class RecyclerViewAdepter extends RecyclerView.Adapter<RecyclerViewAdepter.SpotViewHolder> {

    public static class SpotViewHolder extends RecyclerView.ViewHolder {
        private CardView cv;
        private TextView spotTitle;
        private TextView spotDescription;

        private SpotViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            spotTitle = (TextView) itemView.findViewById(R.id.spot_title);
            spotDescription = (TextView) itemView.findViewById(R.id.spot_description);
            //personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
        }
    }

    private List<Spot> spots;

    public RecyclerViewAdepter(List<Spot> spots) {
        this.spots = spots;
    }

    @Override
    public SpotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        SpotViewHolder svh = new SpotViewHolder(v);
        return svh;
    }

    @Override
    public void onBindViewHolder(SpotViewHolder holder, int position) {
        holder.spotTitle.setText(spots.get(position).getTitle());
        holder.spotDescription.setText(spots.get(position).getDescription());
        //TODO
    }

    @Override
    public int getItemCount() {
        return spots.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

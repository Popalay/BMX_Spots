/*
package com.popalay.bmxspots.lists;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.popalay.bmxspots.R;
import com.popalay.bmxspots.models.Spot;

import java.util.List;


public class RecyclerViewAdepterDefault extends RecyclerView.Adapter<RecyclerViewAdepterDefault.SpotViewHolder> {


    public static class SpotViewHolder extends RecyclerView.ViewHolder {

        protected interface ISpotClickListener {
            void toMapClick(int position);

            void favoriteClick(int position);
        }

        protected ISpotClickListener listener;

        protected TextView spotTitle;
        protected TextView spotAuthor;
        protected TextView spotDistance;
        protected TextView spotDescription;

        //private Button toMap;
        protected Button btnFavorite;

        protected SpotViewHolder(View itemView) {
            super(itemView);
            this.spotTitle = (TextView) itemView.findViewById(R.id.spot_title);
            this.spotAuthor = (TextView) itemView.findViewById(R.id.spot_author);
            this.spotDistance = (TextView) itemView.findViewById(R.id.spot_distance);
            this.spotDescription = (TextView) itemView.findViewById(R.id.spot_description);
            this.btnFavorite = (Button) itemView.findViewById(R.id.card_btn_favorite);
            this.btnFavorite.setOnClickListener(v -> listener.favoriteClick(getAdapterPosition()));
            //this.toMap = (Button) itemView.findViewById(R.id.card_btn_toMap);
            //toMap.setOnClickListener(v -> listener.toMapClick(getAdapterPosition()));
        }

        protected void setClickListener(ISpotClickListener listener) {
            this.listener = listener;
        }
    }

    public static class SpotViewHolderMy extends SpotViewHolder {

        public interface ISpotClickListenerMy extends SpotViewHolder.ISpotClickListener {
            void editClick(int position);
        }

        private SpotViewHolderMy(View itemView) {
            super(itemView);
        }

        public void setClickListener(ISpotClickListener listener) {
            this.listener = listener;
        }
    }

    private List<Spot> spots;

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_MY = 1;

    public RecyclerViewAdepterDefault(List<Spot> spots) {
        this.spots = spots;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = TYPE_NORMAL;
        if(spots.get(position).isMy()) {
            viewType = TYPE_MY;
        }
        return viewType;
    }

    @Override
    public SpotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        SpotViewHolder spotViewHolder;
        switch (viewType) {
            case TYPE_MY:
                ViewGroup vMy = (ViewGroup) mInflater.inflate(R.layout.card_my, parent, false);
                spotViewHolder = new SpotViewHolderMy(vMy);
                break;
            default:
                ViewGroup vNormal = (ViewGroup) mInflater.inflate(R.layout.card_normal, parent, false);
                spotViewHolder = new SpotViewHolder(vNormal);
                break;
        }
        return spotViewHolder;
    }

    @Override
    public void onBindViewHolder(SpotViewHolder holder, int position) {
        if(spots.get(position).isFavorite()){
            holder.btnFavorite.setText("Into favorite");
        }
        if (holder instanceof SpotViewHolderMy) {
            holder.setClickListener(new SpotViewHolderMy.ISpotClickListenerMy() {
                @Override
                public void favoriteClick(int position) {
                    if(spots.get(position).isFavorite()){
                        spots.get(position).intoFavorite();
                        holder.btnFavorite.setText("To favorite");
                    } else {
                        spots.get(position).toFavorite();
                        holder.btnFavorite.setText("Into favorite");
                    }
                }

                @Override
                public void editClick(int position) {

                }

                @Override
                public void toMapClick(int position) {

                }
            });
        } else {
            holder.setClickListener(new SpotViewHolder.ISpotClickListener() {
                @Override
                public void toMapClick(int position) {

                }

                @Override
                public void favoriteClick(int position) {
                    if(spots.get(position).isFavorite()){
                        spots.get(position).intoFavorite();
                        holder.btnFavorite.setText("To favorite");
                    } else {
                        spots.get(position).toFavorite();
                        holder.btnFavorite.setText("Into favorite");
                    }
                }
            });
        }
        holder.spotTitle.setText(spots.get(position).getTitle());
        holder.spotAuthor.setText(spots.get(position).getAuthor());
        holder.spotDistance.setText(spots.get(position).getDistance() + "km");
        holder.spotDescription.setText(spots.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return spots.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void updateList(List<Spot> spots) {
        this.spots = spots;
        notifyDataSetChanged();
    }
}
*/

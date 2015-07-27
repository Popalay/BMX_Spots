package com.popalay.bmxspots.fragmets;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.popalay.bmxspots.R;
import com.popalay.bmxspots.Repo;
import com.popalay.bmxspots.activities.MainActivity;
import com.popalay.bmxspots.lists.RecyclerViewAdepterDefault;

public class SurroundingFragment extends Fragment implements Repo.OnLoadAllSpots {

    public static final String TAG = "SurroundingFragment";

    private View rootView;
    private RecyclerViewAdepterDefault adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        ((MainActivity) getActivity()).getRepo().addOnRefreshAllSpotsListeners(this);
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv);
        rv.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        adapter = new RecyclerViewAdepterDefault(((MainActivity) getActivity()).getRepo().getSurroundingSpots());
        rv.setAdapter(adapter);

        return rootView;
    }

    private void update() {
        adapter.updateList(((MainActivity) getActivity()).getRepo().loadSurroundingSpots());
        Log.d(TAG, "update");
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    @Override
    public void onLoadAllSpots() {
        adapter.updateList(((MainActivity) getActivity()).getRepo().getSurroundingSpots());
        Log.d(TAG, "onLoadAllSpots");
    }
}

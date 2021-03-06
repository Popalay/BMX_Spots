package com.popalay.bmxspots.fragmets;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.popalay.bmxspots.R;
import com.popalay.bmxspots.Repo;
import com.popalay.bmxspots.lists.FavoriteAdapter;

public class FavoriteFragment extends Fragment {

    public static final String TAG = "FavoriteFragment";

    private ToMapClickOnFragmentListener listener;

    private View rootView;

    private FavoriteAdapter adapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ToMapClickOnFragmentListener) {
            listener = (ToMapClickOnFragmentListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement ToMapClickOnFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        ListView rv = (ListView) rootView.findViewById(R.id.rv);

        /*rv.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);*/

        adapter = new FavoriteAdapter(getActivity(), Repo::getFavoriteSpots);
        adapter.setOnMapClickListener(listener::toMapClickOnFragment);
        rv.setAdapter(adapter);

        return rootView;
    }
}

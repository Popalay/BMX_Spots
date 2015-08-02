package com.popalay.bmxspots.fragmets;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.popalay.bmxspots.R;
import com.popalay.bmxspots.Repo;
import com.popalay.bmxspots.lists.DefaultAdapter;

public class MyFragment extends Fragment{

    public static final String TAG = "MyFragment";

    private View rootView;

    private DefaultAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        ListView rv = (ListView) rootView.findViewById(R.id.rv);
        /*rv.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);*/

        adapter = new DefaultAdapter(getActivity(), Repo::getMySpots);
        rv.setAdapter(adapter);

        return rootView;
    }
}

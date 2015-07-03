package com.popalay.bmxspots.fragment.mainfragment.tabs;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.popalay.bmxspots.R;

public class MapFragment extends Fragment implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapLoadedCallback {

    private FloatingActionButton fab;

    private MapView mMapView;
    private GoogleMap mMap;

    private Marker newMarker;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_fragment_map, container, false);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpMapIfNeeded();
    }

    @Override
    public void onMapLoaded() {
        fabHide();
        mMap.setMyLocationEnabled(true);//выводим индикатор своего местоположения
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);

        //TODO myLocation
        Location location = mMap.getMyLocation();
        if (location != null) {
            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(myLocation)
                    .zoom(12)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = mMapView.getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setOnMapLoadedCallback(this);
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (newMarker != null) {
            newMarker.remove();
            fabHide();
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (newMarker != null)
            newMarker.remove();
        newMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(latLng.toString())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
        fabShow();

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void fabShow() {
        fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    private void fabHide() {
        fab.animate().translationY(fab.getHeight() + 16).setInterpolator(new AccelerateInterpolator(2)).start();
    }
}

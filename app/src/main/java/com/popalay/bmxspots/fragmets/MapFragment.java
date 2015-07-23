package com.popalay.bmxspots.fragmets;

import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.annimon.stream.Stream;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.popalay.bmxspots.R;
import com.popalay.bmxspots.Repo;
import com.popalay.bmxspots.activities.MainActivity;
import com.popalay.bmxspots.models.Spot;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMapLoadedCallback, Repo.OnLoadAllSpots {

    public static final String TAG = "MapFragment";

    private CoordinatorLayout coordinatorLayout;

    private MapView mMapView;
    private GoogleMap mMap;

    private Marker newMarker;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        ((MainActivity) getActivity()).getRepo().addOnRefreshAllSpotsListeners(this);
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coord);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(fabClick);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();// needed to get the map to display immediately
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        setUpMapIfNeeded();
    }

/*    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_map, menu);
        ImageView refresh = (ImageView) menu.findItem(R.id.refresh).getActionView();
        if (refresh != null) {
            refresh.setImageResource(R.drawable.refresh);
            refresh.setOnClickListener(v -> {
                v.animate().rotationBy(360).setInterpolator(new DecelerateInterpolator(0.5f)).start();
                // create and use new data set
                Log.d(this.toString(), "refresh clicked");
                ((MainActivity) getActivity()).getRepo().loadAllSpots();
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }*/

    @Override
    public void onMapLoaded() {
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);

        Location location = MainActivity.getCurrentLocation();
        if (location != null) {
            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(myLocation)
                    .zoom(15)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        mMap.setMyLocationEnabled(true);//выводим индикатор своего местоположения
    }

    public void updateMarkers() {
        mMap.clear();
        Stream.of(((MainActivity) getActivity()).getRepo().getAllSpots()).forEach(this::addMarker);
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
            newMarker = null;
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (newMarker != null) {
            newMarker.remove();
            newMarker = null;
        }
        newMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
    }

    private FloatingActionButton.OnClickListener fabClick = new FloatingActionButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (newMarker != null) {
                //creating dialog object
                final Dialog d = new Dialog(getActivity());

                //hiding default title bar of dialog
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.setContentView(R.layout.dialog_spot_add);

                Button add = (Button) d.findViewById(R.id.dialog_spot_add_button_ADD);
                Button cancel = (Button) d.findViewById(R.id.dialog_spot_add_button_CANCEL);

                final TextInputLayout title_edit = (TextInputLayout) d.findViewById(R.id.edit_text_title);
                final TextInputLayout description_edit = (TextInputLayout) d.findViewById(R.id.edit_text_description);
                TextInputLayout location_edit = (TextInputLayout) d.findViewById(R.id.edit_text_address);
                final String address = getAddress(newMarker.getPosition());
                location_edit.getEditText().setText(address);

                add.setOnClickListener(v1 -> {
                    String title = title_edit.getEditText().getText().toString();
                    String description = description_edit.getEditText().getText().toString();
                    if (title.length() != 0 && description.length() != 0) {
                        Spot newSpot = new Spot(title, description, ((MainActivity) getActivity()).getRepo().getUser().getUsername(),
                                ((MainActivity) getActivity()).getRepo().getUser().getObjectId(), newMarker.getPosition(), address);
                        ((MainActivity) getActivity()).getRepo().addSpot(newSpot);
                        newSpot.save();
                        addMarker(newSpot);
                        d.dismiss();
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Spot add", Snackbar.LENGTH_SHORT);
                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        snackbar.show();
                    } else {
                        if (title.length() == 0) title_edit.setError("Title cannot be blank");
                        else title_edit.setError("");

                        if (description.length() == 0)
                            description_edit.setError("Description cannot be blank");
                        else description_edit.setError("");
                    }
                });
                cancel.setOnClickListener(v2 -> {
                    d.dismiss(); //to dismiss the dialog box
                });

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(d.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;

                d.show();
                d.getWindow().setAttributes(lp);
            } else {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Point on a map is not choose!", Snackbar.LENGTH_SHORT);
                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorAccent));
                snackbar.show();
            }
        }
    };

    private String getAddress(LatLng position) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(position.latitude, position.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append(" ");
                }
                //sb.append(address.getLocality());
                sb.append(address.getCountryName());
                result = sb.toString();
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable connect to Geocoder " + e);
        }
        return result;
    }

    private void addMarker(Spot spot) {
        spot.setMarker(mMap.addMarker(new MarkerOptions()
                .position(spot.getPosition())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))));
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

    @Override
    public void onLoadAllSpots() {
        updateMarkers();
        Log.d(TAG, "onLoadAllSpots");
    }
}

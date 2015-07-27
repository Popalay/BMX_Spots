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
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.annimon.stream.Optional;
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
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, Repo.OnLoadAllSpots {

    public static final String TAG = "MapFragment";

    private View rootView;
    private CoordinatorLayout coordinatorLayout;
    private View slidingUpPanelLayout;
    private FloatingActionButton fab;

    private MapView mMapView;
    private GoogleMap mMap;

    private Marker newMarker;
    private Spot selectedSpot;

    private boolean slidingPanelExpand;

    protected TextView spotTitle;
    protected TextView spotAuthor;
    protected TextView spotDistance;
    protected TextView spotDescription;

    //private Button toMap;
    protected Button btnFavorite;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_map, container, false);
        ((MainActivity) getActivity()).getRepo().addOnRefreshAllSpotsListeners(this);
        this.coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coord);
        this.slidingUpPanelLayout = rootView.findViewById(R.id.sliding_layout);
        this.fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        this.fab.setOnClickListener(fabClick);
        this.spotTitle = (TextView) rootView.findViewById(R.id.spot_title);
        this.spotAuthor = (TextView) rootView.findViewById(R.id.spot_author);
        this.spotDistance = (TextView) rootView.findViewById(R.id.spot_distance);
        this.spotDescription = (TextView) rootView.findViewById(R.id.spot_description);
        this.btnFavorite = (Button) rootView.findViewById(R.id.card_btn_favorite);
        this.btnFavorite.setOnClickListener(v -> {
            if (selectedSpot != null) {
                if (selectedSpot.isFavorite()) {
                    selectedSpot.intoFavorite();
                    btnFavorite.setText("To favorite");
                } else {
                    selectedSpot.toFavorite();
                    btnFavorite.setText("Into favorite");
                }
            }
        });
        this.mMapView = (MapView) rootView.findViewById(R.id.mapView);
        this.mMapView.onCreate(savedInstanceState);
        this.mMapView.onResume();// needed to get the map to display immediately
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
        // Restoring the markers on configuration changes
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
                mMap.setOnMapClickListener(this);
                mMap.setOnMapLongClickListener(this);
                mMap.setOnMarkerClickListener(this);
            }
        }
        updateMarkers();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (newMarker != null) {
            newMarker.remove();
            newMarker = null;
        }
        if (slidingPanelExpand) {
            narrowSlidingPanel();
            selectedSpot = null;
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (newMarker != null) {
            newMarker.remove();
            newMarker = null;
            selectedSpot = null;
        }
        newMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (slidingPanelExpand) {
            if (showSpotInformation(marker)) {
                marker.showInfoWindow();
            } else {
                narrowSlidingPanel();
            }
        } else {
            if (showSpotInformation(marker)) {
                marker.showInfoWindow();
                expandSlidingPanel();
            }
        }
        return true;
    }

    private boolean showSpotInformation(Marker marker) {
        Optional<Spot> spotOptional = Stream.of(((MainActivity) getActivity()).getRepo().getAllSpots())
                .filter(spot -> spot.getMarker().equals(marker))
                .findFirst();
        if (spotOptional.isPresent()) {
            selectedSpot = spotOptional.get();
            if (selectedSpot != null) {
                spotTitle.setText(selectedSpot.getTitle());
                spotAuthor.setText(selectedSpot.getAuthor());
                spotDistance.setText(selectedSpot.getDistance() + "km");
                spotDescription.setText(selectedSpot.getDescription());
                if (selectedSpot.isFavorite())
                    btnFavorite.setText("Into favorite");
                return true;
            }
        }
        return false;
    }

    private void expandSlidingPanel() {
        if (slidingUpPanelLayout.getVisibility() == View.GONE)
            slidingUpPanelLayout.setVisibility(View.VISIBLE);
        slidingPanelExpand = true;
        slidingUpPanelLayout.animate().translationY(0)
                .setInterpolator(new AccelerateInterpolator()).start();
        fabMove(-slidingUpPanelLayout.getHeight());
    }

    private void narrowSlidingPanel() {
        Log.d(TAG, "narrowSlidingPanel");
        slidingPanelExpand = false;
        slidingUpPanelLayout.animate().translationY(slidingUpPanelLayout.getHeight())
                .setInterpolator(new AccelerateInterpolator()).start();
        fabMove(0);
    }

    private void fabMove(float value) {
        fab.animate().translationY(value)
                .setInterpolator(new AccelerateInterpolator()).start();
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
                .title(spot.getTitle())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))));
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
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

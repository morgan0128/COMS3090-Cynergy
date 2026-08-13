package com.example.synergy.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.example.synergy.sheets.mapEventListSheet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;


import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class eventMapFragment extends Fragment{


    private MapView mapView;

    private  String userDetailString;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        setupOSMDroid();
        View view = inflater.inflate(R.layout.fragment_event_map, container, false);
        Bundle args = getArguments();
        if (args != null) {
          userDetailString = args.getString("userDetails");
        }


        setupMap(view);


//        Prevent ViewPager swipe conflicts
        mapView.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });




        fetchAndDisplayEvents();

        return view;
    }


    private void setupMap(View view){
        //         Initialize map view
        mapView = view.findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);


        GeoPoint ames = new GeoPoint(42.02365, -93.64585);
        mapView.getController().setZoom(18.0);
        mapView.getController().setCenter(ames);
    }

    private void setupOSMDroid(){
        // Configure osmdroid properly (load + cache directory)
        Configuration.getInstance().load(requireContext(),
                PreferenceManager.getDefaultSharedPreferences(requireContext()));
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        Configuration.getInstance().setOsmdroidBasePath(
                new File(requireContext().getCacheDir().getAbsolutePath(), "osmdroid"));
        Configuration.getInstance().setOsmdroidTileCache(
                new File(requireContext().getCacheDir().getAbsolutePath(), "osmdroid/tiles"));

    }

    /**
     * Functions to dictate what happens when we resume, pause and destroy Map
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
            mapView.getController().setCenter(new GeoPoint(42.02365
                    , -93.64585)); // Reset view center
            mapView.invalidate(); //  Force redraw
        }

        fetchAndDisplayEvents();

    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView = null;
    }




    private void fetchAndDisplayEvents() {
        String url = "http://coms-3090-016.class.las.iastate.edu:8080/api/map/node/all";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        handleMapNodePopulation(response);
                    } catch (Exception e) {
                        Log.d("ERROR", e.toString());
                        Toast.makeText(requireContext(), "Error parsing events", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.d("ERROR", error.toString());
                    Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);
    }


    private final Map<Integer, Marker> markerMap = new HashMap<>();
    private void handleMapNodePopulation(JSONArray response) throws JSONException {
        Set<Integer> updatedNodeIds = new HashSet<>();
        for (int i = 0; i < response.length(); i++) {
            JSONObject obj = response.getJSONObject(i);
            int mapNodeId = obj.getInt("map_node_id");
            updatedNodeIds.add(mapNodeId);

            double lat = obj.getDouble("latitude");
            double lon = obj.getDouble("longitude");
            String description = obj.optString("description", "No description");
            JSONArray associatedEvents = obj.getJSONArray("events");

            Marker marker;
            if (markerMap.containsKey(mapNodeId)) {
                marker = markerMap.get(mapNodeId);
                assert marker != null;
                marker.setPosition(new GeoPoint(lat, lon));
                marker.setSnippet(description);
            } else {
                marker = new Marker(mapView);
                marker.setPosition(new GeoPoint(lat, lon));
                marker.setSnippet(description);
                marker.setOnMarkerClickListener((m, mapView1) -> {
                    mapEventListSheet bottomSheet = mapEventListSheet.newInstance(
                            associatedEvents, userDetailString, mapNodeId);
                    bottomSheet.show(getParentFragmentManager(), "mapEventListSheet");
                    return true;
                });
                mapView.getOverlays().add(marker);
                markerMap.put(mapNodeId, marker);
            }
        }

        // Remove markers no longer in response
        markerMap.keySet().removeIf(id -> {
            if (!updatedNodeIds.contains(id)) {
                mapView.getOverlays().remove(markerMap.get(id));
                return true;
            }
            return false;
        });

        mapView.invalidate();

    }

    private void showStatusDialog(String title, String message) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

}

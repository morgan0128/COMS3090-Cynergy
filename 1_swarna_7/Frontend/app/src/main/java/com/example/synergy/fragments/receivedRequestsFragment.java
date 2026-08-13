package com.example.synergy.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.example.synergy.adapters.receivedRequestAdapter;
import com.example.synergy.items.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class receivedRequestsFragment extends Fragment {

    private String userDetailString;
    private receivedRequestAdapter adapter;
    private List<User> friends;

    public receivedRequestsFragment() {
        // Required empty public constructor
    }

    public static receivedRequestsFragment newInstance(String userDetails) {
        receivedRequestsFragment fragment = new receivedRequestsFragment();
        Bundle args = new Bundle();
        args.putString("userDetails", userDetails);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout (create a layout file called fragment_friends_list.xml)
        return inflater.inflate(R.layout.fragment_received_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve data passed to fragment
        if (getArguments() != null) {
            userDetailString = getArguments().getString("userDetails");
        }

        // Initialize views
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        friends = new ArrayList<>();
        adapter = new receivedRequestAdapter(requireContext(), friends, userDetailString);
        recyclerView.setAdapter(adapter);

        try {
            fetchReceivedRequests();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        SearchView searchFriends = view.findViewById(R.id.searchUsers);
        searchFriends.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            /**
             *  Updates the list of Friends when we submit a query and filters
             *  users based on the query
             *
             * @param query the query text that is to be submitted
             *
             * @return true
             */
            @Override
            public boolean onQueryTextSubmit(String query){
                filterUsers(query);
                return true;
            }

            /**
             * Updates the list of Friends as we are writing a query so that
             * the friend list is being filtered as we type in the search bar.
             *
             * @param newText the new content of the query text field.
             *
             * @return true
             */
            @Override
            public boolean onQueryTextChange(String newText){
                filterUsers(newText);
                return true;
            }
        });

    }

    /**
     * Based on a query text we filter the list of Friends so that it only
     * shows users with the matching name
     * shows users with the matching name
     *
     * @param text : A queried text we get when we type into the search bar
     */
    private void filterUsers(String text){
        List<User> filteredList = new ArrayList<>();
        for (User user: friends){
            if (user.getName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(user);
            }
        }
        adapter.updateList(filteredList);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            fetchReceivedRequests();
        } catch (JSONException e) {
            Log.d("ERROR", e.toString());
        }
    }

    private void fetchReceivedRequests() throws JSONException {
        JSONObject userDetails = new JSONObject(userDetailString);
        int userId = userDetails.getInt("id");
        String server_url ="http://coms-3090-016.class.las.iastate.edu:8080/api/friends/pending/"
                + userId + "/received";


        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                server_url,
                null,
                this::handleFetchSuccess,
                error -> {
                    Log.d("ERROR", error.toString());
                    Toast.makeText(requireContext(), "Failed to load friends", Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void handleFetchSuccess(JSONArray response){
        friends.clear();
        // response is a JsonArray so we loop through this
        // array to get the Friend JsonObject
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject object = response.getJSONObject(i);
                JSONObject friendObject = object.getJSONObject("sender");
                friends.add(new User(friendObject));
            } catch (JSONException e) {
                Log.d("ERROR", e.toString());
            }
        }
        adapter.notifyDataSetChanged();
    }
}
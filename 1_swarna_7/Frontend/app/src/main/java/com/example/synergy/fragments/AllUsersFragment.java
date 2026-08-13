package com.example.synergy.fragments;

import android.annotation.SuppressLint;
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
import com.example.synergy.VolleySingleton;
import com.example.synergy.R;
import com.example.synergy.adapters.UserAdapter;
import com.example.synergy.items.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllUsersFragment extends Fragment {

    private String userDetailString;
    public UserAdapter adapter;
    public List<User> users;
    public RecyclerView recyclerView;
    private SearchView searchUsers;
    private final Set<Integer> excludedIds = new HashSet<>();

    public AllUsersFragment() {
        // Required empty public constructor
    }

    public static AllUsersFragment newInstance(String userDetails) {
        AllUsersFragment fragment = new AllUsersFragment();
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
        return inflater.inflate(R.layout.fragment_all_user, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve data passed to fragment
        if (getArguments() != null) {
            userDetailString = getArguments().getString("userDetails");
        }
        assert userDetailString != null;
        Log.d("string", userDetailString);

        initUI(view);
        setupAdapter();

        setupSearchBar(view);

        try {
            fetchExcludedUsersAndfetchAllUsers();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    private void setupSearchBar(View view){
        searchUsers = view.findViewById(R.id.searchUsers);
        searchUsers.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query){
                filterUsers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText){
                filterUsers(newText);
                return true;
            }
        });
    }
    private void setupAdapter(){
        users = new ArrayList<>();
        adapter = new UserAdapter(requireContext(), users, userDetailString);
        adapter.setOnUserActionListener(friendId -> {
            // Instantly remove from UI
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getId() == friendId) {
                    users.remove(i);
                    adapter.notifyItemRemoved(i);
                    break;
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void initUI(View view){
        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void filterUsers(String text){
        List<User> filteredList = new ArrayList<>();
        for (User user: users){
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
            fetchExcludedUsersAndfetchAllUsers();
        } catch (JSONException e) {
            Log.d("ERROR", e.toString());
        }
    }

    private void fetchExcludedUsersAndfetchAllUsers() throws JSONException {
        JSONObject userDetail = new JSONObject(userDetailString);
        int userId = userDetail.getInt("id");

        excludedIds.add(userId);

        String friendurl = "http://coms-3090-016.class.las.iastate.edu:8080/api/friends/" + userId;


        JsonArrayRequest friendReq = new JsonArrayRequest(Request.Method.GET, friendurl, null,
                response -> {
                    handleExcludingUsersaAndFriends(response, userId);
                },
                volleyError -> fetchAllUsers()
        );

        VolleySingleton.getInstance(getContext()).addToRequestQueue(friendReq);

    }


    private void handleExcludingUsersaAndFriends(JSONArray response, int userId){

        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject friendObj = response.getJSONObject(i).getJSONObject("friend");
                excludedIds.add(friendObj.getInt("friendId"));
            } catch (JSONException e) {
                Log.d("ERROR", e.toString());
            }
        }

        fetchPendingFriendRequests(userId);

    }

    private void handleExcludingpendingRequests(JSONArray pendingResp){
        for (int i = 0; i < pendingResp.length(); i++) {
            try {
                JSONObject sent = (JSONObject) pendingResp.get(i);
                JSONObject pendingPerson = sent.getJSONObject("friend");
                excludedIds.add(pendingPerson.getInt("id"));
            } catch (JSONException e) {
                Log.d("ERROR", e.toString());
            }
        }
    }
    private void fetchPendingFriendRequests(int userId){
        String pendingURL = "http://coms-3090-016.class.las.iastate.edu:8080/api/friends/pending/"
                + userId + "/sent";
        JsonArrayRequest pendingReq = new JsonArrayRequest(Request.Method.GET, pendingURL, null,
                pendingResp -> {
                    handleExcludingpendingRequests(pendingResp);
                    fetchAllUsers();
                },
                volleyError -> fetchAllUsers()
        );
        VolleySingleton.getInstance(getContext()).addToRequestQueue(pendingReq);
    }

    private void fetchAllUsers() {
        String url = "https://0010ad92-2045-4d77-8aa1-a71ce691fb82.mock.pstmn.io/getFriends";
        String server_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/accounts";


        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                server_url,
                null,
                this::setupAllUsers,
                error -> {
                    Log.d("ERROR", error.toString());
                    Toast.makeText(requireContext(), "Failed to load friends", Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);

    }

   @SuppressLint("NotifyDataSetChanged")
   private void setupAllUsers(JSONArray response){
       users.clear();
       for (int i = 0; i < response.length(); i++) {
           try {
               JSONObject userDetail = new JSONObject(userDetailString);
               JSONObject friendObject = response.getJSONObject(i);
               if (userDetail.getInt("id") == friendObject.getInt("id")
                       || excludedIds.contains(friendObject.getInt("id"))){
                   continue;
               }
               users.add(new User(friendObject));
           } catch (JSONException e) {
               Log.d("ERROR", e.toString());
           }
       }
       adapter.notifyDataSetChanged();
   }

}
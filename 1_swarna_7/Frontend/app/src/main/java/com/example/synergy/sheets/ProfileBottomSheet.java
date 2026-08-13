package com.example.synergy.sheets;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileBottomSheet extends BottomSheetDialogFragment {

    private EditText name;
    private EditText age;
    private EditText bio;
    private Spinner gender;
    private Button createProfileBtn;

    private EditText interestInput;
    private ChipGroup interestsGroup;
    private final ArrayList<String> interests = new ArrayList<>();

    private static final String POST_URL =
            "http://coms-3090-016.class.las.iastate.edu:8080/api/profile/create/";

    private int userId;

    public static ProfileBottomSheet newInstance(int userId) {
        ProfileBottomSheet fragment = new ProfileBottomSheet();
        Bundle args = new Bundle();
        args.putInt("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt("userId", -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.bottom_sheet_profile, container, false);

        name = root.findViewById(R.id.createPName);
        age = root.findViewById(R.id.createPAge);
        bio = root.findViewById(R.id.createPBio);
        gender = root.findViewById(R.id.createPGender);
        createProfileBtn = root.findViewById(R.id.createProfileButton);

        interestInput = root.findViewById(R.id.createPInterestInput);
        interestsGroup = root.findViewById(R.id.createPInterestsGroup);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.gender_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);

        interestInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                addInterestFromInput();
                return true;
            }
            return false;
        });

        createProfileBtn.setOnClickListener(v ->
                makeServerReq(
                        name.getText().toString(),
                        age.getText().toString(),
                        bio.getText().toString(),
                        gender.getSelectedItem() != null
                                ? gender.getSelectedItem().toString()
                                : ""
                )
        );

        return root;
    }

    private void addInterestFromInput() {
        String text = interestInput.getText().toString().trim();
        if (text.isEmpty()) return;
        if (interests.contains(text)) {
            interestInput.setText("");
            return;
        }
        interests.add(text);
        addChipForInterest(text);
        interestInput.setText("");
    }

    private void addChipForInterest(String interest) {
        Chip chip = new Chip(requireContext());
        chip.setText(interest);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            interestsGroup.removeView(chip);
            interests.remove(interest);
        });
        interestsGroup.addView(chip);
    }

    private void makeServerReq(String nameStr, String ageStr, String bioStr, String genderStr) {
        if (userId <= 0) {
            showStatusDialog("Error", "Invalid user ID.", false);
            return;
        }

        if (nameStr.isEmpty() || ageStr.isEmpty() || bioStr.isEmpty() || genderStr.isEmpty()) {
            showStatusDialog("Error", "All fields are required.", false);
            return;
        }

        int ageVal;
        try {
            ageVal = Integer.parseInt(ageStr);
            if (ageVal <= 0) {
                showStatusDialog("Error", "Please enter a valid age.", false);
                return;
            }
        } catch (NumberFormatException e) {
            showStatusDialog("Error", "Age must be a number.", false);
            return;
        }

        try {
            JSONObject signupData = new JSONObject();
            signupData.put("profileName", nameStr);
            signupData.put("age", ageVal);
            signupData.put("profileBio", bioStr);
            signupData.put("gender", genderStr);
            signupData.put("interests", new JSONArray(interests));

            String completeUrl = POST_URL + userId;

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    completeUrl,
                    signupData,
                    response -> {
                        Log.d("ProfileBottomSheet", "Create profile: " + response);
                        showStatusDialog("Success", "Profile creation successful!", true);
                        clearForm();
                    },
                    error -> {
                        Log.e("ProfileBottomSheet", "Create error: " + error);
                        showStatusDialog("Error", "Profile creation failed. Please try again.", false);
                        clearForm();
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            VolleySingleton.getInstance(requireContext()).addToRequestQueue(jsonRequest);

        } catch (JSONException e) {
            e.printStackTrace();
            showStatusDialog("Error", "Failed to create request.", false);
            clearForm();
        }
    }

    private void showStatusDialog(String title, String message, boolean dismissOnOk) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    if (dismissOnOk) dismiss();
                })
                .show();
    }

    private void clearForm() {
        name.setText("");
        age.setText("");
        bio.setText("");
        gender.setSelection(0);

        interests.clear();
        interestsGroup.removeAllViews();
        interestInput.setText("");
    }
}


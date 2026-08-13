package com.example.synergy.sheets;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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

public class EditProfileBottomSheet extends BottomSheetDialogFragment {

    private EditText edit_name;
    private EditText edit_age;
    private EditText edit_bio;
    private Spinner edit_gender;
    private Button EditProfileBtn;

    private EditText edit_interestInput;
    private ChipGroup edit_interestsGroup;
    private final ArrayList<String> interests = new ArrayList<>();

    private static final String PUT_URL =
            "http://coms-3090-016.class.las.iastate.edu:8080/api/profile/edit/profile/";

    private static final String ARG_NAME = "name";
    private static final String ARG_AGE = "age";
    private static final String ARG_BIO = "bio";
    private static final String ARG_GENDER = "gender";
    private static final String ARG_PROFILE_ID = "profileId";
    private static final String ARG_INTERESTS = "interests";

    private String name, age, bio, gender;
    private int profileId;

    public interface OnProfileUpdatedListener {
        void onProfileUpdated(String name, String age, String bio, String gender);
    }

    private OnProfileUpdatedListener callback;

    public void setOnProfileUpdatedListener(OnProfileUpdatedListener listener) {
        this.callback = listener;
    }

    public static EditProfileBottomSheet newInstance(String name,
                                                     String age,
                                                     String bio,
                                                     String gender,
                                                     String profileId,
                                                     ArrayList<String> interests) {
        EditProfileBottomSheet fragment = new EditProfileBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_AGE, age);
        args.putString(ARG_BIO, bio);
        args.putString(ARG_GENDER, gender);
        args.putString(ARG_PROFILE_ID, profileId);
        args.putStringArrayList(ARG_INTERESTS, interests);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_NAME);
            age = getArguments().getString(ARG_AGE);
            bio = getArguments().getString(ARG_BIO);
            gender = getArguments().getString(ARG_GENDER);
            try {
                profileId = Integer.parseInt(getArguments().getString(ARG_PROFILE_ID));
            } catch (NumberFormatException e) {
                profileId = -1;
            }
            ArrayList<String> argInterests = getArguments().getStringArrayList(ARG_INTERESTS);
            if (argInterests != null) {
                interests.clear();
                interests.addAll(argInterests);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.bottom_sheet_edit_profile, container, false);

        edit_name = root.findViewById(R.id.editPName);
        edit_age = root.findViewById(R.id.editPAge);
        edit_bio = root.findViewById(R.id.editPBio);
        edit_gender = root.findViewById(R.id.editPGender);
        EditProfileBtn = root.findViewById(R.id.editProfileButton);

        edit_interestInput = root.findViewById(R.id.editPInterestInput);
        edit_interestsGroup = root.findViewById(R.id.editPInterestsGroup);

        edit_name.setText(name);
        edit_age.setText(age);
        edit_bio.setText(bio);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.gender_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edit_gender.setAdapter(adapter);

        if (gender != null) {
            int spinnerPosition = adapter.getPosition(gender);
            if (spinnerPosition >= 0) {
                edit_gender.setSelection(spinnerPosition);
            }
        }

        // existing interests -> chips
        for (String s : interests) {
            addChipForInterest(s);
        }

        // Add interest on IME action
        edit_interestInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                addInterestFromInput();
                return true;
            }
            return false;
        });

        EditProfileBtn.setOnClickListener(v ->
                makeServerReq(
                        edit_name.getText().toString(),
                        edit_age.getText().toString(),
                        edit_bio.getText().toString(),
                        edit_gender.getSelectedItem() != null
                                ? edit_gender.getSelectedItem().toString()
                                : ""
                )
        );

        return root;
    }

    private void addInterestFromInput() {
        String text = edit_interestInput.getText().toString().trim();
        if (text.isEmpty()) return;
        if (interests.contains(text)) {
            edit_interestInput.setText("");
            return;
        }
        interests.add(text);
        addChipForInterest(text);
        edit_interestInput.setText("");
    }

    private void addChipForInterest(String interest) {
        Chip chip = new Chip(requireContext());
        chip.setText(interest);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            edit_interestsGroup.removeView(chip);
            interests.remove(interest);
        });
        edit_interestsGroup.addView(chip);
    }

    private void makeServerReq(String nameStr, String ageStr, String bioStr, String genderStr) {
        if (profileId <= 0) {
            showStatusDialog("Error", "Invalid profile ID.", false);
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
            JSONObject body = new JSONObject();
            body.put("profileName", nameStr);
            body.put("age", ageVal);
            body.put("profileBio", bioStr);
            body.put("gender", genderStr);
            body.put("interests", new JSONArray(interests));

            String completeUrl = PUT_URL + profileId;

            Log.d("url",completeUrl);
            Log.d("body", body.toString());

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.PUT,
                    completeUrl,
                    body,
                    response -> {
                        Log.d("EditProfileSheet", "Update profile: " + response);
                        showStatusDialog("Success", "Profile updated successfully!", true);
                        clearForm();

                        if (callback != null) {
                            callback.onProfileUpdated(nameStr, String.valueOf(ageVal), bioStr, genderStr);
                        }
                        dismiss();
                    },
                    error -> {
                        Log.e("EditProfileSheet", "Update error: " + error);
                        showStatusDialog("Error", "Profile update failed. Please try again.", false);
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
        edit_name.setText("");
        edit_age.setText("");
        edit_bio.setText("");
        edit_gender.setSelection(0);

        interests.clear();
        if (edit_interestsGroup != null) {
            edit_interestsGroup.removeAllViews();
        }
        if (edit_interestInput != null) {
            edit_interestInput.setText("");
        }
    }
}


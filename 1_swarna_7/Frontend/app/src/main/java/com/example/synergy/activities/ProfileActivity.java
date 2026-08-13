package com.example.synergy.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.example.synergy.sheets.EditProfileBottomSheet;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String DELETE_URL =
            "http://coms-3090-016.class.las.iastate.edu:8080/api/profile/delete";
    private static final String BASE_URL =
            "http://coms-3090-016.class.las.iastate.edu:8080";
    private static final String PFP_URL = BASE_URL + "/api/profile/";

    private String profileResponseJson;
    private String email;
    private int userId = -1;
    private int profileId = -1;

    private TextView nameText, ageText, genderText, bioText, interestsText;
    private Button editProfileBtn, deleteProfileBtn;
    private ImageButton backProfileBtn;
    private ShapeableImageView profilePicture;
    private ImageButton changePhotoButton, deletePhotoButton;

    private final ArrayList<String> interestsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            profileResponseJson = extras.getString("response");
            email = extras.getString("email");
            userId = extras.getInt("userId", -1);
        }

        initViews();
        parseProfileDto(profileResponseJson);

        if (userId > 0) {
            loadProfilePicture(userId);
        }

        editProfileBtn.setOnClickListener(v -> openEditProfileSheet());

        deleteProfileBtn.setOnClickListener(v -> confirmAndDeleteProfile());

        backProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            intent.putExtra("response", profileResponseJson);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        });

        changePhotoButton.setOnClickListener(v -> showAvatarChooser());

        deletePhotoButton.setOnClickListener(v -> confirmAndDeleteProfilePicture());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        nameText = findViewById(R.id.profileName);
        ageText = findViewById(R.id.profileAge);
        bioText = findViewById(R.id.profileBio);
        genderText = findViewById(R.id.profileGender);
        interestsText = findViewById(R.id.profileInterests);

        editProfileBtn = findViewById(R.id.editProfileButton);
        deleteProfileBtn = findViewById(R.id.deleteProfileButton);
        backProfileBtn = findViewById(R.id.backButton);

        profilePicture = findViewById(R.id.profilePicture);
        changePhotoButton = findViewById(R.id.changePhotoButton);
        deletePhotoButton = findViewById(R.id.deletePhotoButton);
    }

    private void parseProfileDto(String json) {
        if (json == null) return;

        try {
            JSONObject profileObj = new JSONObject(json);
            Log.d("profile", profileObj.toString());

            profileId = profileObj.optInt("profileId", -1);

            String profileName = profileObj.optString("profileName", "");
            String profileBio = profileObj.optString("profileBio", "");
            int age = profileObj.optInt("age", 0);
            String gender = profileObj.optString("gender", "");

            nameText.setText(profileName);
            bioText.setText(profileBio);
            ageText.setText("Age: " + (age > 0 ? age : 0));
            genderText.setText("Gender: " + gender);

            interestsList.clear();
            JSONArray interestsArray = profileObj.optJSONArray("interests");
            if (interestsArray != null) {
                for (int i = 0; i < interestsArray.length(); i++) {
                    interestsList.add(interestsArray.optString(i));
                }
            }

            if (interestsList.isEmpty()) {
                interestsText.setText("Interests: –");
            } else {
                String joined = android.text.TextUtils.join(", ", interestsList);
                interestsText.setText("Interests: " + joined);
            }

            JSONObject userObj = profileObj.optJSONObject("user");
            if (userObj != null) {
                if (userId <= 0) {
                    userId = userObj.optInt("id", -1);
                }
                if (email == null || email.isEmpty()) {
                    email = userObj.optString("emailId", email);
                }
            }

        } catch (JSONException e) {
            Log.e("ProfileActivity", "parse error", e);
        }
    }

    private void openEditProfileSheet() {
        if (profileId <= 0) {
            showStatusDialog("Error", "Invalid profile ID.", false);
            return;
        }

        EditProfileBottomSheet fragment = EditProfileBottomSheet.newInstance(
                nameText.getText().toString(),
                ageText.getText().toString().replace("Age: ", ""),
                bioText.getText().toString(),
                genderText.getText().toString().replace("Gender: ", ""),
                String.valueOf(profileId),
                new ArrayList<>(interestsList)
        );

        fragment.setOnProfileUpdatedListener((newName, newAge, newBio, newGender) -> {
            nameText.setText(newName);
            ageText.setText("Age: " + newAge);
            bioText.setText(newBio);
            genderText.setText("Gender: " + newGender);
        });

        fragment.show(getSupportFragmentManager(), "EditProfileFragment");
    }

    private void confirmAndDeleteProfile() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this profile?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", (dialog, which) -> deleteProfile())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteProfile() {
        if (email == null || email.isEmpty()) {
            showStatusDialog("Error", "Email not available.", false);
            return;
        }

        Log.d("email",email);

        JSONObject body = new JSONObject();
        try {
            body.put("emailId", email);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest deleteRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                DELETE_URL,
                body,
                response -> {
                    showStatusDialog("Success", "Profile deleted successfully!", true);
                },
                error -> {
                    Log.e("DeleteProfile", error.toString());
                    showStatusDialog("Error", "Profile deletion failed.", false);
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(deleteRequest);
    }

    private void loadProfilePicture(int userId) {
        String url = PFP_URL + userId + "/picture";

        StringRequest getRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        if (response != null && !response.isEmpty()) {
                            byte[] decodedBytes = Base64.decode(response, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                            if (bitmap != null) {
                                profilePicture.setImageBitmap(bitmap);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("PFP Decode", "decode error", e);
                    }
                },
                error -> Log.e("PFP Load", "error: " + error)
        );

        VolleySingleton.getInstance(this).addToRequestQueue(getRequest);
    }

    private void uploadProfilePicture(Bitmap bitmap) {
        if (userId <= 0) {
            showStatusDialog("Error", "Invalid user ID.", false);
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] imageBytes = baos.toByteArray();
        String base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        String url = PFP_URL + userId + "/picture";

        JSONObject body = new JSONObject();
        try {
            body.put("image", base64Image);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                response -> showStatusDialog("Success", "Profile picture updated!", false),
                error -> {
                    Log.e("PFP Upload Error", error.toString());
                    showStatusDialog("Error", "Failed to upload profile picture.", false);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }


    private void confirmAndDeleteProfilePicture() {
        new AlertDialog.Builder(this)
                .setTitle("Remove Picture")
                .setMessage("Do you want to remove your profile picture?")
                .setPositiveButton("Yes", (dialog, which) -> deleteProfilePicture())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteProfilePicture() {
        if (userId <= 0) {
            showStatusDialog("Error", "Invalid user ID.", false);
            return;
        }

        String url = PFP_URL + userId + "/picture";

        StringRequest deleteRequest = new StringRequest(
                Request.Method.DELETE,
                url,
                response -> {
                    profilePicture.setImageResource(R.drawable.lion);
                    showStatusDialog("Success", "Profile picture removed.", false);
                },
                error -> showStatusDialog("Error", "Failed to delete profile picture.", false)
        );

        VolleySingleton.getInstance(this).addToRequestQueue(deleteRequest);
    }

    private void showAvatarChooser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        android.view.View view = getLayoutInflater().inflate(R.layout.dialog_avatar_chooser, null);
        builder.setView(view);

        ImageView avatarLion = view.findViewById(R.id.avatarLion);
        ImageView avatarProfile1 = view.findViewById(R.id.profile1);
        ImageView avatarProfile2 = view.findViewById(R.id.profile2);
        ImageView avatarProfile3 = view.findViewById(R.id.profile3);
        ImageView avatarProfile4 = view.findViewById(R.id.profile4);
        ImageView avatarProfile5 = view.findViewById(R.id.profile5);

        AlertDialog dialog = builder.create();

        android.view.View.OnClickListener avatarClickListener = v -> {
            int resId;

            if (v == avatarLion) {
                resId = R.drawable.lion;
            } else if (v == avatarProfile1) {
                resId = R.drawable.profile_1;
            } else if (v == avatarProfile2) {
                resId = R.drawable.profile_2;
            } else if (v == avatarProfile3) {
                resId = R.drawable.profile_3;
            } else if (v == avatarProfile4) {
                resId = R.drawable.profile_4;
            } else {
                resId = R.drawable.profile_5;
            }

            Bitmap bmp = BitmapFactory.decodeResource(getResources(), resId);
            Bitmap scaled = Bitmap.createScaledBitmap(bmp, 512, 512, true);

            profilePicture.setImageBitmap(scaled);
            uploadProfilePicture(scaled);

            dialog.dismiss();
        };

        avatarLion.setOnClickListener(avatarClickListener);
        avatarProfile1.setOnClickListener(avatarClickListener);
        avatarProfile2.setOnClickListener(avatarClickListener);
        avatarProfile3.setOnClickListener(avatarClickListener);
        avatarProfile4.setOnClickListener(avatarClickListener);
        avatarProfile5.setOnClickListener(avatarClickListener);

        dialog.show();
    }

    private void showStatusDialog(String title, String message, boolean dismissOnOk) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    if (dismissOnOk) {
                        finish();
                    }
                })
                .show();
    }
}


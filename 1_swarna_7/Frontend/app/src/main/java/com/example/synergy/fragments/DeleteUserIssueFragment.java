package com.example.synergy.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.example.synergy.items.TicketApi;

public class DeleteUserIssueFragment extends Fragment {

    private static final String ARG_USER_ID = "userId"; // current user if needed later

    private EditText targetUserIdInput;
    private EditText descriptionInput;
    private Button   submitButton;

    private int currentUserId = -1;

    public static DeleteUserIssueFragment newInstance(int userId) {
        DeleteUserIssueFragment fragment = new DeleteUserIssueFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_delete_user_issue_form, container, false);

        targetUserIdInput = view.findViewById(R.id.targetUserIdInput);
        descriptionInput  = view.findViewById(R.id.descriptionInput);
        submitButton      = view.findViewById(R.id.submitDeleteUserIssueBtn);

        if (getArguments() != null) {
            currentUserId = getArguments().getInt(ARG_USER_ID, -1);
        }

        submitButton.setOnClickListener(v -> submitDeleteUserIssue());

        return view;
    }

    private void submitDeleteUserIssue() {
        String idText = targetUserIdInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        if (TextUtils.isEmpty(idText)) {
            Toast.makeText(requireContext(), "Please enter the user id to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        int userIdToDelete;
        try {
            userIdToDelete = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid user id", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = String.format(TicketApi.POST_DELETE_USER_NO_ADMIN, userIdToDelete);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Toast.makeText(requireContext(), "Delete user request sent", Toast.LENGTH_SHORT).show();
                    clearFields();
                },
                error -> Toast.makeText(requireContext(), "Failed: " + error.toString(), Toast.LENGTH_SHORT).show()
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void clearFields() {
        targetUserIdInput.setText("");
        descriptionInput.setText("");
    }
}

package com.example.homedoctor;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {

    TextView nameText, emailText, phoneText, courseText;
    EditText editName, editPhone, editCourse;
    Button updateButton, logoutButton;
    ProgressDialog progressDialog;
    AsyncHttpClient client;
    String PROFILE_URL = "http://192.168.88.185/home_doctor_api/profile.php"; // Change to your actual URL
    String UPDATE_URL = "http://192.168.88.185/home_doctor_api/update_profile.php"; // Change to your actual URL
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameText = findViewById(R.id.name_text);
        emailText = findViewById(R.id.email_text);
        phoneText = findViewById(R.id.phone_text);
        courseText = findViewById(R.id.course_text);
        editName = findViewById(R.id.edit_name);
        editPhone = findViewById(R.id.edit_phone);
        editCourse = findViewById(R.id.edit_course);
        updateButton = findViewById(R.id.update_button);
        logoutButton = findViewById(R.id.logout_button);

        client = new AsyncHttpClient();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading profile...");

        userEmail = getIntent().getStringExtra("email");

        loadProfile();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadProfile() {
        progressDialog.show();
        RequestParams params = new RequestParams();
        params.put("email", userEmail);

        client.post(PROFILE_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                progressDialog.dismiss();
                String[] data = response.split("\\|");

                if (data.length == 4) {
                    nameText.setText(data[0]);
                    emailText.setText(data[1]);
                    phoneText.setText(data[2]);
                    courseText.setText(data[3]);

                    editName.setText(data[0]);
                    editPhone.setText(data[2]);
                    editCourse.setText(data[3]);
                } else {
                    Toast.makeText(ProfileActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        progressDialog.setMessage("Updating profile...");
        progressDialog.show();

        RequestParams params = new RequestParams();
        params.put("email", userEmail);
        params.put("name", editName.getText().toString().trim());
        params.put("phone", editPhone.getText().toString().trim());
        params.put("course", editCourse.getText().toString().trim());

        client.post(UPDATE_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                loadProfile();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

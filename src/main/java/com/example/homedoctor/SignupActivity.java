package com.example.homedoctor;


import android.annotation.SuppressLint;
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

public class SignupActivity extends AppCompatActivity {

    EditText name, email, phone, course, password;
    Button signupButton;
    TextView loginLink;
    ProgressDialog progressDialog;
    AsyncHttpClient client;
    String SIGNUP_URL = "http://192.168.88.185/home_doctor_api/signup.php"; // Change to your actual URL

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        course = findViewById(R.id.course);
        password = findViewById(R.id.password);
        signupButton = findViewById(R.id.signupbt);
        loginLink = findViewById(R.id.login);
        client = new AsyncHttpClient();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing up...");

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void registerUser() {
        String userName = name.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPhone = phone.getText().toString().trim();
        String userCourse = course.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (userName.isEmpty() || userEmail.isEmpty() || userPhone.isEmpty() || userCourse.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        RequestParams params = new RequestParams();
        params.put("name", userName);
        params.put("email", userEmail);
        params.put("phone", userPhone);
        params.put("course", userCourse);
        params.put("password", userPassword);

        client.post(SIGNUP_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                progressDialog.dismiss();

                if (response.contains("success")) {
                    Toast.makeText(SignupActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(SignupActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(SignupActivity.this, "Signup failed. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

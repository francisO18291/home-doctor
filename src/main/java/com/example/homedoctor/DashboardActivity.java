package com.example.homedoctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class DashboardActivity extends AppCompatActivity {

    TextView welcomeText;
    Button btnDiagnosis, btnRecommendations, btnProfile, btnLogout;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        welcomeText = findViewById(R.id.welcome_text);
        btnDiagnosis = findViewById(R.id.btn_diagnosis);
        btnRecommendations = findViewById(R.id.btn_recommendations);
        btnProfile = findViewById(R.id.btn_profile);
        btnLogout = findViewById(R.id.btn_logout);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            logoutUser();
        }

        HashMap<String, String> user = sessionManager.getUserDetails();
        String name = user.get("name");
        welcomeText.setText("Welcome, " + name + "!");

        // Check if payment is valid
        long lastPaymentTime = sessionManager.getLastPaymentTime();
        long currentTime = System.currentTimeMillis();
        long oneDay = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

        if ((currentTime - lastPaymentTime) > oneDay) {
            Toast.makeText(this, "Payment expired. Please pay to continue.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DashboardActivity.this, PaymentActivity.class);
            startActivity(intent);
            finish();
        }

        btnDiagnosis.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, DiagnosisActivity.class);
            startActivity(intent);
        });

        btnRecommendations.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, RecommendationsActivity.class);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> logoutUser());
    }

    private void logoutUser() {
        sessionManager.logoutUser();
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

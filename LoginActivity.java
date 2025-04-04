package com.example.homedoctor;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginButton;
    TextView signupLink;
    ProgressDialog progressDialog;
    AsyncHttpClient client;
    String LOGIN_URL = "http://192.168.88.185/home_doctor_api/login.php";

    // Session Manager
    SessionManager sessionManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        signupLink = findViewById(R.id.signup_link);
        client = new AsyncHttpClient();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");

        // Initialize Session Manager
        sessionManager = new SessionManager(this);

        // If user is already logged in, redirect to Dashboard
        if (sessionManager.isLoggedIn()) {
            long lastPaymentTime = sessionManager.getLastPaymentTime();
            long currentTime = System.currentTimeMillis();
            long hoursSinceLastPayment = (currentTime - lastPaymentTime) / (1000 * 60 * 60);

            if (hoursSinceLastPayment < 24) {
                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                finish();
            } else {
                startActivity(new Intent(LoginActivity.this, PaymentActivity.class));
                finish();
            }
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
    }

    private void loginUser() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        RequestParams params = new RequestParams();
        params.put("email", userEmail);
        params.put("password", userPassword);

        client.post(LOGIN_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                progressDialog.dismiss();

                if (response.contains("success")) {
                    String[] userData = response.split("\\|");
                    String name = userData[1];
                    String phone = userData[2];
                    String course = userData[3];
                    long lastPaymentTime = Long.parseLong(userData[4]);
                    sessionManager.createLoginSession(name, userEmail, phone, course, lastPaymentTime);

                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                } else if (response.contains("payment_required")) {
                    startActivity(new Intent(LoginActivity.this, PaymentActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Login failed. Try again.", Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}

package com.example.homedoctor;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class RecommendationsActivity extends AppCompatActivity {

    TextView recommendationsText, medicationsText;
    Button fetchRecommendationsButton;
    ProgressDialog progressDialog;
    AsyncHttpClient client;
    String RECOMMENDATION_URL = "http://192.168.88.185/home_doctor_api/recommendation.php"; // Change to your actual URL
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        recommendationsText = findViewById(R.id.recommendations_text);
        medicationsText = findViewById(R.id.medications_text);
        fetchRecommendationsButton = findViewById(R.id.fetch_recommendations_button);
        client = new AsyncHttpClient();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching recommendations...");

        userEmail = getIntent().getStringExtra("email");

        fetchRecommendationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchRecommendations();
            }
        });
    }

    private void fetchRecommendations() {
        progressDialog.show();
        RequestParams params = new RequestParams();
        params.put("email", userEmail);

        client.post(RECOMMENDATION_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                progressDialog.dismiss();

                // Splitting response into recommendations and medications
                String[] parts = response.split("\\|");
                if (parts.length == 2) {
                    recommendationsText.setText(parts[0]);
                    medicationsText.setText(parts[1]);
                } else {
                    Toast.makeText(RecommendationsActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(RecommendationsActivity.this, "Failed to get recommendations", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

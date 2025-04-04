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

public class DiagnosisActivity extends AppCompatActivity {

    EditText symptomsInput;
    Button diagnoseButton;
    TextView diagnosisResult;
    ProgressDialog progressDialog;
    AsyncHttpClient client;
    String DIAGNOSIS_URL = "http://192.168.88.185/home_doctor_api/diagnosis.php"; // Change to your actual URL
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis);

        symptomsInput = findViewById(R.id.symptoms);
        diagnoseButton = findViewById(R.id.diagnose_button);
        diagnosisResult = findViewById(R.id.diagnosis_result);
        client = new AsyncHttpClient();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Analyzing symptoms...");

        userEmail = getIntent().getStringExtra("email");

        diagnoseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performDiagnosis();
            }
        });
    }

    private void performDiagnosis() {
        String symptoms = symptomsInput.getText().toString().trim();

        if (symptoms.isEmpty()) {
            Toast.makeText(this, "Enter symptoms", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        RequestParams params = new RequestParams();
        params.put("email", userEmail);
        params.put("symptoms", symptoms);

        client.post(DIAGNOSIS_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                progressDialog.dismiss();
                diagnosisResult.setText(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(DiagnosisActivity.this, "Diagnosis failed. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

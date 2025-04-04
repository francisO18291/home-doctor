package com.example.homedoctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class PaymentActivity extends AppCompatActivity {

    EditText transactionId;
    Button payButton;
    ProgressDialog progressDialog;
    AsyncHttpClient client;
    String PAYMENT_URL = "http://192.168.88.185/home_doctor_api/payment.php"; // Change to your actual URL
    String userEmail;

    // Session Manager
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        transactionId = findViewById(R.id.transaction_id);
        payButton = findViewById(R.id.pay_button);
        client = new AsyncHttpClient();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verifying payment...");

        userEmail = getIntent().getStringExtra("email");

        // Initialize Session Manager
        sessionManager = new SessionManager(this);

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPayment();
            }
        });
    }

    private void verifyPayment() {
        String mpesaTransactionId = transactionId.getText().toString().trim();

        if (mpesaTransactionId.isEmpty()) {
            Toast.makeText(this, "Enter Mpesa Transaction ID", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        RequestParams params = new RequestParams();
        params.put("email", userEmail);
        params.put("transaction_id", mpesaTransactionId);

        client.post(PAYMENT_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                progressDialog.dismiss();

                if (response.contains("success")) {
                    Toast.makeText(PaymentActivity.this, "Payment verified", Toast.LENGTH_SHORT).show();

                    // Update last payment time in session
                    long currentPaymentTime = System.currentTimeMillis();
                    sessionManager.createLoginSession(
                            sessionManager.getUserDetails().get("name"),
                            userEmail,
                            sessionManager.getUserDetails().get("phone"),
                            sessionManager.getUserDetails().get("course"),
                            currentPaymentTime
                    );

                    startActivity(new Intent(PaymentActivity.this, DashboardActivity.class));
                    finish();
                } else {
                    Toast.makeText(PaymentActivity.this, "Payment failed. Invalid transaction ID", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(PaymentActivity.this, "Error verifying payment", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

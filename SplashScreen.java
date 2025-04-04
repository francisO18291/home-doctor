package com.example.homedoctor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 3000; // Splash screen time duration
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sessionManager = new SessionManager(this);

        new Handler().postDelayed(() -> {

            if (!sessionManager.isLoggedIn()) {

                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            } else {

                long lastPaymentTime = sessionManager.getLastPaymentTime();
                long currentTime = System.currentTimeMillis();
                long oneDay = 24 * 60 * 60 * 1000; // 24 hours in milliseconds


                if ((currentTime - lastPaymentTime) > oneDay) {
                    startActivity(new Intent(SplashScreen.this, PaymentActivity.class));
                } else {

                    startActivity(new Intent(SplashScreen.this, DashboardActivity.class));
                }
            }
            finish();
        }, SPLASH_TIME_OUT);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}

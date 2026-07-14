package com.example.remindertugasmahasiswa;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.remindertugasmahasiswa.databinding.ActivitySplashBinding;

public class SplashActivity
        extends AppCompatActivity {

    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding =
                ActivitySplashBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(binding.getRoot());

        // =========================
        // ANIMASI
        // =========================

        Animation fade =
                AnimationUtils.loadAnimation(
                        this,
                        android.R.anim.fade_in
                );

        binding.imgLogo.startAnimation(fade);

        binding.txtApp.startAnimation(fade);

        // =========================
        // DELAY SPLASH
        // =========================

        new Handler(
                Looper.getMainLooper()
        ).postDelayed(() -> {

            Intent intent =
                    new Intent(
                            SplashActivity.this,
                            MainActivity.class
                    );

            startActivity(intent);

            overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            );

            finish();

        }, 2000);
    }
}
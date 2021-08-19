package com.sefcyn2000.reports.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.ui.activities.MainActivity;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(SplashScreen.this, MainActivity.class));
        finish();
    }
}

package com.sefcyn2000.reports.utilities;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ContextApp extends AppCompatActivity {
    public static Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        if (context == null) {
            context = (ContextApp) this;
        }
    }

    public static Context getContext() {
        return ContextApp.context;
    }
}

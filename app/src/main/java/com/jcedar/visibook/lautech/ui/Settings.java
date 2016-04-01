package com.jcedar.visibook.lautech.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.helper.PrefUtils;

public class Settings extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_up);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if (toolbar != null) {
                        toolbar.setTitle(getString(R.string.title_activity_settings));
                    }
                }
            });

        loadPreferences();
        getFragmentManager().beginTransaction()
                .add(R.id.container, new Preference())
                .commit();


    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        loadPreferences();
    }
    private void loadPreferences() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean notificationState = settings.getBoolean(PrefUtils.NOTIFY_KEY, true);

        settings.registerOnSharedPreferenceChangeListener(Settings.this);
    }


}

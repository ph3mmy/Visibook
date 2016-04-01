package com.jcedar.visibook.lautech.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import com.facebook.appevents.AppEventsLogger;
import com.jcedar.visibook.lautech.R;

public class Preference extends PreferenceFragment {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        settings.registerOnSharedPreferenceChangeListener(mPrefChangeListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        settings.unregisterOnSharedPreferenceChangeListener(mPrefChangeListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        /*EditTextPreference versionPref = (EditTextPreference)findPreference("version");
        String version = null;
        try {
            version = getActivity().getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionPref.setTitle(getString(R.string.version) + ": " + version);
*/
        EditTextPreference devPref = (EditTextPreference)findPreference("developer");
        //devPref.setSummary("Afolayan Oluwaseyi");
        devPref.setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(android.preference.Preference preference) {
                String url = "http://www.facebook.com/afolayanjeph";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return false;
            }
        });

        EditTextPreference desPref = (EditTextPreference)findPreference("designer");
        //desPref.setSummary("Bamisaye Oluwafemi");
        desPref.setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(android.preference.Preference preference) {
                String url = "http://www.facebook.com/obamisaye";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return false;
            }
        });



    }

    private SharedPreferences.OnSharedPreferenceChangeListener mPrefChangeListener
            = new SharedPreferences.OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());


            onNotificationChanged(key);
        }
    };

    private void onNotificationChanged(String key) {
        if(key.equals("notification")){
            SwitchPreference switchPreference = (SwitchPreference) findPreference(key);
            if(null == switchPreference)  return;


        }
    }

    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.activateApp(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.deactivateApp( getActivity());
    }
}
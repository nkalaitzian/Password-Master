package com.ractivedev.passwordmaster;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

/**
 * Created by pasto on 09-Mar-18.
 */

public class MyActivity extends AppCompatActivity {
    public AdView adView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this, getString(R.string.application_ad_id));
    }

    private void setTheme() {
        if(Settings.isDarkThemeEnabled()){
            if(getClass().getName().contains("ActivityMain")){
                setTheme(R.style.DarkTheme_NoActionBar);
            } else {
                setTheme(R.style.DarkTheme);
            }
        } else {
            if(getClass().getName().contains("ActivityMain")){
                setTheme(R.style.AppTheme_NoActionBar);
            } else {
                setTheme(R.style.AppTheme);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adView != null){
            Stuff.initFreePaid(adView);
        }
    }
}

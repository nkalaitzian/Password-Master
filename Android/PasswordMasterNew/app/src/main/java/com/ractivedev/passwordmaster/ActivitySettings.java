package com.ractivedev.passwordmaster;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class ActivitySettings extends MyActivity {
    CheckBox darkThemeCheckBox;
    SeekBar textSizeSeekBar;
    TextView textSizeTextView;
    boolean disableCheckChangeListener = false;
    public static boolean themeChanged = false;
    SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            setTextSize(i);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initComponents();
        adView = findViewById(R.id.banner_ad_settings);
    }

    private void initComponents() {
        darkThemeCheckBox = findViewById(R.id.darkThemeCheckBox);
        textSizeSeekBar = findViewById(R.id.textSizeSeekBar);
        textSizeTextView = findViewById(R.id.textSizeTextView);
        textSizeSeekBar.setOnSeekBarChangeListener(listener);
        darkThemeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!disableCheckChangeListener) {
                    Settings.setDarkTheme(b);
                    restartSettingsActivity();
                    themeChanged = !themeChanged;
                }
            }
        });
    }

    private void restartSettingsActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Stuff.setCheckBoxTextSize(darkThemeCheckBox);
        Stuff.setTextViewSize(textSizeTextView);
        initContents();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void initContents() {
        disableCheckChangeListener = true;
        darkThemeCheckBox.setChecked(Settings.isDarkThemeEnabled());
        disableCheckChangeListener = false;
        textSizeSeekBar.setProgress(Settings.TEXT_SIZE_SEEK_BAR_LOCATION);
    }

    private void setTextSize(int i) {
        switch (i) {
            case 0:
                Settings.setTextSize(getApplicationContext(), R.dimen.twelveText);
                break;
            case 1:
                Settings.setTextSize(getApplicationContext(), R.dimen.fourteenText);
                break;
            case 2:
                Settings.setTextSize(getApplicationContext(), R.dimen.sixteenText);
                break;
            case 3:
                Settings.setTextSize(getApplicationContext(), R.dimen.eighteenText);
                break;
            case 4:
                Settings.setTextSize(getApplicationContext(), R.dimen.twentyText);
                break;
            case 5:
                Settings.setTextSize(getApplicationContext(), R.dimen.twentyTwoText);
                break;
            case 6:
                Settings.setTextSize(getApplicationContext(), R.dimen.twentyFourText);
                break;
            case 7:
                Settings.setTextSize(getApplicationContext(), R.dimen.twentySixText);
                break;
            case 8:
                Settings.setTextSize(getApplicationContext(), R.dimen.twentyEightText);
                break;
            case 9:
                Settings.setTextSize(getApplicationContext(), R.dimen.thirtyText);
                break;
            default:
                Settings.setTextSize(getApplicationContext(), R.dimen.twentyText);
                break;
        }
        Settings.TEXT_SIZE_SEEK_BAR_LOCATION = i;
        Stuff.setTextViewSize(textSizeTextView);
        Stuff.setCheckBoxTextSize(darkThemeCheckBox);
    }
}

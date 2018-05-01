package com.ractivedev.passwordmaster;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.design.widget.Snackbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class Stuff {
    public static final String WRITE_PERMISSION_STRING = "android.permission.WRITE_EXTERNAL_STORAGE";

    public static final String TEST_DEVICE = "645CA7B9BE828484746CB27E0C7CD8B7";

    public static final int exitWait = 1000 * 3;
    public static final int TITLE_MODE = 0, USERNAME_MODE = 1, PASSWORD_MODE = 2, WEBSITE_MODE = 3, OTHER_MODE = 4;
    public static final String LOGIN_ID = "LOGIN_ID";
    public static final int IMAGE_REQUEST_ID = 1;


    private final static String ps = "Password Strength:     ",
                        ps_low = "low",
                        ps_med_low = "fair",
                        ps_med = "normal",
                        ps_med_high = "good",
                        ps_high = "great",
                        ps_perfect = "excellent";


    private final static String[] alphabet = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
    private final static String[] uppercaseAlphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    private final static String[] numbers = {"1","2","3","4","5","6","7","8","9","0"};
    private final static String[] symbols = {"!","/","@","#","$","%","^","&","*","(",")","-","_","+","=",";",":","'","|","{","}","[","]",",",".","<",">","?","€"};


    public static void checkPasswordStrength(String password, ProgressBar passwordStrengthPB) {
        int rating = 0;
        for(String a: alphabet){
            if(password.contains(a)){
                rating+=10;
                break;
            }
        }
        for(String a: uppercaseAlphabet){
            if(password.contains(a)){
                rating+=10;
                break;
            }
        }
        for(String a: numbers){
            if(password.contains(a)){
                rating+=10;
                break;
            }
        }
        for(String a: symbols){
            if(password.contains(a)){
                rating+=10;
                break;
            }
        }
        rating += ((float)password.length()/16.0) * 60;
        if(rating >100){
            rating = 100;
        }
        passwordStrengthPB.setProgress(rating);
        passwordStrengthPB.getProgressDrawable().setColorFilter(getProgressBarColor(rating), PorterDuff.Mode.SRC_IN);
    }

    private static int startColor = 0xFFFF0000, endColor = 0xFF5b76ff, centerColor = 0xffffb700;
    private static int getProgressBarColor(int rating) {
        ArgbEvaluator evaluator = new ArgbEvaluator();
        if(rating <50) {
            float fraction = rating / 50f;
            return (int) evaluator.evaluate(fraction, startColor, centerColor);
        } else if (rating == 50){
            return centerColor;
        } else {
            float fraction = (rating-50) / 50f;
            return (int) evaluator.evaluate(fraction, centerColor, endColor);
        }
    }

    public static void setTextViewSize(TextView... tvs){
        for (TextView tv : tvs) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, Settings.getTextSize());
        }
    }

    public static void setEditTextSize(EditText... ets){
        for (EditText et: ets) {
            et.setTextSize(TypedValue.COMPLEX_UNIT_PX, Settings.getTextSize());
        }
    }

    public static void setCheckBoxTextSize(CheckBox... cbs){
        for (CheckBox cb: cbs) {
            cb.setTextSize(TypedValue.COMPLEX_UNIT_PX, Settings.getTextSize());
        }
    }

    public static void setButtonTextSize(Button... btns){
        for (Button btn : btns) {
            btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, Settings.getTextSize());

        }
    }

    public static void setToggleButtonTextSize(ToggleButton... tbs){
        for (ToggleButton btn : tbs) {
            btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, Settings.getTextSize());
        }
    }

    public static void showSnackbar(String text, View focus){
        Snackbar.make(focus, text, exitWait).show();
    }

    public static void showToast(String text, Context context){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showLooooongToast(String text, Context context){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static String checkURL(String url) {
        if(url.contains("http://")){
            return url;
        }else if(url.contains("https://")){
            return url;
        }else{
            url = "https://" + url;
        }
        return url;
    }

    public static void initFreePaid(AdView mAdView) {

        if (Constants.type == Constants.Type.FREE) {
//            if(counter == null) {
//                counter = new MinuteCounter();
//            }
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(TEST_DEVICE)
                    .build();
//            if (Settings.adCoins.intValue() <= 0) {
                mAdView.setVisibility(View.VISIBLE);
                mAdView.loadAd(adRequest);
//            } else {
//                mAdView.setVisibility(View.GONE);
//                if (!counterRunning) {
//                    counter.start();
//                }
//            }
        } else {
            mAdView.setVisibility(View.GONE);
        }
    }
}

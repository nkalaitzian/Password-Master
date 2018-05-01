package com.ractivedev.passwordmaster;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by pasto on 05-Mar-18.
 */

public class Settings {

    public final static String SEPARATOR = "---!---";
    public final static String SEPARATOR2 = "-!-!-!-";
    static File settingsFile;
    public static float DEFAULT_TEXT_SIZE = 0;
    private static float TEXT_SIZE = 0;
    public static int TEXT_SIZE_SEEK_BAR_LOCATION = 4;

    private static boolean darkThemeEnabled = false;
    private static long timeout = 0;
    private static final int TIMEOUT_DURATION_MINUTES = 30;
    private final static long TIMEOUT_DURATION_MILLIS = TIMEOUT_DURATION_MINUTES * 60 * 1000;

    public static void initSettings(Context context){
        DEFAULT_TEXT_SIZE = context.getResources().getDimensionPixelSize(R.dimen.twentyText);
        settingsFile = new File(context.getFilesDir(), "settings.ini");
        if(settingsFile.exists()){
            readSettings();
        }
    }

    private static void readSettings() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(settingsFile));
            String line;
            String result = "";
            while((line = br.readLine()) != null){
                result += line;
            }
            br.close();
            if(!result.contains(SEPARATOR2)) {
                fromString(result);
            } else {
                String[] res = result.split(SEPARATOR2);
                try {
                    for(String s : res) {
                        if(s.contains("Generator")) {
                            Generator.setFromString(s);
                        }
                        if(s.contains("Settings")) {
                            fromString(s);
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException ex){
                    Log.e("ERROR", null, ex);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveSettings(){
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(settingsFile));
            bw.write(getString());
            bw.write(SEPARATOR2);
            bw.write(Generator.getString());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static float getTextSize() {
        if (TEXT_SIZE != 0.0) {
            return TEXT_SIZE;
        }
        return DEFAULT_TEXT_SIZE;
    }

    public static void setTextSize(Context context, int textSize) {
        TEXT_SIZE = context.getResources().getDimensionPixelSize(textSize);
    }

    public static void setTextSize(float textSize, int seekBarLocation){
        TEXT_SIZE = textSize;
        TEXT_SIZE_SEEK_BAR_LOCATION = seekBarLocation;
    }

    public static String getString(){
        return "Settings:TEXT_SIZE:"+getTextSize() + "," + TEXT_SIZE_SEEK_BAR_LOCATION +
                SEPARATOR + "DARK_THEME:" + darkThemeEnabled +
                getTimeout();
    }

    public static void fromString(String s) {
        s = s.replace("Settings:","");
        for (String a : s.split(SEPARATOR)) {
            if (a.contains("TEXT_SIZE:")) {
                String[] b = a.replace("TEXT_SIZE:", "").split(",");
                try {
                    setTextSize(Float.valueOf(b[0]), Integer.valueOf(b[1]));
                } catch (NumberFormatException ex) {
                    TEXT_SIZE_SEEK_BAR_LOCATION = 4;
                }
            } else if (a.contains("DARK_THEME:")) {
                setDarkTheme(Boolean.valueOf(a.replace("DARK_THEME:","")));
            } else if(a.contains("TIMEOUT:")){
                timeout = Long.valueOf(a.replace("TIMEOUT:",""));
            }
        }
    }


    public static void setDarkTheme(boolean darkTheme) {
        Settings.darkThemeEnabled = darkTheme;
    }
    public static boolean isDarkThemeEnabled(){
        return darkThemeEnabled;
    }

    public static boolean hasTimeout(){
        if(System.currentTimeMillis() - timeout > 0){
            if(timeout != 0) {
                timeout = 0;
                LoginList.passwordAttempts = 0;
            }
            return false;
        } else {
            return true;
        }
    }

    public static void setTimeout(){
        timeout = System.currentTimeMillis() + TIMEOUT_DURATION_MILLIS;
    }

    public static int getRemainingTimeout() {
        return (int) ((timeout - System.currentTimeMillis())/1000);
    }


    public static String getTimeout() {
        if(timeout > 0){
            return SEPARATOR + "TIMEOUT:" + timeout;
        }
        return "";
    }
}

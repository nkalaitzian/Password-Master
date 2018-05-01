package com.ractivedev.passwordmaster;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class ActivityShowLogin extends MyActivity {

    EditText loginTitleEditText, loginUsernameEditText, loginPasswordEditText, loginWebsiteEditText, loginOtherEditText;
    ImageView loginImage;
    CheckBox favouriteCheckBox;
    ProgressBar pb;
    Login login;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_login);
        initComponents();
        int id = (int) getIntent().getSerializableExtra(Stuff.LOGIN_ID);
        login = LoginList.getLogin(id);
        initContents();
        adView = findViewById(R.id.banner_ad_show_login);
    }

    Uri tempImageUri = null;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case Stuff.IMAGE_REQUEST_ID:
                if(resultCode == RESULT_OK) {
                    Uri selectedImageUri = data.getData();
                    tempImageUri = selectedImageUri;
                    applyImage(tempImageUri);
                }
                break;
            default:
                break;
        }
    }

    private void applyImage(Uri tempImageUri) {
        try {
            loginImage.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), tempImageUri));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_show_login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveLoginMenuItem :
                saveLogin();
                break;
            case R.id.cancelLoginMenuItem :
                cancelLogin();
                break;
            case R.id.deleteLoginMenuItem :
                deleteLogin();
                break;
            case R.id.resetImageMenuItem :
                resetImage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    boolean resetImage = false;
    private void resetImage() {
        loginImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.add_icon_black));
        resetImage = true;
        addImageToLogin(login.getTitle());
    }

    private Uri copyImageToImages(@NonNull Uri selectedImageUri) {
        FileChannel source;
        FileChannel destination;
        try {
            source = new FileInputStream(getContentResolver().openFileDescriptor(selectedImageUri, "r").getFileDescriptor()).getChannel();
            File outputFile = new File(getApplicationContext().getFilesDir(), login.getId()+"");
            destination = new FileOutputStream(outputFile).getChannel();
            if (destination != null && source != null) {
                destination.transferFrom(source, 0, source.size());
                source.close();
                destination.close();
            }
            return Uri.fromFile(outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initComponents() {
        loginTitleEditText = findViewById(R.id.loginTitleEditText);
        loginUsernameEditText = findViewById(R.id.loginUsernameEditText);
        loginPasswordEditText = findViewById(R.id.loginPasswordEditText);
        loginWebsiteEditText = findViewById(R.id.loginWebsiteEditText);
        loginOtherEditText = findViewById(R.id.loginOtherEditText);
        loginImage = findViewById(R.id.showLoginImage);
        favouriteCheckBox = findViewById(R.id.loginFavouriteCheckBox);
        pb = findViewById(R.id.loginPasswordStrengthProgressBar);
        Stuff.setEditTextSize(loginTitleEditText, loginUsernameEditText, loginPasswordEditText, loginWebsiteEditText, loginOtherEditText);
        Stuff.setCheckBoxTextSize(favouriteCheckBox);
        Button button = findViewById(R.id.loginOpenWebsiteButton), genPasswordButton = findViewById(R.id.generatePasswordShowLoginButton);
        Stuff.setButtonTextSize(button, genPasswordButton);
        initListeners();
    }

    private void initListeners(){
        loginPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s!=null){
                    Stuff.checkPasswordStrength(s.toString(), pb);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        loginTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s!=null){
                    if(!login.hasCustomImage() && tempImageUri == null || resetImage){
                        addImageToLogin(s.toString());
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        loginImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageIntent = new Intent();
                imageIntent.setType("image/*");
                imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(imageIntent, getString(R.string.image_picker_text)),
                        Stuff.IMAGE_REQUEST_ID);
            }
        });
    }

    int tempImage = -1;
    private void addImageToLogin(String s) {
        if(s.toLowerCase().contains("facebook")){
            tempImage = R.drawable.facebook_icon;
        } else if (s.toLowerCase().contains("messenger")){
            tempImage = R.drawable.facebook_messenger_icon;
        } else if (s.toLowerCase().contains("google")){
            tempImage = R.drawable.google_icon;
        } else if (s.toLowerCase().contains("google") && s.toLowerCase().contains("plus")){
            tempImage = R.drawable.google_plus_icon;
        } else if (s.toLowerCase().contains("instagram")){
            tempImage = R.drawable.instagram_icon;
        } else if (s.toLowerCase().contains("linkedin")){
            tempImage = R.drawable.linkedin_icon;
        } else if (s.toLowerCase().contains("myspace")){
            tempImage = R.drawable.myspace_icon;
        } else if (s.toLowerCase().contains("stackoverflow")){
            tempImage = R.drawable.stack_overflow_icon;
        } else if (s.toLowerCase().contains("tumblr")){
            tempImage = R.drawable.tumblr_icon;
        } else if (s.toLowerCase().contains("twitter")){
            tempImage = R.drawable.twitter_icon;
        } else if (s.toLowerCase().contains("youtube")){
            tempImage = R.drawable.youtube_icon;
        } else if (s.toLowerCase().contains("reddit")){
            tempImage = R.drawable.reddit_icon;
        } else if (s.toLowerCase().contains("imgur")){
            tempImage = R.drawable.imgur_icon;
        } else if (s.toLowerCase().contains("twitch")){
            tempImage = R.drawable.twitch_icon;
        } else {
            tempImage = R.drawable.add_icon_black;
        }
        if(tempImage != -1) {
            loginImage.setImageDrawable(getDrawable(tempImage));
        }
    }

    private void initContents() {
        loginTitleEditText.setText(login.getTitle());
        loginUsernameEditText.setText(login.getUsername());
        loginPasswordEditText.setText(login.getPassword());
        loginWebsiteEditText.setText(login.getWebsite());
        loginOtherEditText.setText(login.getOther());
        loginImage.setImageBitmap(login.getImage(this));
        favouriteCheckBox.setChecked(login.getFavorite());
    }

    public void saveLogin() {
        login.setTitle(String.valueOf(loginTitleEditText.getText()).replaceAll("---","-_-"));
        login.setUsername(String.valueOf(loginUsernameEditText.getText()).replaceAll("---","-_-"));
        login.setPassword(String.valueOf(loginPasswordEditText.getText()).replaceAll("---","-_-"));
        login.setWebsite(String.valueOf(loginWebsiteEditText.getText()).replaceAll("---","-_-"));
        login.setOther(String.valueOf(loginOtherEditText.getText()).replaceAll("---","-_-"));
        login.setFavorite(favouriteCheckBox.isChecked());
        if(resetImage){
            login.resetImage();
            resetImage = false;
        }
        if(tempImageUri != null){
            login.deleteImageUri();
            login.setImageURI(copyImageToImages(tempImageUri));
            tempImageUri = null;

            gotoMainPage();
            return;
        }
        if(tempImage != -1) {
            login.setImage(tempImage);
            tempImage = -1;

            gotoMainPage();
            return;
        }
        gotoMainPage();
    }

    public void cancelLogin() {
        gotoMainPage();
    }

    public void deleteLogin() {
        ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog_Alert);
        new AlertDialog.Builder(ctw)
                .setTitle(getString(R.string.delete_login_alert_title))
                .setMessage(getString(R.string.delete_login_alert_content))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        LoginList.deleteLogin(login);
                        gotoMainPage();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();

    }

    private void gotoMainPage() {
        finish();
    }

    @Override
    public void onBackPressed() {
        saveLogin();
        gotoMainPage();
    }

    public void openWebsite(View view) {
        String url = String.valueOf(loginWebsiteEditText.getText());
        if(url.trim().equals("")){
            Stuff.showToast(getString(R.string.website_empty_toast_text),this);
            return;
        }
        Uri uri = Uri.parse(Stuff.checkURL(url));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


    public void showPasswordGenerator(View view) {
        Intent intent = new Intent(this, ActivityPasswordGenerator.class);
        startActivity(intent);
    }
}

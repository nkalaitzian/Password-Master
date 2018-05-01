package com.ractivedev.passwordmaster;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

public class ActivityPasswordChange extends MyActivity {

    EditText passwordVerificationEditText, newPasswordEditText;
    ProgressBar pb;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        initComponents();
        adView = findViewById(R.id.banner_ad_password_change);
    }

    private void initComponents() {
        passwordVerificationEditText = findViewById(R.id.password_verification_edit_text);
        newPasswordEditText = findViewById(R.id.new_password_edit_text);
        pb = findViewById(R.id.changePasswordStrengthProgressBar);
        TextWatcher editTextWatcher = new TextWatcher() {
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
        };
        newPasswordEditText.addTextChangedListener(editTextWatcher);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Stuff.setEditTextSize(passwordVerificationEditText,newPasswordEditText);
    }

    public void submitPassword(View view) {
        String passwordVerification = String.valueOf(passwordVerificationEditText.getText());
        String newPassword = String.valueOf(newPasswordEditText.getText());
        if(initialCheck(passwordVerification)){
            if (LoginList.changePassword(passwordVerification, newPassword)) {
                finish();
            } else {
                if(LoginList.passwordAttempts >= LoginList.TOTAL_ATTEMPTS){
                    Settings.setTimeout();
                    Stuff.showLooooongToast(getString(R.string.toast_timeout_text) + Settings.getRemainingTimeout(),this);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 5000);
                    return;
                }
                String wrongPasswordToast = getResources().getString(R.string.wrong_password_warning) + " " + (LoginList.TOTAL_ATTEMPTS - LoginList.passwordAttempts);
                Stuff.showToast(wrongPasswordToast, getApplicationContext());
            }
        }
    }

    private boolean initialCheck(String password){
        if(Settings.hasTimeout()){
            Stuff.showToast(getString(R.string.toast_timeout_text) + Settings.getRemainingTimeout(),this);
            return false;
        }
        if(password.trim().equals("")){
            Stuff.showToast(getString(R.string.toast_text_empty_password), this);
            return false;
        }
        return true;
    }
}

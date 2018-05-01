package com.ractivedev.passwordmaster;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ActivityPasswordRequest extends MyActivity {

    EditText passwordEditText;
    TextView appTitleTextView;
    Button submitButton;
    ProgressBar pb;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Settings.initSettings(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_request);
        initComponents();
        LoginList.init(getApplicationContext());
//        clearOtherFiles();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Stuff.setEditTextSize(passwordEditText);
        Stuff.setButtonTextSize(submitButton);
        appTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, Settings.getTextSize());
    }

    private void initComponents() {
        adView = findViewById(R.id.banner_ad_password_request);
        passwordEditText = findViewById(R.id.password_request_edit_text);
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
        passwordEditText.addTextChangedListener(editTextWatcher);

        submitButton = findViewById(R.id.password_change_submit_button);
        appTitleTextView = findViewById(R.id.appTitleTextView);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ( (actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN ))){
                    submitPassword(getCurrentFocus());
                    return true;
                }
                else{
                    return false;
                }
            }
        });
    }

    public void submitPassword(View view) {
        LoginList.password = passwordEditText.getText().toString();
        if(initialCheck(LoginList.password)) {
            if (LoginList.readLogins()) {
                Intent intent = new Intent(this, ActivityMain.class);
                startActivity(intent);
                finish();
            } else {
                if (LoginList.passwordAttempts >= LoginList.TOTAL_ATTEMPTS) {
                    Settings.setTimeout();
                    Stuff.showLooooongToast(getString(R.string.toast_timeout_text) + Settings.getRemainingTimeout(), this);
                    return;
                }
                String wrongPasswordToast = getResources().getString(R.string.wrong_password_warning) + " " + (LoginList.TOTAL_ATTEMPTS - LoginList.passwordAttempts);
                Stuff.showToast(wrongPasswordToast, this);
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

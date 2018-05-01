package com.ractivedev.passwordmaster;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

public class ActivityPasswordGenerator extends MyActivity {

    ConstraintLayout advancedGeneratorLayout;
    EditText passwordGeneratorEditText, lowercaseEditText, uppercaseEditText, symbolsEditText, numbersEditText, passwordLengthEditText;
    CheckBox lowercaseCheckBox, uppercaseCheckBox, symbolsCheckBox, numbersCheckBox;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_generator);
        initComponents();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_password_generator_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch  (item.getItemId()) {
            case R.id.restoreGeneratorDefaultsMenuItem :
                Generator.setDefault();
                initContent();
                break;
            case android.R.id.home :
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onPause() {
        setEverything();
        super.onPause();
    }

    private void initComponents() {
        advancedGeneratorLayout = findViewById(R.id.advancedGeneratorLayout);

        Button generatePasswordButton = findViewById(R.id.generatePasswordButton),
                copyPasswordButton = findViewById(R.id.copyPasswordButton),
                shufflePasswordButton = findViewById(R.id.shufflePasswordButton);
        ToggleButton showAdvancedGeneratorToggleButton = findViewById(R.id.showAdvancedGeneratorToggleButton);
        Stuff.setButtonTextSize(generatePasswordButton, copyPasswordButton, shufflePasswordButton);
        Stuff.setToggleButtonTextSize(showAdvancedGeneratorToggleButton);

        passwordGeneratorEditText = findViewById(R.id.passwordGeneratorEditText);
        lowercaseEditText = findViewById(R.id.lowercaseEditText);
        uppercaseEditText = findViewById(R.id.uppercaseEditText);
        symbolsEditText = findViewById(R.id.symbolsEditText);
        numbersEditText = findViewById(R.id.numbersEditText);
        passwordLengthEditText = findViewById(R.id.passwordLengthEditText);
        Stuff.setEditTextSize(passwordGeneratorEditText, lowercaseEditText, uppercaseEditText, symbolsEditText, numbersEditText, passwordLengthEditText);

        lowercaseCheckBox = findViewById(R.id.lowercaseCheckBox);
        uppercaseCheckBox = findViewById(R.id.uppercaseCheckBox);
        symbolsCheckBox = findViewById(R.id.symbolsCheckBox);
        numbersCheckBox = findViewById(R.id.numbersCheckBox);
        initListeners();
    }

    private void initListeners() {
        lowercaseCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Generator.setLowercaseSelected(b);
            }
        });
        uppercaseCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Generator.setUppercaseSelected(b);
            }
        });
        symbolsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Generator.setSymbolsSelected(b);
            }
        });
        numbersCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Generator.setNumbersSelected(b);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initContent();
    }

    private void initContent() {
        initEditTexts();
        initCheckBoxes();
    }

    private void initEditTexts() {
        lowercaseEditText.setText(Generator.getLowercaseString());
        uppercaseEditText.setText(Generator.getUppercaseString());
        symbolsEditText.setText(Generator.getSymbolsString());
        numbersEditText.setText(Generator.getNumbersString());
        passwordLengthEditText.setText(Generator.getLength() + "");
    }

    private void initCheckBoxes() {
        lowercaseCheckBox.setChecked(Generator.isLowercaseSelected());
        uppercaseCheckBox.setChecked(Generator.isUppercaseSelected());
        symbolsCheckBox.setChecked(Generator.isSymbolsSelected());
        numbersCheckBox.setChecked(Generator.isNumbersSelected());
    }

    public void showGeneratorAdvancedSettings(View view) {
        if(advancedGeneratorLayout.getVisibility() == View.GONE){
            advancedGeneratorLayout.setVisibility(View.VISIBLE);
        } else {
            advancedGeneratorLayout.setVisibility(View.GONE);
        }
    }

    public void generatePassword(View view) {
        setEverything();
        passwordGeneratorEditText.setText(Generator.createPassword());
    }

    private void setEverything() {
        setArrays();
        setSelectedArrays();
        setLength();
    }

    private void setArrays() {
        String lc = String.valueOf(lowercaseEditText.getText());
        Generator.setLowercase(lc.split(","));

        String uc = String.valueOf(uppercaseEditText.getText());
        Generator.setUppercase(uc.split(","));

        String s = String.valueOf(symbolsEditText.getText());
        Generator.setSymbols(s.split(","));

        String n = String.valueOf(numbersEditText.getText());
        Generator.setNumbers(n.split(","));
    }

    private void setSelectedArrays() {
        Generator.setLowercaseSelected(lowercaseCheckBox.isChecked());
        Generator.setUppercaseSelected(uppercaseCheckBox.isChecked());
        Generator.setSymbolsSelected(symbolsCheckBox.isChecked());
        Generator.setNumbersSelected(numbersCheckBox.isChecked());
    }

    private void setLength(){
        try {
            int length = Integer.valueOf(String.valueOf(passwordLengthEditText.getText()));
            Generator.setLength(length);
        } catch (NumberFormatException ex){
            Generator.setLength(10);
        }
    }

    public void copyPassword(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String s = String.valueOf(passwordGeneratorEditText.getText());
        Stuff.showToast(getString(R.string.toast_text_password_generator_copy), this);
        ClipData clip = ClipData.newPlainText(getResources().getString(R.string.app_name),s);
        clipboard.setPrimaryClip(clip);
    }

    public void shufflePassword(View view) {
        passwordGeneratorEditText.setText(Generator.shufflePassword());
    }
}

package com.ractivedev.passwordmaster;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public class ActivityMain extends MyActivity implements NavigationView.OnNavigationItemSelectedListener{
    private final int FILE_SELECT_CODE = 0;

    ConstraintLayout filterLayout;
    DrawerLayout drawer;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer_and_layout);
        initContent();
    }

    private void initContent() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginList.newLogin();
                ActivityMainFragment.updateContent();
            }
        });
        filterLayout = findViewById(R.id.filterLayout);
        adView = findViewById(R.id.banner_ad_main_activity);
        drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.mainActivityToolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Settings.hasTimeout()){
            LoginList.exit();
            finish();
            return;
        }
        if(ActivitySettings.themeChanged){
            restartActivity();
        }
    }


    public static boolean recreated = false;
    private void restartActivity() {
        if(recreated){
            return;
        }
        recreated = true;
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    if(hasPermission(Stuff.WRITE_PERMISSION_STRING)){
                        requestFilePassword(uri);
                    } else {
                        Stuff.showToast(getString(R.string.permission_failure_toast), getApplicationContext());
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void requestFilePassword(final Uri uri) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityMain.this);
        alertDialog.setTitle(getString(R.string.dialog_title_password_request));

        final EditText input = new EditText(ActivityMain.this);
        input.setTextColor(getResources().getColor(R.color.blackFontColor));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton(getString(R.string.button_submit_text),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        LoginList.importFromFile(uri, getApplicationContext(), String.valueOf(input.getText()), replaceCurrentLogins);
                        Stuff.showToast(getString(R.string.toast_text_import_file_decrypt_attempt), getApplicationContext());
                    }
                });

        alertDialog.setNegativeButton(getString(R.string.button_text_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
        try {
            getSupportActionBar().setIcon(R.mipmap.ic_launcher);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (NullPointerException e){
            Log.e("ERROR", null, e);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.filterMenuItem:
                toggleFilter();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleFilter() {
        if(filterLayout.getVisibility() == View.GONE) {
            filterLayout.setVisibility(View.VISIBLE);
        } else {
            filterLayout.setVisibility(View.GONE);
        }
    }

    static boolean replaceCurrentLogins = false;
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch  (item.getItemId()) {
            case R.id.settingsMenuItem :
                openSettings();
                return true;
            case R.id.changePasswordMenuItem :
                openPasswordChangeActivity();
                return true;
            case R.id.generatePasswordMenuItem :
                openPasswordGenerator();
                return true;
            case R.id.shareLoginsMenuItem :
                shareLogins();
                return true;
            case R.id.importLoginsFromFile :
                replaceCurrentLogins = false;
                importLogins();
                return true;
            case R.id.importAndDeleteLoginsFromFile :
                replaceCurrentLogins = true;
                importLogins();
                return true;
            default:
                break;
        }
        return false;
    }


    boolean readyToImportLogins = false;
    private void importLogins() {
        if(hasPermission(Stuff.WRITE_PERMISSION_STRING)){
            selectLoginFile();
        } else {
            managePermissions();
            readyToImportLogins = true;
        }
    }

    private void selectLoginFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a .pmaster file to get Logins from."), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, R.string.please_install_file_manager_toast, Toast.LENGTH_SHORT).show();
        }
    }
    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {
            case 200:
                break;
        }
        if(grantResults[0] == 0){
            if(readyToImportLogins){
                readyToImportLogins = false;
                importLogins();
            }
        }
    }

    private void managePermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            ArrayList<String> perms = new ArrayList<>();
            if (!hasPermission("android.permission.WRITE_EXTERNAL_STORAGE")) {
                perms.add("android.permission.WRITE_EXTERNAL_STORAGE");
            }
            String[] perm = new String[perms.size()];
            int i = 0;
            for (String p : perms) {
                perm[i++] = p;
            }
            requestPermissions(perm, 200);
        }
    }

    private void shareLogins() {
        ActivityMainFragment.updateContent();
        try {
            File fileForSharing = LoginList.saveLoginsForSharing(getApplicationContext());
            if(fileForSharing!= null){
                shareFile(fileForSharing);
            }
        } catch (IOException e) {
            Log.e("ERROR", null, e);
            Stuff.showToast("Something went wrong.",getApplicationContext());
        } catch (IllegalBlockSizeException | BadPaddingException |InvalidAlgorithmParameterException | InvalidKeyException e) {
            Log.e("ERROR", null, e);
            Stuff.showToast("Something went wrong.",getApplicationContext());
        }
    }

    private void shareFile(File fileForSharing) {
        Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), "com.ractivedev.passwordmaster.myfileprovider", fileForSharing);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("*/*");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.menu_item_share_logins)));
    }

    public void openPasswordGenerator() {
        Intent intent = new Intent(this, ActivityPasswordGenerator.class);
        startActivity(intent);
    }

    public void openPasswordChangeActivity() {
        Intent intent  = new Intent (this, ActivityPasswordChange.class);
        startActivity(intent);
    }

    public void openSettings() {
        Intent intent = new Intent(this, ActivitySettings.class);
        startActivity(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ActivityMainFragment.updateContent();
    }

    boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            LoginList.saveLogins();
            LoginList.exit();
            super.onBackPressed();
        } else {
            Stuff.showSnackbar(getResources().getString(R.string.press_back_again_to_exit), getCurrentFocus());
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, Stuff.exitWait);
        }
    }
}

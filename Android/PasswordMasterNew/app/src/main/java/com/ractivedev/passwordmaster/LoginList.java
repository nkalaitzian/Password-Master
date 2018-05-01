package com.ractivedev.passwordmaster;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Collections;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

/**
 * Created by pasto on 05-Mar-18.
 */

public class LoginList {

    public static String password;

    public static int passwordAttempts = 0;
    public static final int TOTAL_ATTEMPTS = 5;
    public static int loginCount = 0;
    public static ArrayList<Login> loginArrayList;
    static File loginFile;
    private static AES cipher;

    public static void init(Context context){
        if(loginFile == null){
            loginFile = new File(context.getFilesDir(),"logins.pmaster");
        }
        if(cipher == null) {
            cipher = new AES();
        }
    }

    public static ArrayList<Login> getLoginList(){
        if(loginArrayList == null){
            loginArrayList = new ArrayList<>();
        }
        return loginArrayList;
    }

    public static boolean readLogins() {
        loginCount = 0;
        if(!loginFile.exists()){
            return true;
        }
        FileInputStream fis = null;
        byte[] result = new byte[(int) loginFile.length()];
        try {
            fis = new FileInputStream(loginFile);
            fis.read(result);
            fis.close();
            String res = null;
            try {
                res = cipher.decrypt(result, password).substring(16);
                for (String l : res.split("--!--")) {
                    Login login = Login.fromString(l);
                    loginCount++;
                    getLoginList().add(login);
                }
                passwordAttempts = 0;
                return true;
            } catch (InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                Log.e("ERROR",null, e);
                passwordAttempts ++;
                return false;
            }
        } catch (IOException | NullPointerException e) {
            Log.e("ERROR",null, e);
        }
        return false;
    }

    public static void saveLogins() {
        if(getLoginList().size() <= 0){
            return;
        }
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(loginFile);
            String plaintext = "AAAAAAAAAAAAAAAA";
            for(Login a:getLoginList()){
                plaintext += a.toString();
            }
            byte[] encryptedFile = cipher.encrypt(plaintext, password);
            fos.write(encryptedFile);
            fos.close();
        } catch (BadPaddingException | InvalidKeyException | IllegalBlockSizeException | InvalidAlgorithmParameterException | IOException e) {
            Log.e("ERROR:", null, e);
        }
    }

    public static void sortLoginArrayList(){
        Collections.sort(loginArrayList, new Comparators.LoginComparatorDescending());
    }

    public static Login getLogin(int id){
        if(loginArrayList == null){
            return null;
        }
        for(Login l : loginArrayList){
            if(l.getId() == id){
                return l;
            }
        }
        return null;
    }

    public static Login newLogin(){
        if(loginArrayList == null){
            return null;
        }
        Login login = new Login(loginCount++);
        loginArrayList.add(login);
        return login;
    }

    public static void deleteLogin(Login login){
        loginArrayList.remove(login);
        login.deleteImageUri();
        int count = 0;
        for(Login a: loginArrayList){
            a.setId(count++);
        }
        loginCount--;
    }

    public static void exit() {
        Settings.saveSettings();
        password = null;
        passwordAttempts = 0;
        loginArrayList = null;
        loginCount = 0;
        cipher = null;
    }

    public static boolean changePassword(String oldPassword, String newPassword) {
        if(password.equals(oldPassword)) {
            passwordAttempts = 0;
            password = newPassword;
            saveLogins();
            return true;
        } else {
            passwordAttempts++;
            return false;
        }
    }

    public static File saveLoginsForSharing(Context context) throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        if(getLoginList().size() <= 0){
            return null;
        }
        FileOutputStream fos;
        File loginFile = getExternalLoginFile(context);
        fos = new FileOutputStream(loginFile);
        String plaintext = "AAAAAAAAAAAAAAAA";
        for(Login a:getLoginList()){
            plaintext += a.toPmasterString();
        }
        byte[] encryptedFile = cipher.encrypt(plaintext, password);
        fos.write(encryptedFile);
        fos.close();
        return loginFile;
    }

    public static File getExternalLoginFile(Context context) throws IOException {
        File folder = new File(context.getFilesDir(), "sharing");
        if(!folder.exists()){
            folder.mkdir();
        }
        File file = new File(folder, "logins.pmaster");
        if(file.exists()){
            file.delete();
        }
        file.createNewFile();
        return file;
    }

    public static void importFromFile(Uri uri, Context context, String filePassword, boolean replaceCurrentLogins) {
        if(replaceCurrentLogins){
            getLoginList().clear();
            loginCount = 0;
        }
        try {
            FileInputStream fis = (FileInputStream) context.getContentResolver().openInputStream(uri);
            byte[] result = new byte[Integer.valueOf(getFileLengthFromUri(uri, context).toString())];
            fis.read(result);
            fis.close();
            String res = null;
            try {
                res = cipher.decrypt(result, filePassword).substring(16);
                for (String l : res.split("--!--")) {
                    Login login = Login.fromString(l);
                    loginCount++;
                    getLoginList().add(login);
                }
            } catch (InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                Log.e("ERROR",null, e);
                Stuff.showToast(context.getString(R.string.toast_text_import_logins_password_fail), context);
            }
        } catch (IOException e) {
            Stuff.showToast(context.getString(R.string.toast_text_import_file_input_failure), context);
            Log.e("ERROR",null, e);
        } catch (Exception e){
            Stuff.showToast(context.getString(R.string.toast_text_import_failure), context);
            Log.e("ERROR",null, e);
        }
    }

    private static Long getFileLengthFromUri(Uri uri, Context context){
        Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        return returnCursor.getLong(sizeIndex);
    }
}

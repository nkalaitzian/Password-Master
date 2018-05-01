package com.ractivedev.passwordmaster;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    static String IV = "RandomInitVector";
    static Cipher cipher;
    static MessageDigest md;
    static IvParameterSpec spec;
    public AES(){
        try {
            md = MessageDigest.getInstance("SHA-256");
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            spec = new IvParameterSpec(IV.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException ex) {
            Log.e("ERROR","------------", ex);
        }
    }

    public byte[] encrypt(String plainText, String encryptionKey) throws UnsupportedEncodingException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        SecretKeySpec key = new SecretKeySpec(checkKey(encryptionKey).getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key,spec);
        byte[] input = plainText.getBytes("UTF-8");
        byte[] result = cipher.doFinal(input);

        return result;
    }

    public String decrypt(byte[] encryptedText, String encryptionKey) throws UnsupportedEncodingException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IllegalArgumentException {

        SecretKeySpec key = new SecretKeySpec(checkKey(encryptionKey).getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] decrypted = cipher.doFinal(encryptedText);
        String decryptedText = new String(decrypted, "UTF-8");

        return decryptedText;
    }

    private String checkKey(String encryptionKey){
        if(encryptionKey.equals("")){
            return "";
        }else{
            if(encryptionKey.length()<16){
                String[] key = encryptionKey.split("");
                int index = 0;
                while(encryptionKey.length()<16){
                    encryptionKey = encryptionKey+key[index%key.length];
                    index++;
                }
                return encryptionKey;
            }else{
                String[] key = encryptionKey.split("");
                IV = key[0]+key[1]+key[2]+key[3]+key[4]+key[5]+key[6]+key[7]+key[8]+key[9]+key[10]+key[11]+key[12]+key[13]+key[14]+key[15];
                key = encryptionKey.substring(16).split("");
                String temp = "";
                int index = 0;
                while(temp.length()<16){
                    temp = temp+key[index%key.length];
                    index++;
                }
                return temp;
            }
        }
    }
}


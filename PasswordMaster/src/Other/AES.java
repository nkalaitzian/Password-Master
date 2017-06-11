package Other;


import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

/**
 * This class holds the methods used to encrypt and decrypt text using the AES cipher.
 * @author Nikos
 */
public class AES {
    private String IV = "AAAAAAAAAAAAAAAA";
    private IvParameterSpec ivParameter = null;
    private Cipher cipher;
    private static final Logger LOG = Logger.getLogger(AES.class.getName());

    /**
     * Constructor.
     */
    public AES(){
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
            ivParameter = new IvParameterSpec(IV.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException ex) {
            LOG.log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,"Error:\n"+ex,"Error!",JOptionPane.ERROR_MESSAGE);
        }
    }    
    
    /**
     * This method checks the encryption key/password as to how long it is.
     * If it is an empty character, it pops up an error JOptionPane.
     * If it is shorter than 16 characters, it gets repeated until it is exactly 16 characters.
     * If it is longer than 16 characters, something magical happens.
     * @param encryptionKey The encryption key.
     * @return Returns the possibly altered encryption key.
     */
    private String checkKey(String encryptionKey){
        if(encryptionKey.equals("")){
            JOptionPane.showMessageDialog(null,"Encryption Key cannot be empty!","Error!",JOptionPane.ERROR_MESSAGE);
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
    
    /**
     * This is the method that encrypts plain text bytes.
     * @param in The string that contains the logins.
     * @param encryptionKey The encryption key that encrypts the "in" string.
     * @return Returns the encrypted text bytes.
     */
    public byte[] encrypt(String in, String encryptionKey){
        byte[] input = in.getBytes();
        byte[] result = null;
        encryptionKey = checkKey(encryptionKey);
        if(encryptionKey.equals("")){
            return null;
        }
        try{
            SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParameter);
            result = cipher.doFinal(input);
        }catch(UnsupportedEncodingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex){
            LOG.log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,ex.getMessage()+"\nPossible wrong password.","Error!",JOptionPane.ERROR_MESSAGE);
        }
        
        return result;
    }
    
    /**
     * This is the method that decrypts text bytes.
     * @param input The text bytes to be decrypted.
     * @param encryptionKey The decryption key with witch the text gets decrypted.
     * @return Returns the decrypted text bytes.
     */
    public String decrypt(byte[] input, String encryptionKey) {
        byte[] result = null;
        encryptionKey = checkKey(encryptionKey);
        if(encryptionKey.equals("")){
            return null;
        }
        try{
            SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
            cipher.init(Cipher.DECRYPT_MODE, key,ivParameter);
            result = cipher.doFinal(input);
        }catch(UnsupportedEncodingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex){
            LOG.log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,ex.getMessage()+"\nPossible wrong password.","Error!",JOptionPane.ERROR_MESSAGE);
        }
        return new String(result);
    }
}

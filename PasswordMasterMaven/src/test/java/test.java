
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Nikos Kalaitzian
 */
public class test {

    public static void main(String[] args) {
        readFirefoxLogins();
    }
    static File logins;
    static File key3db;

    public static void readFirefoxLogins() {
        File profiles = new File("C:\\Users\\pasto\\Desktop\\Firefox Logins");
        File login = new File(profiles.getPath() + "\\logins.json");
        key3db = new File(profiles.getPath() + "\\key3.db");
        if (login.exists()) {
            importLoginFromFirefox(login);
        }
    }
    
    static String encryptionKey;

    private static void importLoginFromFirefox(File login) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(login));
            String logins = "";
            String temp;
            while ((temp = reader.readLine()) != null) {
                logins += temp;
            }
            reader.close();
            reader = new BufferedReader(new FileReader(key3db));
            String l;
            encryptionKey = "";
            while ((l = reader.readLine()) != null) {
                encryptionKey += l;
            }
            reader.close();
            System.out.println(encryptionKey);
            JSONObject json = new JSONObject(logins);
            JSONArray array = json.getJSONArray("logins");
            for (int i = 0; i < array.length(); i++) {
                addFirefoxJSONLogin(array.getJSONObject(i));
            }
        } catch (FileNotFoundException ex) {
//            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
//            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void addFirefoxJSONLogin(JSONObject obj) {
        String encryptedUsername = obj.getString("encryptedUsername").substring(6);
        String encryptedPassword = obj.getString("encryptedPassword");
        String website = obj.getString("hostname");
        
//        System.out.println("username: ");
//        System.out.println(un);
//        System.out.println("password: ");
//        System.out.println(pw);
//        System.out.println(website);

        String other = "imported from firefox";
    }
}

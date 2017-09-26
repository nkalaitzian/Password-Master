package nikoskalai.passwordmasterandroid;

import android.content.Context;
import android.support.design.widget.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LoginList {

    private static LoginList loginList;

    private static ArrayList<Login> logins;
    private Context applicationContext;

    public LoginList(Context context) {
        if(Stuff.correctPassword) {
            this.applicationContext = context;
            logins = new ArrayList<>();
            readFile();
        }
    }

    private void readFile(){
        File file = new File(this.applicationContext.getFilesDir(), "logins");
        if(file.exists()){
            byte[] filebytes = new byte[(int) file.length()];
            String result = "";
            try {
                FileInputStream fis = new FileInputStream(file);
                fis.read(filebytes);
                result = new String(filebytes, "UTF-8");
            } catch (IOException e) {
                Snackbar.make(MainFrame.coordinatorLayout, e.getLocalizedMessage(), Snackbar.LENGTH_INDEFINITE).show();
            }/*
            try{
                //TODO ReadFile
            } catch (InvalidKeyException | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
                Stuff.correctPassword = false;
            }*/
        }else{
            newLogin();
            Stuff.correctPassword = true;
        }
    }

    public static LoginList get(Context context){
        if(loginList==null){
            loginList = new LoginList(context);
        }else if(!Stuff.correctPassword){
            loginList = new LoginList(context);
        }
        return loginList;
    }

    public static Login newLogin(){
        Login login = new Login(makeId());
        logins.add(login);
        return login;
    }

    private static int makeId(){
        int id = -1;
        for(Login l: logins){
            if(l.getId() >= id){
                id = l.getId() + 1;
            }
        }
        return id;
    }

    public static void deleteLogin(Login login){
        ArrayList<Login> newLoginList = new ArrayList<>();
        for(Login a: logins){
            if(login.getId()!=a.getId()){
                newLoginList.add(a);
            }
        }
        logins = newLoginList;
    }
}

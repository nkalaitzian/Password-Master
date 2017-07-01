/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Other;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pasto
 */
public class FileManagement {

    private static final Logger LOG = Logger.getLogger(FileManagement.class.getName());
    
    public static final File settingsFile = new File(Settings.getDirectory() + Settings.getFolderSlash() + "PasswordMaster.ini");
    
    public static void saveSettingsToFile() {
        BufferedWriter bw = null;
        try {
            if (!settingsFile.exists()) {
                settingsFile.createNewFile();
            }
            bw = new BufferedWriter(new FileWriter(settingsFile));
            bw.write(Settings.getString());
            bw.write("-!-!-");
            bw.write(Generator.getString());
            bw.close();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
    
    public static void importSettingsFromFile(){
        BufferedReader br = null;
        try {
            if (settingsFile.exists()) {
                br = new BufferedReader(new FileReader(settingsFile));
                String string = br.readLine();
                while (true) {
                    String t = br.readLine();
                    if (t == null) {
                        break;
                    } else {
                        string += t;
                    }
                }
                for(String str: string.split("-!-!-")){
                    Settings.setFromString(str);
                    Generator.setFromString(str);
                }
                br.close();
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
}

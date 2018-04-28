/*
 * 	https://github.com/nikoskalai/Password-Master
 *
 * 	Copyright (c) 2018 Nikos Kalaitzian
 * 	Licensed under the WTFPL
 * 	You may obtain a copy of the License at
 *
 * 	http://www.wtfpl.net/about/
 *
 * 	Unless required by applicable law or agreed to in writing, software
 * 	distributed under the License is distributed on an "AS IS" BASIS,
 * 	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package Other;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;
import passwordMaster.MainWindow;

/**
 *
 * @author pasto
 */
public class FileManagement {

    private static final Logger LOG = Logger.getLogger(FileManagement.class.getName());
    
    public static final File SETTINGS_FILE = new File(Settings.getDirectory() + File.separator + "PasswordMaster.ini");
    
    public static void saveSettingsToFile() {
        BufferedWriter bw = null;
        try {
            if (!SETTINGS_FILE.exists()) {
                SETTINGS_FILE.createNewFile();
            }
            bw = new BufferedWriter(new FileWriter(SETTINGS_FILE));
            bw.write(Settings.getString());
            bw.write("-!-!-");
            bw.write(Generator.getString());
            bw.write("-!-!-");
            bw.write(Login.getStringPreferences());
            bw.close();
        } catch (IOException ex) {
            MainWindow.showError(ex, "Could not save settings file.");
        }
    }
    
    public static void importSettingsFromFile(){
        BufferedReader br = null;
        try {
            if (SETTINGS_FILE.exists()) {
                br = new BufferedReader(new FileReader(SETTINGS_FILE));
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
                    Login.setPreferencesFromString(str);
                }
                br.close();
            }
        } catch (IOException ex) {
            MainWindow.showError(ex, "Could not import settings from file.");
        }
    }
}

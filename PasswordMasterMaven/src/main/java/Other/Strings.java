/**
 *	https://github.com/nikoskalai/Password-Master
 *
 *	Copyright (c) 2018 Nikos Kalaitzian
 *	Licensed under the WTFPL
 *	You may obtain a copy of the License at
 *
 *	http://www.wtfpl.net/about/
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package Other;

public class Strings {
    //TODO transfer all hardcoded strings here
    
    public static final String APP_NAME = "Password Master";
    public static final String SETTINGS_NAME = "Settings";
    public static final String PASSWORD_GENERATOR_NAME = "Password Generator";
    public static final String ICON_NAME = "ic_launcher.png";
    public static final String FILE_EXTENSION = ".pmster";
    
    public static String APP_VERSION = "";
    
    public static String getAppTitle() {
        return APP_NAME + " v" + APP_VERSION;
    }
    
    public static String getSettingsTitle() {
        return getAppTitle() + " - " + SETTINGS_NAME;
    }
    
    public static String getPasswordGeneratorTitle() {
        return getAppTitle() + " - " + PASSWORD_GENERATOR_NAME;
    }
    
    public static class MainWindowStrings {
        
        public static final String HIDE_MAIN_WINDOW = "Hide " + APP_NAME;
        public static final String SHOW_MAIN_WINDOW = "Show " + APP_NAME;
        public static final String OPEN_PASSWORD_GENERATOR = "Open " + PASSWORD_GENERATOR_NAME;
        public static final String OPEN_SETTINGS = "Open " + SETTINGS_NAME;
        
        public static final String COPY_USERNAME = "Copy " + LoginStrings.LOGIN_USERNAME;
        public static final String PASTE_USERNAME = "Paste " + LoginStrings.LOGIN_USERNAME;
        public static final String COPY_PASSWORD = "Copy " + LoginStrings.LOGIN_PASSWORD;
        public static final String PASTE_PASSWORD = "Paste " + LoginStrings.LOGIN_PASSWORD;
        public static final String COPY_WEBSITE = "Copy " + LoginStrings.LOGIN_WEBSITE;
        public static final String PASTE_WEBSITE = "Paste " + LoginStrings.LOGIN_WEBSITE;
        public static final String OPEN_WEBSITE = "Open " + LoginStrings.LOGIN_WEBSITE;
        public static final String COPY_OTHER = "Copy " + LoginStrings.LOGIN_OTHER;
        public static final String PASTE_OTHER = "Paste " + LoginStrings.LOGIN_OTHER;
        
        public static final String HIDE_INFORMATION = "Hide Information";
        public static final String SHOW_INFORMATION = "Show Information";
        
        public static final String WARNING_FILE_UNSAVED_CONTENT = "Do you want to save this file before making a new one?";
        public static final String WARNING_FILE_UNSAVED_TITLE = "Warning! This file is unsaved.";
        
        public static final String EXIT_STRING = "Exit App";
        
    }
    
    public static class LoginStrings {
        public static final String LOGIN_ID = "ID";
        public static final String LOGIN_WEBSITE  = "website";
        public static final String LOGIN_USERNAME = "username";
        public static final String LOGIN_PASSWORD = "password";
        public static final String LOGIN_OTHER = "other";
        public static final String LOGIN_FAVORITE = "favorite";
        
    }
    
    public static class SettingsWindowStrings {
        
    }
    
    public static class ExitWindowStrings {
        
    }
    
    public static class PasswordGeneratorWindowStrings {
        
    }
    
    public static class PasswordFrameStrings {
        
    }
    
    public static class StatusStrings {
        public static final String CLIPBOARD_COPY = " copied to clipboard.";
        public static final String CLIPBOARD_COPY_USERNAME = "Username: ";
        public static final String CLIPBOARD_COPY_PASSWORD = "Password: ";
        public static final String CLIPBOARD_COPY_PASSWORD_FOR_WEBSITE = "Password: ";
        public static final String CLIPBOARD_COPY_WEBSITE = "Website: ";
        public static final String OPEN_WEBSITE = "Website: ";
        public static final String PASTED_TEXT = "Pasted text: ";
    }
    
    public static class ErrorStrings {
        public static final String HIDING_INFORMATION_COPY_USERNAME = "copy username";
        public static final String HIDING_INFORMATION_COPY_PASSWORD = "copy password";
        public static final String HIDING_INFORMATION_COPY_WEBSITE = "copy website";
        public static final String HIDING_INFORMATION_OPEN_WEBSITE = "open website";
        public static final String HIDING_INFORMATION_OPEN_SETTINGS = "open settings";
        
        public static final String ERROR = "Error!";
        public static final String ERROR_COULD_NOT_INITIALIZE_TRAY_ICON = "Could not initialize tray icon.";
        public static final String ERROR_COULD_NOT_INITIALIZE_THEME = "Could not initialize theme.";
        public static final String ERROR_COULD_NOT_OPEN_FILE = "Could not open file: ";
        public static final String ERROR_COULD_NOT_OPEN_WEBSITE = "Could not open website: ";
        public static final String ERROR_NULL_FILE = "Could not open file.";
        public static final String ERROR_PASTE = "Could not paste text.";
        public static final String ERROR_WRONG_PASSWORD = "Possible wrong password.";
        public static final String ERROR_CANNOT_CALCULATE_IDLE_TIME = "Cannot calculate user idle time on this OS.";
        public static final String EXCEPTION_INTERRUPTED = "Could not pause running thread.";
        
    }
}

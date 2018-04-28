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

import java.awt.Dimension;
import java.io.File;
import java.util.logging.Logger;
import passwordMaster.MainWindow;

/**
 *
 * @author Nikos
 */
public class Settings {

    private static final Logger LOG = Logger.getLogger(Settings.class.getName());
    public static final String SEPARATOR = ",";

    /**
     * A "blank" login variable.
     */
    public static final Login STANDARD_LOGIN = new Login(0, "https://www.website.com", "username", "password", "comment");
    public static final String APP_NAME = "Password Master";
    public static final String SETTINGS_NAME = "Settings";
    public static String APP_VERSION = "3.5.0";
    
    public static String getAppTitle() {
        return APP_NAME + " - " + APP_VERSION;
    }

    private static String theme = "Windows";
    public static final String DEFAULT_THEME = "Windows";
    public static String DEFAULT_DIR = System.getProperty("user.dir");
    public static boolean alwaysDefaultDir = false;

    private static String userDir = "";
    public static final Dimension DEFAULT_SIZE = new Dimension(850, 550);
    private static Dimension userSize;
    private static int windowState = 0;

    /**
     * The default value for the idle seconds timer.
     */
    public static final int DEFAULT_IDLE_SECONDS = 20;
    private static int userIdleSeconds = -1;

    public static boolean bypassExitWindow = false;
    public static int bypassMode = 2;
    public static final int bypassAndExit = 1,
            bypassAndSaveExit = 2,
            bypassAndMinimize = 3,
            bypassAndMinimizeToSysTray = 4;

    /**
     * This method sets all settings from a String variable.
     * @param fromString The String variable.
     */
    public static void setFromString(String fromString) {
        if (!fromString.contains("Settings")) {
            return;
        }
        fromString = fromString.replaceAll("Settings", "");
        fromString = fromString.replaceAll("\\{", "");
        fromString = fromString.replaceAll("\\}", "");
        String[] fs = fromString.split(SEPARATOR);
        for (String s : fs) {
            if (s.startsWith("theme=")) {
                setTheme(s.replaceAll("theme=", ""));
            } else if (s.contains("userDir=")) {
                setDirectory(s.replaceAll("userDir=", "").trim());
            } else if (s.contains("windowState=")) {
                setWindowState(s.replaceAll("windowState=", "").trim());
            } else if (s.contains("userSize=")) {
                setUserSizeFromString(s.replaceAll("userSize=", "").trim());
            } else if (s.contains("userIdleSeconds=")) {
                setUserIdleSeconds(new Integer(s.replaceAll("userIdleSeconds=", "")));
            } else if (s.contains("bypassExitWindow=")) {
                bypassExitWindow = Boolean.valueOf(s.replace("bypassExitWindow=", ""));
            } else if (s.contains("bypassMode=")) {
                bypassMode = Integer.valueOf(s.replace("bypassMode=", ""));
            } else if (s.contains("alwaysDefaultDir=")){
                alwaysDefaultDir = Boolean.valueOf(s.replace("alwaysDefaultDir=", ""));
            }
        }
    }

    /**
     * This method returns the theme the user has selected.
     * @return The selected theme.
     */
    public static String getTheme() {
        if (theme.equals("")) {
            return DEFAULT_THEME;
        } else {
            return theme;
        }
    }

    /**
     * Sets the theme the user has selected.
     * @param theme The selected theme.
     */
    public static void setTheme(String theme) {
        if (theme == null) {
            Settings.theme = "";
        } else {
            Settings.theme = theme;
        }
    }

    /**
     * This method returns the directory the user has selected.
     * Linux distros and Windows OS use either / or \ in their file management system.
     * If the user directory contains \ and the current OS directory uses /, the user directory will reset.
     * @return The user directory.
     */
    public static String getDirectory() {
        if(alwaysDefaultDir){
            return DEFAULT_DIR;
        }
        if (DEFAULT_DIR.contains(File.separator)) {
            if (userDir.contains(File.separator)) {
                return userDir;
            } else {
                userDir = "";
            }
        } else {
            if (userDir.contains(File.separator)) {
                userDir = "";
            } else {
                return userDir;
            }
        }
        return DEFAULT_DIR;
    }

    /**
     * This method sets the directory the user has selected.
     * @param userDir The selected directory.
     */
    public static void setDirectory(String userDir) {
        if (userDir == null) {
            Settings.userDir = "";
        } else if (userDir.equals(DEFAULT_DIR)) {
            Settings.userDir = "";
        } else {
            Settings.userDir = userDir;
        }
    }

    /**
     * This method returns the window state for the MainWindow class. If 0, state is normal. If 6, state is maximized.
     * @see <a href="../passwordMaster/MainWindow.html">MainWindow.class</a>
     * @return The window state.
     */
    public static int getWindowState() {
        return windowState;
    }

    /**
     * This method sets the window state for the MainWindow class from an integer.
     * If 0, state is normal.
     * If 6, state is maximized.
     * @see <a href="../passwordMaster/MainWindow.html">MainWindow.class</a>
     * @param windowState The window state.
     */
    public static void setWindowState(int windowState) {
        Settings.windowState = windowState;
    }

    /**
     * This method sets the window state for the MainWindow class from a String.
     * If 0, state is normal.
     * If 6, state is maximized.
     * @see <a href="../passwordMaster/MainWindow.html">MainWindow.class</a>
     * @param windowState
     */
    public static void setWindowState(String windowState) {
        try {
            Settings.windowState = new Integer(windowState);
        } catch (NumberFormatException ex) {
            Settings.windowState = javax.swing.JFrame.MAXIMIZED_BOTH;
        }
    }

    /**
     * This method returns the window size for the MainWindow class as a Dimension variable.
     * @see <a href="../passwordMaster/MainWindow.html">MainWindow.class</a>
     * @return The window size.
     */
    public static Dimension getUserSize() {
        if (userSize == null) {
            return DEFAULT_SIZE;
        } else {
            return userSize;
        }
    }

    /**
     * This method returns the window size for the MainWindow class as a String variable.
     * Mainly used for saving methods.
     * @see <a href="../passwordMaster/MainWindow.html">MainWindow.class</a>
     * @return The window size in a String variable.
     */
    public static String getUserSizeString() {
        return "userSize=width=" + getUserSize().width + "-height=" + getUserSize().height;
    }

    private static void setUserSizeFromString(String str) {
        str = str.replaceAll("\\[", "");
        str = str.replaceAll("\\]", "");
        String[] s = str.split("-");
        if (s[0].equals("")) {
            return;
        }
        try {
            setUserSize(new Integer(s[0].replace("width=", "")), new Integer(s[1].replace("height=", "")));
        } catch (ArrayIndexOutOfBoundsException ex) {
            MainWindow.showError(ex, "Could not convert saved window size properly.");
        }
    }

    /**
     * This method sets the window size for the MainWindow class from a Dimension variable.
     * @see <a href="../passwordMaster/MainWindow.html">MainWindow.class</a>
     * @param userSize The window size in a String variable.
     */
    public static void setUserSize(Dimension userSize) {
        Settings.userSize = userSize;
    }

    /**
     * This method sets the window size for the MainWindow class from two integer variables.
     * @see <a href="../passwordMaster/MainWindow.html">MainWindow.class</a>
     * @param width The width of the window.
     * @param height The height of the window.
     */
    private static void setUserSize(int width, int height) {
        Settings.userSize = new Dimension(width, height);
    }

    /**
     *
     * @return
     */
    public static String getString() {
        return "Settings{"
                + "theme=" + getTheme()
                + SEPARATOR
                + "userDir=" + getDirectory()
                + SEPARATOR
                + "windowState=" + getWindowState()
                + SEPARATOR
                + getUserSizeString()
                + SEPARATOR
                + "userIdleSeconds=" + getUserIdleSeconds()
                + SEPARATOR
                + "bypassExitWindow=" + bypassExitWindow
                + SEPARATOR
                + "bypassMode=" + bypassMode
                + SEPARATOR
                + "alwaysDefaultDir=" + alwaysDefaultDir + "}";
    }

    /**
     *
     * @return
     */
    public static int getUserIdleSeconds() {
        if (userIdleSeconds == -1) {
            return DEFAULT_IDLE_SECONDS;
        } else {
            return userIdleSeconds;
        }
    }

    /**
     *
     * @param userIdleSeconds
     */
    public static void setUserIdleSeconds(int userIdleSeconds) {
        if (userIdleSeconds == DEFAULT_IDLE_SECONDS) {
            userIdleSeconds = -1;
        }
        Settings.userIdleSeconds = userIdleSeconds;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Other;

import java.awt.Dimension;
import java.util.logging.Logger;

/**
 *
 * @author Nikos
 */
public class Settings {

    private static final Logger LOG = Logger.getLogger(Settings.class.getName());

    public static Login standardLogin = new Login(0, "https://www.website.com", "username", "password", "comment");

    /**
     * The app name.
     */
    public static String app_name = "Password Master";

    /**
     * The Settings Window name.
     */
    public static String settings_name = "Settings";

    /**
     * The app version.
     */
    public static double version = 3.1;

    private static String theme = "Windows";
    public static final String defaultTheme = "Windows";

    /**
     * The default directory.
     */
    public static final String defaultDir = System.getProperty("user.dir");

    private static String userDir = "";

    /**
     *
     */
    public static final Dimension defaultSize = new Dimension(850, 550);
    private static Dimension userSize;

    /**
     *
     */
    public static int windowState = 0;

    public static final int defaultIdleSeconds = 30;
    private static int userIdleSeconds = -1;

    /**
     *
     * @param fromString
     */
    public static void setFromString(String fromString) {
        if (!fromString.contains("Settings")) {
            return;
        }
        fromString = fromString.replaceAll("Settings", "");
        fromString = fromString.replaceAll("\\{", "");
        fromString = fromString.replaceAll("\\}", "");
        String[] fs = fromString.split(",");
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
            }
        }
    }

    /**
     *
     * @return
     */
    public static String getTheme() {
        if (theme == null || theme.equals("")) {
            return defaultTheme;
        } else {
            return theme;
        }
    }

    /**
     *
     * @param theme
     */
    public static void setTheme(String theme) {
        if (theme == null) {
            Settings.theme = "";
        } else {
            Settings.theme = theme;
        }
    }

    /**
     * Linux Distros and Windows OS use either / or \ in their file management
     * system. If the user directory contains \ and the current OS directory
     * uses /, the user directory will reset.
     *
     * @return
     */
    public static String getDirectory() {
        if (defaultDir.contains("\\")) {
            if (userDir.contains("\\")) {
                return userDir;
            } else {
                userDir = "";
            }
        } else {
            if (userDir.contains("\\")) {
                userDir = "";
            } else {
                return userDir;
            }
        }
        return defaultDir;
    }

    /**
     *
     * @param userDir
     */
    public static void setDirectory(String userDir) {
        if (userDir == null) {
            Settings.userDir = "";
        } else if (userDir.equals(defaultDir)) {
            Settings.userDir = "";
        } else {
            Settings.userDir = userDir;
        }
    }

    /**
     *
     * @return
     */
    public static int getWindowState() {
        return windowState;
    }

    /**
     *
     * @param windowState
     */
    public static void setWindowState(int windowState) {
        Settings.windowState = windowState;
    }

    /**
     *
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
     *
     * @return
     */
    public static Dimension getUserSize() {
        if (userSize == null) {
            return defaultSize;
        } else {
            return userSize;
        }
    }

    /**
     *
     * @return
     */
    public static String getUserSizeString() {
        return "userSize=[width=" + getUserSize().width + "-height=" + getUserSize().height + "]";
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
            LOG.log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param userSize
     */
    public static void setUserSize(Dimension userSize) {
        Settings.userSize = userSize;
    }

    /**
     *
     * @param width
     * @param height
     */
    private static void setUserSize(int width, int height) {
        Settings.userSize = new Dimension(width, height);
    }

    public static String getString() {
        return "Settings{"
                + "theme=" + getTheme()
                + ",userDir=" + getDirectory()
                + ",windowState=" + getWindowState()
                + "," + getUserSizeString() + "}"
                + ",userIdleSeconds=" + getUserIdleSeconds() + "}";
    }

    public static String getFolderSlash() {
        if (defaultDir.contains("\\")) {
            return "\\";
        } else {
            return "/";
        }
    }

    public static int getUserIdleSeconds() {
        if (userIdleSeconds == -1) {
            return defaultIdleSeconds;
        } else {
            return userIdleSeconds;
        }
    }

    public static void setUserIdleSeconds(int userIdleSeconds) {
        if (userIdleSeconds == defaultIdleSeconds) {
            userIdleSeconds = -1;
        }
        Settings.userIdleSeconds = userIdleSeconds;
    }
}

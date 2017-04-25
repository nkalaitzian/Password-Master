/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordMaster;

import java.awt.Dimension;
import java.util.logging.Logger;

/**
 *
 * @author Nikos
 */
public class Settings {

    public static Login standardLogin = new Login("https://www.website.com", "username", "password", "comment");

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
    public static double version = 2.0;

    private String theme = "Windows";
    private String defaultTheme = "Windows";

    /**
     * The default directory.
     */
    public String defaultDir = "user.dir";

    /**
     * The directory specified by the user.
     */
    public String userDir = "";

    /**
     *
     */
    public Dimension defaultSize = new Dimension(850, 550);
    private Dimension userSize;

    /**
     *
     */
    public int windowState;

    public int defaultIdleSeconds = 30;
    private int userIdleSeconds = -1;

    /**
     *
     * @param fromString
     */
    public void fromString(String fromString) {
        fromString = fromString.replaceAll("Settings", "");
        fromString = fromString.replaceAll("\\{", "");
        fromString = fromString.replaceAll("\\}", "");
        String[] fs = fromString.split(",");
        for (String s : fs) {
            if (s.startsWith("theme=")) {
                setTheme(s.replaceAll("theme=", ""));
            } else if (s.contains("userDir=")) {
                setUserDir(s.replaceAll("userDir=", "").trim());
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
    public String getTheme() {
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
    public void setTheme(String theme) {
        if (theme == null) {
            this.theme = "";
        } else {
            this.theme = theme;
        }
    }

    /**
     * Linux Distros and Windows OS use either / or \ in their file management
     * system. If the user directory contains \ and the current OS directory
     * uses /, the user directory will reset.
     *
     * @return
     */
    public String getUserDir() {
        if (System.getProperty(defaultDir).contains("\\")) {
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
        return userDir;
    }

    /**
     *
     * @param userDir
     */
    public void setUserDir(String userDir) {
        if (userDir == null) {
            this.userDir = "";
        } else if (userDir.equals(defaultDir)) {
            this.userDir = "";
        } else {
            this.userDir = userDir;
        }
    }

    /**
     *
     * @return
     */
    public int getWindowState() {
        return windowState;
    }

    /**
     *
     * @param windowState
     */
    public void setWindowState(int windowState) {
        this.windowState = windowState;
    }

    /**
     *
     * @param windowState
     */
    public void setWindowState(String windowState) {
        try {
            this.windowState = new Integer(windowState);
        } catch (NumberFormatException ex) {
            this.windowState = javax.swing.JFrame.MAXIMIZED_BOTH;
        }
    }

    /**
     *
     * @return
     */
    public Dimension getUserSize() {
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
    public String getUserSizeString() {
        return "userSize=[width=" + getUserSize().width + "-height=" + getUserSize().height + "]";
    }

    private void setUserSizeFromString(String str) {
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
    private static final Logger LOG = Logger.getLogger(Settings.class.getName());

    /**
     *
     * @param userSize
     */
    public void setUserSize(Dimension userSize) {
        this.userSize = userSize;
    }

    /**
     *
     * @param width
     * @param height
     */
    public void setUserSize(int width, int height) {
        this.userSize = new Dimension(width, height);
    }

    @Override
    public String toString() {
        return "Settings{"
                + "theme=" + getTheme()
                + ",userDir=" + getUserDir()
                + ",windowState=" + getWindowState()
                + "," + getUserSizeString() + "}"
                + ",userIdleSeconds=" + getUserIdleSeconds() + "}";
    }

    public String getFolderSlash() {
        if (System.getProperty(defaultDir).contains("\\")) {
            return "\\";
        } else {
            return "/";
        }
    }

    public int getUserIdleSeconds() {
        if (userIdleSeconds == -1) {
            return defaultIdleSeconds;
        } else {
            return userIdleSeconds;
        }
    }

    public void setUserIdleSeconds(int userIdleSeconds) {
        if(userIdleSeconds == defaultIdleSeconds){
            userIdleSeconds = -1;
        }
        this.userIdleSeconds = userIdleSeconds;
    }
}

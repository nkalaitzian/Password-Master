package Other;

/**
 *
 * @author Nikos
 */
public class Login {

    private int id;
    private String website = "";
    private String websiteHidden = "";
    private static boolean hideWebsite = false;
    private String username = "";
    private String usernameHidden = "";
    private static boolean hideUsername = true;
    private String password = "";
    private String passwordHidden = "";
    private String other = "";
    private String otherHidden = "";
    private static boolean hideOther = false;

    /**
     *
     * @param id
     * @param website
     * @param username
     * @param password
     * @param other
     */
    public Login(int id, String website, String username, String password, String other) {
        this.id = id;
        this.website = website;
        this.username = username;
        this.password = password;
        for (int i = 0; i < 10; i++) {
            websiteHidden += "●";
        }
        for (int i = 0; i < 10; i++) {
            usernameHidden += "●";
        }
        for (int i = 0; i < 10; i++) {
            passwordHidden += "●";
        }
        for (int i = 0; i < 10; i++) {
            otherHidden += "●";
        }
        this.other = other;
    }

    public Login() {
    }

    public static Login fromString(String s) {
        s = s.replace("--!--", "");
        String[] temp = s.split("---");
        if (temp.length == 5) {
            int id = new Integer(temp[0].replace("id:", ""));
            String website = temp[1].replace("website:", "");
            String username = temp[2].replace("username:", "");
            String password = temp[3].replace("password:", "");
            String other = temp[4].replace("other:", "");
            Login login = new Login(id, website, username, password, other);
            return login;
        } else if (temp.length == 4) {
            String website = temp[0].replace("website:", "");
            String username = temp[1].replace("username:", "");
            String password = temp[2].replace("password:", "");
            String other = temp[3].replace("other:", "");
            Login login = new Login(0, website, username, password, other);
            return login;
        }
        return null;
    }

    /**
     *
     * @return
     */
    public String getWebsite() {
        return website;
    }

    /**
     *
     * @param website
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *
     * @return
     */
    public String getOther() {
        return other;
    }

    /**
     *
     * @param other
     */
    public void setOther(String other) {
        this.other = other;
    }

    /**
     *
     * @return
     */
    public int getIntId() {
        return id;
    }

    /**
     *
     * @return
     */
    public String getId() {
        return id + "";
    }

    /**
     *
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @param id
     */
    public void setId(String id) {
        this.id = new Integer(id);
    }

    public static boolean isHideWebsite() {
        return hideWebsite;
    }

    public static void setHideWebsite(boolean hideWebsite) {
        Login.hideWebsite = hideWebsite;
    }

    public static boolean isHideUsername() {
        return hideUsername;
    }

    public static void setHideUsername(boolean hideUsername) {
        Login.hideUsername = hideUsername;
    }

    public static boolean isHideOther() {
        return hideOther;
    }

    public static void setHideOther(boolean hideOther) {
        Login.hideOther = hideOther;
    }

    @Override
    public String toString() {
        return "id:" + id + "---website:" + website + "---username:" + username + "---password:" + password + "---other:" + other + "--!--";
    }

    /**
     *
     * @param i
     * @return
     */
    public Object[] toObject() {
        Object[] obj = new Object[]{id + "", website, username, password, other};
        return obj;
    }

    /**
     *
     * @return
     */
    public Object[] toObjectHidden() {
        String website = this.website;
        if (isHideWebsite()) {
            website = websiteHidden;
        }
        String username = this.username;
        if (isHideUsername()) {
            username = usernameHidden;
        }
        String other = this.other;
        if (isHideOther()) {
            other = otherHidden;
        }
        Object[] obj = new Object[]{id + "", website, username, passwordHidden, other};
        return obj;
    }

    public static String getStringPreferences() {
        String prefs = "LoginSettings{";
        if (isHideWebsite()) {
            prefs += "hideWebsite=true";
        } else {
            prefs += "hideWebsite=false";
        }
        if (isHideUsername()) {
            prefs += ",hideUsername=true";
        } else {
            prefs += ",hideUsername=false";
        }
        if (isHideOther()) {
            prefs += ",hideOther=true";
        } else {
            prefs += ",hideOther=false";
        }
        prefs += "}";
        return prefs;
    }

    public static void setPreferencesFromString(String string) {
        if (!string.contains("LoginSettings")) {
            return;
        }
        System.out.println(string);
        string = string.replaceAll("LoginSettings", "");
        string = string.replaceAll("\\{", "");
        string = string.replaceAll("\\}", "");
        System.out.println(string);
        String[] stringArray = string.split(",");
        for (String str : stringArray) {
            if (str.contains("hideWebsite=")) {
                str = str.replace("hideWebsite=", "");
                setHideWebsite(Boolean.valueOf(str));
            } else if (str.contains("hideUsername=")) {
                str = str.replace("hideUsername=", "");
                setHideUsername(Boolean.valueOf(str));
            } else if (str.contains("hideOther=")) {
                str = str.replace("hideOther=", "");
                setHideOther(Boolean.valueOf(str));
            }
        }
    }
}

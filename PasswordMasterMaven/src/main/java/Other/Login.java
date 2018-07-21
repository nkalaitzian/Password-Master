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

/**
 *
 * @author Nikos
 */
public class Login {

    private int id;
    private String strID;
    public int numberOfZeroes;
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
    private boolean favorite = false;

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
        favorite = false;
    }
    
    /**
     *
     * @param id
     * @param website
     * @param username
     * @param password
     * @param other
     * @param favorite
     */
    public Login(int id, String website, String username, String password, String other, boolean favorite) {
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
        this.favorite = favorite;
    }

    public Login() {
    }

    public static Login fromString(String s) {
        s = s.replace("--!--", "");
        String[] temp = s.split("---");
        String idStr = null, website = null, username = null, password = null, other = null;
        boolean favorite = false;
        for(String t: temp) {
            if(t.contains("id:")){
                idStr = t.replaceAll("id:", "");
            } else if(t.contains("website:")){
                website = t.replaceAll("website:", "");
            } else if(t.contains("username:")){
                username = t.replaceAll("username:", "");
            } else if(t.contains("password:")){
                password = t.replaceAll("password:", "");
            } else if(t.contains("other:")){
                other = t.replaceAll("other:", "");
            } else if(t.contains("favorite:")){
                try {
                    favorite = Boolean.valueOf(t.replaceAll("favorite:", ""));
                } catch (Exception e) {
                    favorite = false;
                }
            }
        }
        if(website == null || username == null || password == null || other == null){
            return null;
        }
        int id = 0;
        try {
            id = Integer.valueOf(idStr);
        } catch (Exception ex){
            id = 0;
        }
        Login login;
        if(favorite){
            login = new Login(id, website, username, password, other, favorite);
        } else {
            login = new Login(id, website, username, password, other);
        }
        
        return login;
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
        this.website = website.replaceAll("--!--", "").replaceAll("---", "");
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
        this.username = username.replaceAll("--!--", "").replaceAll("---", "");
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
        this.password = password.replaceAll("--!--", "").replaceAll("---", "");
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
        this.other = other.replaceAll("--!--", "").replaceAll("---", "");
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

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
    
    public void setNumberOfZeroes(int numberOfZeroes){
        this.numberOfZeroes = numberOfZeroes;
        strID = "";
        if(numberOfZeroes != 0){
            for(int i = 0; i<numberOfZeroes ; i++){
                strID += "0";
            }
        }
        strID += getId();
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
        return "id:" + id + "---website:" + website + "---username:" + username + "---password:" + password + "---other:" + other + "---favorite:" + favorite + "--!--";
    }

    /**
     *
     * @return
     */
    public Object[] toObject() {
        Object[] obj = new Object[]{strID + "", website, username, password, other, favorite};
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
        Object[] obj = new Object[]{strID + "", website, username, passwordHidden, other, favorite};
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
        string = string.replaceAll("LoginSettings", "");
        string = string.replaceAll("\\{", "");
        string = string.replaceAll("\\}", "");
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

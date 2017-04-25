package passwordMaster;

/**
 *
 * @author Nikos
 */
public class Login {

    private String website = "";
    private String username = "";
    private String password = "";
    private String passwordHidden = "";
    private String other = "";

    /**
     *
     * @param website
     * @param username
     * @param password
     */
    public Login(String website, String username, String password) {
        this.website = website;
        this.username = username;
        this.password = password;
        for (int i = 0; i < 10; i++) {
            passwordHidden += "●";
        }
        this.other = "";
    }

    /**
     *
     * @param website
     * @param username
     * @param password
     * @param other
     */
    public Login(String website, String username, String password, String other) {
        this.website = website;
        this.username = username;
        this.password = password;
        for (int i = 0; i < 10; i++) {
            passwordHidden += "●";
        }
        this.other = other;
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

    @Override
    public String toString() {
        return "website:" + website + "---username:" + username + "---password:" + password + "---other:" + other + "--!--";
    }

    /**
     *
     * @param i
     * @return
     */
    public Object[] toObject(String i) {
        Object[] obj = new Object[]{i, website, username, password, other};
        return obj;
    }

    /**
     *
     * @param i
     * @return
     */
    public Object[] toObjectHidden(String i) {
        Object[] obj = new Object[]{i, website, username, passwordHidden, other};
        return obj;
    }

}

package nikoskalai.passwordmasterandroid;

public class Login {


    private int image;
    private boolean isFavorite;
    private int id;
    private String website = "";
    private String username = "";
    private String password = "";
    private String other = "";

    public Login(int id) {
        this.id = id;
    }

    public Login(int id, String website, String username, String password, String other) {
        this.id = id;
        this.website = website;
        this.username = username;
        this.password = password;
        this.other = other;
        isFavorite = false;
        image = Integer.valueOf(R.drawable.ic_add_circle_outline_black_24dp);
    }

    public int getId() {
        return id;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}

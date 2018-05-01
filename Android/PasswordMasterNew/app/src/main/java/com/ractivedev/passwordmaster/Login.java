package com.ractivedev.passwordmaster;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class Login {

	private String website,
			username,
			password,
			title,
			other;

	private int image;
	private Uri imageUri;
	private boolean favorite;
	private int id;

	public Login(int id){
		this.id = id;
		website = "";
		username = "";
		password = "";
		title = "Login";
		favorite = false;
		image = Integer.valueOf(R.drawable.add_icon_black);
	}

	public Login(int id, String website, String username, String password, String other, String title, int image, boolean favorite) {
		setId(id);
		setWebsite(website);
		setUsername(username);
		setPassword(password);
		setOther(other);
		setTitle(title);
		setImage(image);
		setFavorite(favorite);
	}

	public Login(int id, String website, String username, String password, String other, String title, Uri imageUri, boolean favorite) {
		setId(id);
		setWebsite(website);
		setUsername(username);
		setPassword(password);
		setOther(other);
		setTitle(title);
		setImage(R.drawable.add_icon_black);
		setImageURI(imageUri);
		setFavorite(favorite);
	}

	public static Login fromString(String s){
		s = s.replace("--!--", "");
		String[] temp = s.split("---");
		if (temp.length == 8) {
			int id = Integer.valueOf(temp[0].replace("id:", ""));
			String website = temp[1].replace("website:", "");
			String username = temp[2].replace("username:", "");
			String password = temp[3].replace("password:", "");
			String other = temp[4].replace("other:", "");
			String title = temp[5].replace("title:", "");
			String fav = temp[7].replace("FAV:", "");
			return parseImage(temp[6], id,  website, username, password, other, title, fav);
		} else if (temp.length == 5){
			int id = Integer.valueOf(temp[0].replace("id:", ""));
			String website = temp[1].replace("website:", "");
			String username = temp[2].replace("username:", "");
			String password = temp[3].replace("password:", "");
			String other = temp[4].replace("other:", "");
			String title = "Login";
			int img = R.drawable.add_icon_black;
			boolean fav = false;
			Login login = new Login(id, website, username, password, other, title, img, fav);
			return login;
		}
		return null;
	}

	private static Login parseImage(String s, int id, String website, String username, String password, String other, String title, String fav){
		Login login;
		if(s.contains("IMG:")) {
			String img = s.replace("IMG:", "");
			if(img.equals("0")){
				login = new Login(id, website, username, password, other, title, R.drawable.add_icon_black, Boolean.parseBoolean(fav));
				return login;
			}
			try {
				int image = Integer.valueOf(img);
				login = new Login(id, website, username, password, other, title, image, Boolean.parseBoolean(fav));
			} catch (NumberFormatException ex){
				login = new Login(id, website, username, password, other, title, R.drawable.add_icon_black, Boolean.parseBoolean(fav));
			}
		} else {
			String imgUri = s.replace("IMGURI:", "");
			login = new Login(id, website, username, password, other, title, Uri.parse(imgUri), Boolean.parseBoolean(fav));
		}
		return login;
	}

	public int getId () {
		return id;
	}

	public void setId (int id) {
		this.id = id;
	}

	public String getPassword () {
		return password;
	}

	public void setPassword (String password) {
		this.password = password;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getUsername () {
		return username;
	}

	public void setUsername (String username) {
		if(username.equals("null")){
			username = "";
		}
		this.username = username;
	}

	public Bitmap getImage (Context context) {
		if(imageUri == null) {
			if(image != 0) {
				return BitmapFactory.decodeResource(context.getResources(), image);
//				return BitmapFactory.decodeResource(context.getResources(), R.drawable.add_icon_black);
			} else {
				return BitmapFactory.decodeResource(context.getResources(), R.drawable.add_icon_black);
			}
		} else {
			try {
				Bitmap bmp =  MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
				return bmp;
			} catch (IOException e) {
				Log.e("ERROR", "getImage Exception", e);
				imageUri = null;
				return BitmapFactory.decodeResource(context.getResources(), image);
			}
		}
	}

	public Uri getImageURI(){
		return imageUri;
	}

	private String getImageSTR(){
		if(imageUri == null){
			return "IMG:" + image;
		}else {
			return "IMGURI:" + imageUri;
		}
	}

	public boolean hasCustomImage() {
		return imageUri != null;
	}

	public void setImage (int image) {
		this.image = image;
	}

	public void setImageURI(Uri uri){
		imageUri = uri;
	}

	public boolean getFavorite () {
		return favorite;
	}

	public void setFavorite (boolean favorite) {
		this.favorite = favorite;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOther() { return other; }

	public void setOther(String other){
		if(other.equals("null")){
			other = "";
		}
		this.other = other;
	}

	@Override
	public String toString() {
		return "id:" + id + "---website:" + website + "---username:" + username + "---password:" + password + "---other:" + other + "---title:" + title + "---" + getImageSTR() + "---FAV:" + favorite + "--!--";
	}

	public String toPmasterString() {
		return "id:" + id + "---website:" + website + "---username:" + username + "---password:" + password + "---other:" + other + "--!--";
	}

	public void resetImage() {
		setImage(R.drawable.add_icon_black);
		deleteImageUri();
	}

	public void deleteImageUri() {
		if(hasCustomImage()){
			try {
				File file = new File(imageUri.getPath());
				file.delete();
				imageUri = null;
			} catch (Exception ex){
				Log.e("ERROR", "", ex);
			}
		}
	}
}

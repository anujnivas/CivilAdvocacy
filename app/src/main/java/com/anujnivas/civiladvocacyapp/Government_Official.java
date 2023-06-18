package com.anujnivas.civiladvocacyapp;

import android.util.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

public class Government_Official implements Serializable {
    String title;
    String name;
    String party;
    String address;
    String phone;
    String portfolio_url;
    String office_url;
    String email_id;
    String image_url;
    String youtube_url;
    String facebook_url;
    String twitter_url;

    public Government_Official(String title, String name, String party, String address, String phone, String email_id ,String portfolio_url, String office_url, String image_url, String youtube_url, String facebook_url, String twitter_url) {
        this.title = title;
        this.name = name;
        this.party = party;
        this.address = address;
        this.phone = phone;
        this.portfolio_url = portfolio_url;
        this.email_id = email_id;
        this.office_url = office_url;
        this.image_url = image_url;
        this.youtube_url = youtube_url;
        this.facebook_url = facebook_url;
        this.twitter_url = twitter_url;
    }

    public String getParty() {
        return party;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getFacebook_url() {
        return facebook_url;
    }

    public String getEmail_id() {
        return email_id;
    }

    public String getTwitter_url() {
        return twitter_url;
    }

    public String getYoutube_url() {
        return youtube_url;
    }

    public String getOffice_url() {
        return office_url;
    }

    public String getImage_url() {
        return image_url==null?null:image_url.replace("https","http").replace("http","https");
    }
}

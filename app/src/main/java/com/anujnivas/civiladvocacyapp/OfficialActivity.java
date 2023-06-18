package com.anujnivas.civiladvocacyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.regex.Pattern;

public class OfficialActivity extends AppCompatActivity {

    Government_Official government_official;
    TextView title, email, email_holder, address_holder, website_holder, phone_holder;
    TextView name;
    TextView party;
    TextView address;
    TextView phone;
    TextView website, location_tv;
    ImageView fb, twitter, youtube, image, partylogo;
    ScrollView scrollView;
    String location;
    private final String TAG = getClass().getSimpleName();
    private Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);
        picasso = Picasso.get();
        Intent intent = getIntent();
        setTitle("Know Your Government");
        if (intent.hasExtra("info")) {
            government_official = (Government_Official) intent.getSerializableExtra("info");
        }
        if (intent.hasExtra("location")) {
            location = intent.getStringExtra("location");
            location_tv = findViewById(R.id.location_tv);
            location_tv.setText(location);
        }

        scrollView = findViewById(R.id.s2_scroll_layout);
        scrollView.setBackgroundColor(getResources().getColor(R.color.black));
        title = findViewById(R.id.s2_title);
        name = findViewById(R.id.s2_name);
        party = findViewById(R.id.s2_party);
        address = findViewById(R.id.s2_address);
        partylogo = findViewById(R.id.imageViewParty);
        phone = findViewById(R.id.s2_phone);
        email = findViewById(R.id.s2_email);
        website = findViewById(R.id.s2_website);
        website_holder = findViewById(R.id.s2_website_holder);
        address_holder = findViewById(R.id.s2_address_holder);
        phone_holder = findViewById(R.id.s2_phone_holder);
        email_holder = findViewById(R.id.s2_email_holder);
        fb = findViewById(R.id.imageViewFB);
        twitter = findViewById(R.id.imageViewTwitter);
        youtube = findViewById(R.id.imageViewYoutube);
        image = findViewById(R.id.s2_imageView);
        partylogo.setVisibility(View.INVISIBLE);
        setInvisible();
        if (government_official.getParty() != null) {
            if (government_official.getParty().equals("Democratic Party")) {
                partylogo.setImageResource(R.drawable.dem_logo);
                partylogo.setVisibility(View.VISIBLE);
                scrollView.setBackgroundColor(getResources().getColor(R.color.democratic_color));

            } else if (government_official.getParty().equals("Republican Party")) {
                partylogo.setImageResource(R.drawable.rep_logo);
                partylogo.setVisibility(View.VISIBLE);
                scrollView.setBackgroundColor(getResources().getColor(R.color.republic_color));

            } else {
                partylogo.setVisibility(View.INVISIBLE);
            }
        }
        if (government_official.getTitle() != null) {
            title.setText(government_official.getTitle());
            title.setVisibility(View.VISIBLE);
        }
        if (government_official.getName() != null) {
            name.setText(government_official.getName());
            name.setVisibility(View.VISIBLE);

        }
        if (government_official.getParty() != null) {
            party.setText("(" + government_official.getParty() + ")");
            party.setVisibility(View.VISIBLE);

        }
        if (government_official.getAddress() != null) {
            address.setText(government_official.getAddress());
            address.setVisibility(View.VISIBLE);
            address_holder.setVisibility(View.VISIBLE);
            Pattern pattern = Pattern.compile(".*", Pattern.DOTALL);
            address.setLinkTextColor(Color.WHITE);
            Linkify.addLinks(address, pattern, "geo:0,0?q=");
        }
        if (government_official.getPhone() != null) {
            phone.setText(government_official.getPhone());
            phone.setVisibility(View.VISIBLE);
            phone_holder.setVisibility(View.VISIBLE);
            phone.setLinkTextColor(Color.WHITE);

            Linkify.addLinks(phone, Linkify.ALL);
        }
        if (government_official.getEmail_id() != null) {
            email.setText(government_official.getEmail_id());
            email.setVisibility(View.VISIBLE);
            email_holder.setVisibility(View.VISIBLE);
            Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);
            email.setLinkTextColor(Color.WHITE);

        }
        if (government_official.getOffice_url() != null) {
            website.setText(government_official.getOffice_url());
            website_holder.setVisibility(View.VISIBLE);
            website.setVisibility(View.VISIBLE);
            Linkify.addLinks(website, Linkify.ALL);
            website.setLinkTextColor(Color.WHITE);
        }
        if (government_official.getFacebook_url() != null) {
            fb.setVisibility(View.VISIBLE);
        }
        if (government_official.getYoutube_url() != null) {
            youtube.setVisibility(View.VISIBLE);
        }
        if (government_official.getTwitter_url() != null) {
            twitter.setVisibility(View.VISIBLE);
        }
        if (government_official.getImage_url() != null) {
            loadRemoteImage(government_official.getImage_url());
        }
//        if(!hasNetworkConnection()){
//            location_tv.setText("No Internet Connection");
//        }
    }

    private void setInvisible() {
        fb.setVisibility(View.GONE);
        twitter.setVisibility(View.GONE);
        youtube.setVisibility(View.GONE);

        address.setVisibility(View.GONE);
        phone.setVisibility(View.GONE);
        website.setVisibility(View.GONE);
        email.setVisibility(View.GONE);
        email_holder.setVisibility(View.GONE);
        address_holder.setVisibility(View.GONE);
        website_holder.setVisibility(View.GONE);
        phone_holder.setVisibility(View.GONE);
    }

    private void loadRemoteImage(String imageURL) {
        // Needs gradle  implementation 'com.squareup.picasso:picasso:2.71828'
        picasso.load(imageURL)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(image);
    }

    public void twitterClicked(View v) {
        Intent intent = null;
        String name = government_official.getTwitter_url();
        try { // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) { // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    public void facebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/" + government_official.getFacebook_url();
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else {
                //older versions of fb app
//                 urlToUse = "fb://page/" + channels.get("Facebook");
                urlToUse = "";
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }


    public void youTubeClicked(View v) {
        String name = government_official.getYoutube_url();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + name)));
        }
    }

    public void detailedView(View v) {
        if(government_official.getImage_url()==null)
            return;
        Intent intent = new Intent(OfficialActivity.this, PhotoDetailActivity.class);
        intent.putExtra("info", government_official);
        intent.putExtra("address", location);
        startActivity(intent);
    }

    public void partyLogoClick(View v) {
        String url = government_official.getParty().equals("Republican Party") ? "https://www.gop.com" : "https://democrats.org";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }
}
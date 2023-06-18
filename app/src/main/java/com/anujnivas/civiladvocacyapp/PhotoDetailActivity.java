package com.anujnivas.civiladvocacyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends AppCompatActivity {
    private Picasso picasso;
    ImageView imageView,imageViewDetailedParty;
    private final String TAG = getClass().getSimpleName();
    ConstraintLayout constraintLayout;
    TextView location_header;
    TextView name;
    TextView Title;
    Government_Official government_official;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        setTitle("Civil Advocacy");
        picasso=Picasso.get();
        imageView=findViewById(R.id.imageViewDetailed);
        constraintLayout=findViewById(R.id.detailed_constraint_layout);
        imageViewDetailedParty=findViewById(R.id.imageViewDetailedParty);
        location_header=findViewById(R.id.photoDetailLocationTV);
        Title=findViewById(R.id.photoDetailTitleTV);
        name=findViewById(R.id.photoDetailNameTV);
        Intent i=getIntent();
        government_official=(Government_Official) i.getSerializableExtra("info");

        location_header.setText(i.getStringExtra("address"));
        name.setText(government_official.getName());
        Title.setText(government_official.getTitle());
        loadRemoteImage(government_official.getImage_url());
        if(government_official.getParty()!=null){
            if(government_official.getParty().equals("Democratic Party")){
                imageViewDetailedParty.setImageResource(R.drawable.dem_logo);
                imageViewDetailedParty.setVisibility(View.VISIBLE);
                constraintLayout.setBackgroundColor(getResources().getColor(R.color.democratic_color));

            }else  if(government_official.getParty().equals("Republican Party")){
                imageViewDetailedParty.setImageResource(R.drawable.rep_logo);
                imageViewDetailedParty.setVisibility(View.VISIBLE);
                constraintLayout.setBackgroundColor(getResources().getColor(R.color.republic_color));

            }else{
                imageViewDetailedParty.setVisibility(View.INVISIBLE);
            }
        }
//        if(!hasNetworkConnection()){
//            location_header.setText("No Internet Connection");
//        }
    }
    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }
    private void loadRemoteImage(String imageURL) {
        // Needs gradle  implementation 'com.squareup.picasso:picasso:2.71828'
        if(imageURL==null)
            return;
        picasso.load(imageURL)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(imageView);
    }

    public void partyLogoClick(View v) {
        if(government_official.getImage_url()==null)
            return;
        String url = government_official.getParty().equals("Republican Party")?"https://www.gop.com":"https://democrats.org";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
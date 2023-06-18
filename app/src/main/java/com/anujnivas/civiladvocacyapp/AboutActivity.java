package com.anujnivas.civiladvocacyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends AppCompatActivity {
    TextView credits_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle("Civic Advocacy");
        credits_tv=findViewById(R.id.attrition_tv);
        credits_tv.setPaintFlags(credits_tv.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

    }

    public void AboutTextClick(View v) {
        String url = "https://developers.google.com/civic-information/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
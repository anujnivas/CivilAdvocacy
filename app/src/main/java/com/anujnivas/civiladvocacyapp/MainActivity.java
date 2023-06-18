package com.anujnivas.civiladvocacyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final ArrayList<Government_Official> officials_ArrayList = new ArrayList<>();
    private static final String TAG = "MainActivity";
    private RequestQueue queue;
    private static String URL = "https://www.googleapis.com/civicinfo/v2/representatives";
    private static String KEY = "AIzaSyCqjXSyx7yUyiFFGELKCibgzavVoR8Wifs";
    private String location1 = "500E 33rd Street Chicago";
    RecyclerView list_recyclerView;
    MainActivityListAdapter mainActivityListAdapter;
    TextView location_tv;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Know your Government");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if(hasNetworkConnection())
            determineLocation();
        list_recyclerView = findViewById(R.id.list_recycler_view);
        mainActivityListAdapter = new MainActivityListAdapter(this, officials_ArrayList);
        list_recyclerView.setAdapter(mainActivityListAdapter);
        list_recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        location_tv = findViewById(R.id.location_header_tv);
        location_tv.setText(location1);
        queue = Volley.newRequestQueue(this);
        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_opt, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.location) {
            changeLocation();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        if (!hasNetworkConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Data cannot be accessed/loaded without an internet connection");

            AlertDialog dialog = builder.create();
            dialog.show();
            location_tv.setText("No Network Connection");
            return;
        }
        Uri.Builder buildUrl = Uri.parse(URL).buildUpon();
        buildUrl.appendQueryParameter("key", KEY);
        buildUrl.appendQueryParameter("address", location1);
        String urlToUse = buildUrl.build().toString();
        Response.Listener<JSONObject> listener = response -> {
            try {
                populateArrayList(response);
            } catch (Exception e) {
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
                Log.d(TAG, "connect: " + e.getMessage());
            }
        };
        Response.ErrorListener error = error1 -> {
            try {
                JSONObject jsonObject = new JSONObject("{}");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("Information could not be found. Please try again");

                AlertDialog dialog = builder.create();
                dialog.show();
                officials_ArrayList.clear();
                mainActivityListAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);
        queue.add(jsonObjectRequest);
    }

    @Override
    public void onClick(View view) {
        int pos = list_recyclerView.getChildLayoutPosition(view);
        Intent intent = new Intent(MainActivity.this, OfficialActivity.class);
        Government_Official temp = officials_ArrayList.get(pos);
        intent.putExtra("info", temp);
        intent.putExtra("location", location1);
        startActivity(intent);
    }

    private void populateArrayList(JSONObject response) {
        officials_ArrayList.clear();
        try {
            JSONObject locationJsonDetails = response.getJSONObject("normalizedInput");

            String address_set="";
            if(locationJsonDetails.has("line1")){
                address_set+=locationJsonDetails.get("line1").toString();
                if(address_set.length()>0)
                    address_set+=", ";
            }
            if(locationJsonDetails.has("city")){
                address_set+=locationJsonDetails.get("city").toString();
                if(address_set.length()>0)
                    address_set+=", ";
            }
            if(locationJsonDetails.has("state")){
                address_set+=locationJsonDetails.get("state").toString();
            }
            if(locationJsonDetails.has("zip")){
                if(locationJsonDetails.get("zip").toString().length()>0)
                    address_set+=", ";
                address_set+=locationJsonDetails.get("zip").toString();
            }
            location_tv.setText(address_set);
            JSONArray offices = response.getJSONArray("offices");
            for (int i = 0; i < offices.length(); i++) {
                JSONObject row = offices.getJSONObject(i);
                String OfficeTitle = row.getString("name");
                JSONArray Official_array = row.getJSONArray("officialIndices");
                for (int j = 0; j < Official_array.length(); j++) {
                    String index = Official_array.get(j).toString();
                    populate_a(response, OfficeTitle, Integer.parseInt(index));
                }
            }
            mainActivityListAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            Log.d(TAG, "connect: " + e.getMessage());
        }


    }

    private void populate_a(JSONObject response, String OfficeTitle, int i) throws JSONException {
        JSONArray aa = response.getJSONArray("officials");
        JSONObject row = aa.getJSONObject(i);
        String name = row.getString("name");
        String party = row.getString("party");
        String address = null;
        String urls = null;
        String emails = null;
        String photoUrl = null;
        String phone = null;

        if (row.has("address")) {
            JSONObject temp = row.getJSONArray("address").getJSONObject(0);
            String line1 = temp.has("line1") ? temp.getString("line1") + ", " : "";
            String line2 = temp.has("line2") ? temp.getString("line2") + ", " : "";
            String city = temp.has("city") ? temp.getString("city") + ", " : "";
            String state = temp.has("state") ? temp.getString("state") + " " : "";
            String zip = temp.has("zip") ? temp.getString("zip") : "";
            address = line1 + line2 + city + state + zip;
        }

        if (row.has("phones"))
            phone = row.getJSONArray("phones").get(0).toString();
        if (row.has("urls"))
            urls = row.getJSONArray("urls").get(0).toString();
        if (row.has("emails"))
            emails = row.getJSONArray("emails").get(0).toString();
        if (row.has("photoUrl")) {
            photoUrl = row.getString("photoUrl");
        }
        String twitter_url = null;
        String facebook_url = null;
        String youtube_url = null;
        if (row.has("channels")) {
            JSONArray channels = row.getJSONArray("channels");
            for (int j = 0; j < channels.length(); j++) {
                String type = channels.getJSONObject(j).get("type").toString();
                if (type.toLowerCase().equals("twitter")) {
                    twitter_url = channels.getJSONObject(j).get("id").toString();
                } else if (type.toLowerCase().equals("facebook")) {
                    facebook_url = channels.getJSONObject(j).get("id").toString();

                } else if (type.toLowerCase().equals("youtube")) {
                    youtube_url = channels.getJSONObject(j).get("id").toString();
                }

            }
        }

        Government_Official go = new Government_Official(OfficeTitle, name, party, address, phone, emails, urls, urls, photoUrl, youtube_url, facebook_url, twitter_url);
        officials_ArrayList.add(go);
    }

    @SuppressLint("MissingPermission")
    private void determineLocation() {
        if (checkAppPermissions()) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        // Got last known location. In some situations this can be null.
                        if (location != null) {
                            location1 = getPlace(location);
                            officials_ArrayList.clear();
                            loadData();
//                            location_tv.setText(location1);

                        }
                    })
                    .addOnFailureListener(this, e -> Toast.makeText(MainActivity.this,
                            e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    private boolean checkAppPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_REQUEST);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                } else {
//                    textView.setText(R.string.deniedText);
                }
            }
        }
    }

    private String getPlace(Location loc) {

        StringBuilder sb = new StringBuilder();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            if (addresses.size() == 0) {
                sb.append("60616");
            } else {
                String addressLine = addresses.get(0).getAddressLine(0);
                sb.append(String.format(
                        Locale.getDefault(),
                        "%s",
                        addressLine));
            }

        } catch (IOException e) {
            Toast.makeText(this,"GRPC failed! Defaulting to Chicago. :(",Toast.LENGTH_LONG).show();
            return "Chicago, IL, 60616";
        }
        return sb.toString();
    }

    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    public void changeLocation() {
        // Single input value dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create an edittext and set it to be the builder's view
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et);

        // lambda can be used here (as is below)
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                location1=et.getText().toString();
                location1=doLocationName();
//                location_tv.setText(location1);
                loadData();
            }
        });
        // lambda can be used here (as is below)
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Do Nothing
            }
        });

        builder.setMessage("For US locations only, enter as 'City', or 'City,State' or a ZipCode");
        builder.setTitle("Enter a Location");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public String doLocationName() {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> arr = geocoder.getFromLocationName(location1, 10);
            if (arr.size() == 0) {
                Toast.makeText(this, "Empty location name returned from GeoCoder.", Toast.LENGTH_LONG).show();
                return location1;
            }
            Address address = arr.get(0);
            String temp="";
//            String temp = (address.getLocality() == null ? (address.getAdminArea() != null ? address.getAdminArea() + ", " : "") : address.getLocality() + ", ") + address.getCountryName();
//            if (address.getCountryCode().equals("US")){
//                temp = address.getLocality() + ", " + address.getAdminArea() + ", " + address.getPostalCode();
                if(address.getLocality()!=null){
                    temp+=address.getLocality();
                }
                if(temp.length()>0)
                    temp+=", ";
                if(address.getAdminArea()!=null){
                    temp+=address.getAdminArea();
                }
                if(address.getPostalCode()!=null){
                    temp+=", "+address.getPostalCode();
                }

            return temp;
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return "";
    }
}
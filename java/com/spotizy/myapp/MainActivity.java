package com.spotizy.myapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ListIterator;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, PlaceSelectionListener  {

    private static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 1;
    private Context context;
    private ArrayList<ActivityData> activities;
    private Spinner interestList;
    //private Button showInterestList;
    private ImageButton createActivity;
    private Button createUser;

    private double latitude;
    private double longitude;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    private ArrayList<String> interests;
    private TextView mPlaceDetailsText;

    private TextView mPlaceAttribution;

    private String interestSelected;
    private PlaceAutocompleteFragment autocompleteFragment;

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private String username;
    private String userid;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("#####  Before Main");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUserContext();
        getUserContext();

        //showInterestList = (Button)this.findViewById(R.id.interest_cancel);
        createActivity = (ImageButton) this.findViewById(R.id.create_activity);
        createUser = (Button) this.findViewById(R.id.create_user);

        //activityAddress = (EditText)this.findViewById(R.id.address);
        //searchAddress = (Button)this.findViewById(R.id.search);
        //refreshActivities = (Button)this.findViewById(R.id.refresh_activities);
        interestList = (Spinner) this.findViewById(R.id.interest_list);

        mActivityTitle = getTitle().toString();
        //debugInfo = (EditText)this.findViewById(R.id.debug_info);
        //this.layoutIflator = LayoutInflater.from(this);
        context = this.getApplicationContext();

        mDrawerList = (ListView)findViewById(R.id.options_list);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Register a listener to receive callbacks when a place has been selected or an error has
        // occurred.
        autocompleteFragment.setOnPlaceSelectedListener(this);

        // Retrieve the TextViews that will display details about the selected place.
        //mPlaceDetailsText = (TextView) findViewById(R.id.place_details);
        //mPlaceAttribution = (TextView) findViewById(R.id.place_attribution);

        interestSelected = "all";

        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MainActivity.MY_PERMISSION_ACCESS_COURSE_LOCATION);
        }

        //webView = (WebView)this.findViewById(R.id.json_display);
        System.out.println("#####  Main");

        latitude = longitude = -1;

        /*
        Get current location.
         */


        updateLocation();
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_show);
        mapFragment.getMapAsync(this);
        //webView = (WebView)this.findViewById(R.id.json_display);
        /*
        Get current location.
         */
        System.out.println("#####  Done with page");
        GPSTracker tracker = new GPSTracker(this);
        if (!tracker.canGetLocation()) {
            System.out.println("#####   Did not find tracker");
            tracker.showSettingsAlert();
        } else {
            latitude = tracker.getLatitude();
            longitude = tracker.getLongitude();
            System.out.println("#### Found location latitude = "+latitude+" longitude = "+longitude);
        }
        //Intent intent = new Intent(getApplicationContext(), InterestActivity.class);
        WebApiDataTask interestDataFetcher = new WebApiDataTask(MainActivity.this);
        try {
            LinkedHashMap<String,String> getParams=new LinkedHashMap<>();
            //check whether the msg empty or not

            String getURL = ServerDataRetriever.createGetURL(getParams);
            //webDataFetcher.execute("http://hospitopedia.com/activity/get?interestid=0&phone=%221234%22&lat="+latitude+"&long="+longitude);

            interestDataFetcher.execute("GET", "interests", getURL);
            //http://aptivity-nodeserver.44fs.preview.openshiftapps.com/activities/interest/0/lat/"+latitude+"/lng/"+longitude);
        } catch (Exception e) {
            //interestDataFetcher.cancel(true);
        }
        //startActivityForResult(intent, 0);
        getActivitiesOnInterest("all");
/*
        searchAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String address = activityAddress.getText().toString();
                List<Address> addressList = null;
                Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
                try {
                    addressList = geoCoder.getFromLocationName(address, 1);
                }
                catch (Exception e) {

                }
                Address myAddress = addressList.get(0);
                latitude = myAddress.getLatitude();
                longitude = myAddress.getLongitude();
                LatLng location = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(location).title("Marker in location"));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12));

                getActivitiesOnInterest(interestSelected);
            }
        });
*/
        interestList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

                interestSelected = interestList.getSelectedItem().toString();
                getActivitiesOnInterest(interestSelected);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // Do nothing

            }

        });

        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //LinkedHashMap<String, String> postParams = new LinkedHashMap<>();
                JSONObject postParams = new JSONObject();
                try {
                    postParams.put("username", "Nandy");
                    postParams.put("imsi", "+1123456789");

                } catch (JSONException e) {
                    System.out.println("Error in JSON");
                }

                WebApiDataTask webDataFetcher = new WebApiDataTask(MainActivity.this);
                try {

                    //check whether the msg empty or not

                    String postURL = postParams.toString();//ServerDataRetriever.createPostURL(postParams);
                    webDataFetcher.execute("POST", "user", postURL);
                } catch (Exception e) {
                    webDataFetcher.cancel(true);
                }
                //finish();

            }
        });


        createActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int resultCode = 0;
                Intent intent = new Intent(getApplicationContext(), CreateActivity.class);
                //intent.putExtra("interestid", 0);
                intent.putExtra("interests", interests);
                startActivityForResult(intent, resultCode);
            }
        });

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPlaceSelected(Place place) {

        latitude = place.getLatLng().latitude;
        longitude = place.getLatLng().longitude;
        LatLng location = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(location).title("Marker in location"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12));

        getActivitiesOnInterest(interestSelected);

/*
        // Format the returned place's details and display them in the TextView.
        mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(), place.getId(),
                place.getAddress(), place.getPhoneNumber(), place.getWebsiteUri()));

        CharSequence attributions = place.getAttributions();
        if (!TextUtils.isEmpty(attributions)) {
            mPlaceAttribution.setText(Html.fromHtml(attributions.toString()));
        } else {
            mPlaceAttribution.setText("");
        }
        */
    }

    public void setActivities(ArrayList<ActivityData> activities) {

        this.activities = activities;
        ActivityData item;

        ListIterator<ActivityData> li = this.activities.listIterator();
        mMap.clear();

        LatLng activityLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(activityLocation).title("My location"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(activityLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(activityLocation, 10));
        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            mMap.setMyLocationEnabled(true);
        }
        while (li.hasNext()) {
            item = li.next();
            activityLocation = new LatLng(item.getLatitude(), item.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions().position(activityLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(item.getActivityName()));
            //marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon));
            marker.setTag(item);
            marker.showInfoWindow();
        }
    }

    protected void getActivitiesOnInterest(String interestId) {
        System.out.println("##### I am here");

        System.out.println("onActivityResult:interestId = "+interestId);
        WebApiDataTask webDataFetcher = new WebApiDataTask(MainActivity.this);
        try {
            //webDataFetcher.execute("http://hospitopedia.com/activity/get?interestid="+interestId+"&phone=%221234%22&lat="+latitude+"&long="+longitude);
            LinkedHashMap<String,String> getParams=new LinkedHashMap<>();
            getParams.put("userid", userid);
            getParams.put("interest", interestId);
            getParams.put("lat", Double.toString(latitude));
            getParams.put("lng", Double.toString(longitude));

            //check whether the msg empty or not

            String getURL = ServerDataRetriever.createGetURL(getParams);
            //webDataFetcher.execute("http://hospitopedia.com/activity/get?interestid=0&phone=%221234%22&lat="+latitude+"&long="+longitude);

            webDataFetcher.execute("GET", "activities", getURL);
            //webDataFetcher.execute("http://aptivity-nodeserver.44fs.preview.openshiftapps.com/activities/interest/"+interestId+"/lat/"+latitude+"/lng/="+longitude);
        } catch (Exception e) {
            webDataFetcher.cancel(true);
        }
        //super.onActivityResult(requestCode, resultCode, data);

    }

    private void updateLocation() {
        System.out.println("#####  Get own position");
        GPSTracker tracker = new GPSTracker(this);
        String debugText = null;
        if (!tracker.canGetLocation()) {
            System.out.println("#####   Did not find tracker");
            debugText = "No Text Found";
            tracker.showSettingsAlert();
        } else {
            latitude = tracker.getLatitude();
            longitude = tracker.getLongitude();
            debugText = "Latitude " + latitude+" longitude "+longitude;
            System.out.println("#### Found location latitude = "+latitude+" longitude = "+longitude);
        }
        //debugInfo.setText(debugText);
        //latitude = 39;
        //longitude = -74;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;


        System.out.println("##### Map is ready now");
        // Add a marker to the activity location
        //latitude = 10;
        //longitude = 100;
        LatLng activityLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(activityLocation).title("My location"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(activityLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(activityLocation, 10));
        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnMarkerClickListener(this);
    }

    public void setInterests(ArrayList<String> interestArray) {
        this.interests = interestArray;
        System.out.println("#####  setData::Interests");
        //for (int i = 0; i < interestArray.size(); i++) {
          //  System.out.println(" Inside setMessages " + interestArray.get(i).getInterestId() + " " + interestArray.get(i).getInterest());
        //}
        ArrayAdapter<String> adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, interestArray);
        interestList.setAdapter(adaptor);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        ActivityData item = (ActivityData)marker.getTag();
        if (item != null) {
            Intent intent = new Intent(this.getApplicationContext(), MessageActivity.class);

            intent.putExtra("interest", item.getInterestId());
            intent.putExtra("activityid", item.getActivityId());
            intent.putExtra("activityname", item.getActivityName());
            intent.putExtra("latitude", item.getLatitude());
            intent.putExtra("longitude", item.getLongitude());
            intent.putExtra("date", item.getDate());
            this.startActivity(intent);
        }
        return false;
    }
    @Override
    public void onError(Status status) {
        System.out.println("Error hit");
    }

    private void addDrawerItems() {
        String[] osArray = { "User details", "Interests", "Activities"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUserContext() {
        UserCredentials.setUserId("thisisme");
        UserCredentials.setIMSI("123456");
        UserCredentials.setName("Aptivity user");
    }

    private void getUserContext() {
        username = UserCredentials.getUserName();
        userid = UserCredentials.getUserId();
    }
}

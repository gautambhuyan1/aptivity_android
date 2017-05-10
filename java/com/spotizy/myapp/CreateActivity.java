package com.spotizy.myapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

//import android.support.v7.app.AppCompactActivity;


public class CreateActivity extends AppCompatActivity implements OnMapReadyCallback, PlaceSelectionListener {

    private Context context;
    private Button createActivity;
    private Button searchAddress;
    private EditText activityAddress;
    private EditText activityName;
    private EditText activityDateTime;
    private EditText interestName;
    private String interestId;
    private double latitude;
    private double longitude;
    private GoogleMap mMap;
    private Spinner interestList;
    private String interestSelected;
    private ArrayList<String> interests;
    private PlaceAutocompleteFragment autocompleteFragment;
    private static String date;
    private String userid;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("#####  In Create Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        getUserContext();
        context = this.getApplicationContext();
        Intent triggerData = this.getIntent();
        if (triggerData != null) {
            this.interests = triggerData.getStringArrayListExtra("interests");
            System.out.println("##### Interest ID is" + this.interestId);
        } else {
            System.out.println("##### No intent data");
        }
        createActivity = (Button) this.findViewById(R.id.create_activity);
        interestList = (Spinner) this.findViewById(R.id.interest_selection);
        activityName = (EditText) this.findViewById(R.id.activity_name);

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.activity_address);

        // Register a listener to receive callbacks when a place has been selected or an error has
        // occurred.
        autocompleteFragment.setOnPlaceSelectedListener(this);

        ArrayAdapter<String> adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, this.interests);
        interestList.setAdapter(adaptor);

        latitude = longitude = -1;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
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
            System.out.println("#### Found location latitude = " + latitude + " longitude = " + longitude);
        }

        interestList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

                interestSelected = interestList.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //interestSelected = interestList.
                // Do nothing

            }

        });


        createActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = activityName.getText().toString();
                String dateAndTime = date;//activityDateTime.getText().toString();
                String interest = interestSelected;
                //LinkedHashMap<String, String> postParams = new LinkedHashMap<>();
                JSONObject postParams = new JSONObject();
                try {
                    postParams.put("userid", userid);
                    postParams.put("username", username);
                    postParams.put("interest", interest);
                    postParams.put("activity", name);
                    postParams.put("lat", Double.toString(latitude));
                    postParams.put("lng", Double.toString(longitude));
                    postParams.put("date", dateAndTime);

                } catch (JSONException e) {
                    System.out.println("Error in JSON");
                }

                WebApiDataTask webDataFetcher = new WebApiDataTask(CreateActivity.this);
                try {

                    //check whether the msg empty or not

                    String postURL = postParams.toString();//ServerDataRetriever.createPostURL(postParams);
                    webDataFetcher.execute("POST", "activity", postURL);
                } catch (Exception e) {
                    webDataFetcher.cancel(true);
                }
                finish();
            }
        });
    }

    @Override
    public void onPlaceSelected(Place place) {
        LatLng location = place.getLatLng();

        latitude = location.latitude;
        longitude = location.longitude;

        mMap.clear();

        mMap.addMarker(new MarkerOptions().position(location).title("Marker in location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        interestId = data.getStringExtra("interest");

        System.out.println("#### onActivityResult::InterestId = " + interestId);

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        System.out.println("##### Map is ready now");
        LatLng activityLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(activityLocation).title("Marker in location"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(activityLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(activityLocation, 12));
        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            mMap.setMyLocationEnabled(true);
        }
    }

    public void creationCompleted(boolean status) {
        System.out.println("####   Completed creation of task");
        finishActivity(0);
    }

    @Override
    public void onError(Status status) {
        System.out.println("Error hit");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), R.style.dialogTheme, this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            date = month+":"+day+":"+year;
        }

    }

    public void showDatePickerDialog(View v) {

        FragmentManager fragmentManager = getFragmentManager();

        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(fragmentManager, "datePicker");
    }

    private void getUserContext() {
        username = UserCredentials.getUserName();
        userid = UserCredentials.getUserId();
    }
}

package aadcapstone.coursera.org.nearbyplacesmovie;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aadcapstone.coursera.org.nearbyplacesmovie.webserviceclient.GooglePlacesClient;
import aadcapstone.coursera.org.nearbyplacesmovie.webserviceclient.NearbyPlacesResolvedListener;

import static aadcapstone.coursera.org.nearbyplacesmovie.R.id.search_close_btn;
import static aadcapstone.coursera.org.nearbyplacesmovie.R.id.show_in_youtube_button;
import static android.widget.Toast.LENGTH_LONG;


/**
 * The location selection activity (screen 2).
 * Main goal:
 *  - current location retrieval (with relevant permission checking/fixing)
 *  - get Google Places results for nearby locations (as per selected location type in
 *    previous activity)
 *  - ask the user to make a choice which location to show in YouTube
 */
public class LocationSelectionActivity  extends     AppCompatActivity
                                        implements  GoogleApiClient.ConnectionCallbacks,
                                                    GoogleApiClient.OnConnectionFailedListener,
                                                    LocationListener,
                                                    ActivityCompat.OnRequestPermissionsResultCallback,
                                                    NearbyPlacesResolvedListener {

    /**
     * The location type to use is stored in the Intent which started this Activity
     * with the following key name
     */
    public static final String SELECTED_LOCATION_TYPE = "CMD_SelectedLocationType";

    /**
     * int used to define our 'fine location'-permission request
     */
    private static final int MY_PERMISSION_REQUEST_READ_FINE_LOCATION = 100;

    /**
     * GUI items
     */
    private Spinner mNearByPlacesSpinner;       //the Spinner
    private Button mShowInYouTubeButton;        //the (one and only) button
    private TextView mStatusText;               //status text to update the user with progress

    /**
     * (official) Google API, used to get the location.
     * (this has become the preferred method)
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * The location request instance used with the Google API client
     */
    private LocationRequest mLocationRequest;

    /**
     * Our own Google Places webservice client
     */
    private GooglePlacesClient mGooglePlacesClient;

    /**
     * The location type as received from the other Activity
     */
    private String mLocationType;

    /**
     * The city in which we currently are: used to make the search in YouTube more accurate
     */
    private String mCurrentCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initiate some GUI related members
        mStatusText = (TextView) findViewById(R.id.status_text);
        mNearByPlacesSpinner = (Spinner)findViewById(R.id.nearby_locations_spinner);

        //disable the To-Do label for now, enable it after we have the location
        ((TextView) findViewById(R.id.todo_text)).setVisibility(View.INVISIBLE);

        //get/store the location type to search later on
        mLocationType = getIntent().getStringExtra(SELECTED_LOCATION_TYPE);

        try {
            mStatusText.setText("Checking location permission");
            if (locationRetrievalAllowed()) {
                //OK, we have already have permission, get the location
                initiateLocationRetrieval();
            }
            //note that INTERNET access permission is granted by default, so no need to check
            //(it's in the manifest, and this is sufficient)

            //init the button (invisible until the location has been determined)
            mShowInYouTubeButton = (Button) findViewById(show_in_youtube_button);
            mShowInYouTubeButton.setClickable(false);
            mShowInYouTubeButton.setVisibility(View.INVISIBLE);
            mShowInYouTubeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showInYouTube();
                }
            });

            mGooglePlacesClient = new GooglePlacesClient();

        } catch (SecurityException e) {
            mStatusText.setText("Please grant GPS access in order for the application to work");
        }
    }

    /**
     * Checks if location retrieval is already allowed.
     * If it isn't, request permission.
     * @return true if we already have the proper permission, so location retrieval can be done
     */
    private boolean locationRetrievalAllowed () {
        boolean locationRetrievalAllowed = false;

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //OK, cool, we have permission
            locationRetrievalAllowed = true;

        } else {
            //OK, we don't have permission..
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                mStatusText.setText("Please grant GPS access in order for the application to work");

                // Provide some 'useful hint' why we need this permission
                showText("Please provide location access, hit back and try again..");
            }

            // we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_REQUEST_READ_FINE_LOCATION);

            // MY_PERMISSION_REQUEST_READ_FINE_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }

        return locationRetrievalAllowed;
    }

    /**
     * Internal method to start location retrieval
     */
    private void initiateLocationRetrieval() {

        mStatusText.setText("Permission is OK");

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mStatusText.setText("Retrieving current location..");
    }

    /**
     * If we required permission approval, now might be the time we actually got the
     * required permission.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSION_REQUEST_READ_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay!
                // So, it's time to get the location
                initiateLocationRetrieval();

            } else {
                // permission denied, boo!
                showText("Please provide location access, hit back and try again..");
            }
        }
    }

    /**
     * Callback in which we recieve the current location
     * @param loc
     */
    @Override
    public void onLocationChanged(Location loc) {

        mStatusText.setText("Location received");

        //please don't keep on 'spamming' with new updates.
        //we might optimize based on accuracy, but this is for future updates ;-)
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        double longitude = loc.getLongitude();
        double latitude = loc.getLatitude();
        float accuracy = loc.getAccuracy();

        try {
            //store the current place/city for better YouTube search results
            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0)
                mCurrentCity = addresses.get(0).getLocality();
            //if nothing found, just don't use the city in the search result
        } catch (IOException e) {
            //same scenario: just don't use the city in the search result
        }

        try {
            //only check the webservice if it has been properly initalized.
            if (mGoogleApiClient != null) {
                mStatusText.setText("Contacting Google for nearby locations");

                //asking Google Places for nearby locations of the proper type
                mGooglePlacesClient.getNearbyPlaces(Double.toString(longitude),
                        Double.toString(latitude),
                        mLocationType,
                        this);
            }
        } catch (Exception e) {
            showText(e.getMessage());
        }
    }

    /**
     * The nearby places have been resolved, update the GUI accordingly
     */
    public void nearbyPlacesResolved() {

        //consulting the webservice is done.
        //let's see if we have some results first
        String nearbyPlaces[] = mGooglePlacesClient.getNearbyPlaces();
        if (nearbyPlaces.length == 0) {

            //hmm.. nothing found.
            //lets provide some feedback
            mStatusText.setText("No nearby locations found for this type");

        } else {
            //ok, places have been found: good!

            //make sure the GUI represents this
            mStatusText.setText("");
            ((TextView) findViewById(R.id.todo_text)).setVisibility(View.VISIBLE);
            mShowInYouTubeButton.setVisibility(View.VISIBLE);

            //add the nearby places to the Spinner
            List<String> spinnerArray = new ArrayList<String>();
            for (int i = 0; i < nearbyPlaces.length; i++) {
                spinnerArray.add(nearbyPlaces[i]);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, spinnerArray);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mNearByPlacesSpinner.setAdapter(adapter);
        }
    }

    /**
     * Helper method in order to get the proper search criteria in the URL to YouTube based on the
     * current location type, nearby place name and (optioannly) the city
     * @return YouTube search criteria
     */
    private String getYouTubeSearchCriteria () {
        StringBuilder searchCriteria = new StringBuilder();

        searchCriteria.append(mNearByPlacesSpinner.getSelectedItem().toString()).append(" ");
        if (!mCurrentCity.isEmpty())
            searchCriteria.append(mCurrentCity).append(" ");
        searchCriteria.append(mLocationType);

        try {
            //use an URL encoder to ensure a proper URL
            return URLEncoder.encode(searchCriteria.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return mLocationType;   //revert to something simple..
        }
    }

    /**
     * Open a YouTube search based on our search criteria
     */
    private void showInYouTube(){

        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://www.youtube.com/results?search_query=" +
                                                getYouTubeSearchCriteria()));
        startActivity(webIntent);
    }

    /**
     * Create a Toast with some text.
     * @param text Text to show
     */
    private void showText (String text) {
        Toast.makeText(getApplicationContext(), text, LENGTH_LONG).show();
    }

    /**
     * Google API requirement: connect to the API in onStart
     */
    @Override
    protected void onStart() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        super.onStart();
    }

    /**
     * Google API requirement: connect to the API after resume
     */
    @Override
    protected void onResume() {
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
        super.onResume();
    }

    /**
     * Google API requirement: disconnect from the API if we stop
     */
    @Override
    protected void onStop() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Location retrieval initiation.
     * After calling connect(), this method will be invoked asynchronously when
     * the connect request has successfully completed.
     * @param connectionHint
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        try {

            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            //parameters are set, now ask for updates on the location
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } catch (SecurityException e) {
            showText("Please give access to location");
        }
    }


    /**
     * Google API callback: unused
     * Called when the client is temporarily in a disconnected state
     * @param cause
     */
    @Override
    public void onConnectionSuspended(int cause) {
    }

    /**
     * Google API callback. Connection failed..
     * @param result
     */
    @Override
    public void onConnectionFailed (ConnectionResult result) {
        showText("Please give access to location");
    }
}

package partykalauz.hu.partykalauz;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.service.carrier.CarrierMessagingService;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

//List view: event.xml
public class PartyKalauz extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {

    ListView list;
    Context context = this;
    Event[] currentEvents;
    Gson gson = new Gson();
    private LocationManager locationManager;
    private String provider;
    FloatingActionButton fabMap;
    FloatingActionButton fabSettings;
    private GoogleApiClient client;
    boolean mapPermissionGiven = false;
    final int PERMISSION_COARSE = 1;
    int selectedDistance = 40;
    String selectedName = "Default";
    int maxEvents = 1000;
    ArrayList<String> allPlaces = new ArrayList<String>();
    Date selectedDate = new Date();
    TextView noEventsText;
    //================== Variables for location handling =============
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected Boolean mRequestingLocationUpdates;
    protected Location mCurrentLocation;
    protected String mLastUpdateTime;
    double lat = 47.504292;
    double lng = 19.058779;
    protected static final String TAG = "PartyKalauz";
    protected GoogleApiClient mGoogleApiClient;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected LocationRequest mLocationRequest;
    protected LocationSettingsRequest mLocationSettingsRequest;
    //================================================================

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private boolean searchedForADate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_kalauz);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //================================= Analytics ============================================
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        //========================================================================================


        //============================ Start Location Functions ==================================
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        //========================================================================================
        /**
         Clicking the Settings button (fab) sends the user to the Settings Activity, where he can
         set filters for the events.
         Clicking the Map button (fabMap) sends the user to the Google Maps Activity.
         Clicking on an event opens the EventView activity, that shows the Facebook pate of
         the event in a Webview.
         **/
        //========================================================================================
        fabSettings = (FloatingActionButton) findViewById(R.id.fab);
        fabSettings.hide();
        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent eventFilterIntent = new Intent(context, EventFilters.class);
                eventFilterIntent.putExtra("DISTANCE", selectedDistance);
                eventFilterIntent.putExtra("DATE", selectedDate.getTime());
                Collections.sort(allPlaces);
                String[] allPlacesString = new String[allPlaces.size()];
                allPlacesString = allPlaces.toArray(allPlacesString);
                eventFilterIntent.putExtra("PLACES", allPlacesString);
                startActivity(eventFilterIntent);
            }
        });
        //========================================================================================
        fabMap = (FloatingActionButton) findViewById(R.id.fabMap);
        fabMap.hide();
        fabMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String jsonEvents = gson.toJson(currentEvents);
                Intent mapViewIntent = new Intent(context, EventMap.class);
                mapViewIntent.putExtra("JSON", jsonEvents);
                mapViewIntent.putExtra("LATITUDE", lat);
                mapViewIntent.putExtra("LONGITUDE", lng);
                startActivity(mapViewIntent);
            }
        });
        //========================================================================================

        list = (ListView) findViewById(R.id.events);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event listItem = (Event) list.getItemAtPosition(position);
                Intent eventViewIntent = new Intent(context, EventView.class);
                eventViewIntent.putExtra("EXTRA_MESSAGE", listItem.eventURL);
                eventViewIntent.putExtra("PLACE", listItem.place);
                eventViewIntent.putExtra("LATITUDE", listItem.eventCoordinates.latitude);
                eventViewIntent.putExtra("LONGITUDE", listItem.eventCoordinates.longitude);
                startActivity(eventViewIntent);
            }
        });
        populateListView();
        noEventsText = (TextView) findViewById(R.id.noEventsText);
        noEventsText.setText(R.string.no_event_notify);
        noEventsText.setVisibility(noEventsText.INVISIBLE);
    }

    //========================================================================================
    /*
    Method onNewIntent gets called after returning from CalendarActivity. It uses the user chosen
    date and collects the events on that date.

    !!! Handle case when the user returns from EventMap or EventView !!!

     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        ProgressBar loadingCircle = (ProgressBar) findViewById(R.id.progressBar);
        loadingCircle.setVisibility(loadingCircle.VISIBLE);
        noEventsText.setVisibility(noEventsText.INVISIBLE);

        selectedDate.setTime(intent.getLongExtra("DATE", -1));
        selectedDistance = intent.getIntExtra("DISTANCE", -1);
        selectedName = intent.getStringExtra("NAME");
        getEventsForFilters(selectedDate, selectedDistance, selectedName);
        //================================= Analytics ============================================
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("platform", "Android");
        ParseAnalytics.trackEventInBackground("DetailsOpened", properties);
        //========================================================================================
    }

    /**
     * Collects the events for a date specified in parameter.
     * @param selectedDate - the selected date for the events
     * @param selectedDistance - the selected maximum distance for the events
     * @param selectedName - the selected place for the events
     */
    private void getEventsForFilters(Date selectedDate, int selectedDistance, String selectedName) {
        list.setAdapter(null);
        searchedForADate = true;
        Date nextDay = new Date();
        nextDay.setTime(selectedDate.getTime() + 86400000);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Events");
        query.whereGreaterThanOrEqualTo("event_date", selectedDate);
        query.whereLessThan("event_date", nextDay); // Filter for events that are on the user chosen day
        if (selectedName != null)
            query.whereContains("event_location", selectedName);  // Filter for events that include the user chosen name

        /*
        Next block is used for getting the last known location of the user. Copied from EventMap...
        Should be made cleaner and avoid code duplication...
        */
        //============================ Enable Location ================================


        //========================================================================================
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Define the criteria how to select the location provider -> use
        // default
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(criteria, true);
        //    locationManager.requestLocationUpdates(provider, 5000, 1000, (LocationListener) this);
        int permissionCheck = ActivityCompat.checkSelfPermission(PartyKalauz.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(PartyKalauz.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                Toast.makeText(PartyKalauz.this, "Helyzetmeghatározás", Toast.LENGTH_SHORT).show(); //Explanation to the user
                ActivityCompat.requestPermissions(PartyKalauz.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_COARSE);
            } else {
                //This is where we request the user the permission
                ActivityCompat.requestPermissions(PartyKalauz.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_COARSE);
            }
        } else {
            ActivityCompat.requestPermissions(PartyKalauz.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_COARSE);
            //     mapPermissionGiven = true;
        }
        ParseGeoPoint currentLocation = new ParseGeoPoint();

        Location currentLatLngLocation = locationManager.getLastKnownLocation(provider); // This sometimes crashes, when called with provider = gps
        try {
            currentLocation.setLatitude(currentLatLngLocation.getLatitude());
            currentLocation.setLongitude(currentLatLngLocation.getLongitude());
        } catch (Exception e) {

            currentLatLngLocation = locationManager.getLastKnownLocation(provider); // Network provider always works
            currentLocation.setLatitude(mCurrentLocation.getLatitude());
            currentLocation.setLongitude(mCurrentLocation.getLongitude());
        }

        //=======================================================================================
        query.whereWithinKilometers("event_coordinates", currentLocation, selectedDistance); //Filter for events that the near to current user location
        query.setLimit(maxEvents);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> eventList, ParseException e) {
                Event[] myItems = new Event[eventList.size()];
                if (e == null) {
                    Log.d("score", "Retrieved " + eventList.size() + " events");
                    int i = 0;
                    for (ParseObject currentObject : eventList) {
                        Event event = new Event(currentObject);
                        myItems[i] = event;
                        i++;
                    }
                    Arrays.sort(myItems, Event.compareAttendees);
                    //Arrays.sort(myItems, Event.EventComparator.decending(Event.EventComparator.getComparator(Event.EventComparator.DATE_SORT, Event.EventComparator.ATTENDEES_SORT)));
                    currentEvents = myItems;
                    EventAdapter adapter = new EventAdapter(context, myItems);
                    list.setAdapter(adapter);
                    if (adapter.isEmpty() == true)
                        noEventsText.setVisibility(noEventsText.VISIBLE);

                    ProgressBar loadingCircle = (ProgressBar) findViewById(R.id.progressBar);
                    loadingCircle.setVisibility(loadingCircle.INVISIBLE);

                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Collects all events.
     */
    private void populateListView() {
        searchedForADate = false;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Events");
        query.whereGreaterThanOrEqualTo("event_date", new Date());
        query.setLimit(maxEvents);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> eventList, ParseException e) {
                Event[] myItems = new Event[eventList.size()];
                if (e == null) {
                    Log.d("score", "Retrieved " + eventList.size() + " events");
                    int i = 0;
                    for (ParseObject currentObject : eventList) {
                        Event event = new Event(currentObject);
                        if (currentObject.get("event_location") != null) {
                            if (allPlaces.contains((String) currentObject.get("event_location")) == false)
                                allPlaces.add((String) currentObject.get("event_location"));
                        }
                        myItems[i] = event;
                        i++;
                    }
                    fabMap.show();
                    fabSettings.show();
                    // Arrays.sort(myItems,Event.compareAttendees);
                    Arrays.sort(myItems, Event.EventComparator.decending(Event.EventComparator.getComparator(Event.EventComparator.DATE_SORT, Event.EventComparator.ATTENDEES_SORT)));
                    currentEvents = myItems;
                    EventAdapter adapter = new EventAdapter(context, myItems);
                    list.setAdapter(adapter);

                    ProgressBar loadingCircle = (ProgressBar) findViewById(R.id.progressBar);
                    loadingCircle.setVisibility(loadingCircle.INVISIBLE);

                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * Overriding onBackPressed is necessary to get back all events and deleting all filters.
     */
    @Override
    public void onBackPressed() {

        if (searchedForADate) {
            ProgressBar loadingCircle = (ProgressBar) findViewById(R.id.progressBar);
            loadingCircle.setVisibility(loadingCircle.VISIBLE);
            list.setAdapter(null);
            populateListView();
        } else
            super.onBackPressed();


    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_COARSE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 //   Toast.makeText(PartyKalauz.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                 //   Toast.makeText(PartyKalauz.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();

    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
           // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(PartyKalauz.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    //============================ Enable Location ================================
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = true;
            }
        });

    }

    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = false;
            }
        });
    }
}

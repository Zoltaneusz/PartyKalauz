package partykalauz.hu.partykalauz;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//List view: event.xml
public class PartyKalauz extends AppCompatActivity implements LocationListener{

    ListView list;
    Context context = this;
    Event[] currentEvents;
    Gson gson = new Gson();
    private LocationManager locationManager;
    private String provider;
    FloatingActionButton fabMap;
    double lat = 47.504292;
    double lng = 19.058779;
    private GoogleApiClient client;
    boolean mapPermissionGiven = false;
    final int PERMISSION_COARSE = 0;
    int selectedDistance = 40;
    String selectedName;

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

        /**
         Clicking the Settings button (fab) sends the user to the Settings Activity, where he can
         set filters for the events.
         Clicking the Map button (fabMap) sends the user to the Google Maps Activity.
         Clicking on an event opens the EventView activity, that shows the Facebook pate of
         the event in a Webview.
         **/
        //========================================================================================
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent eventFilterIntent = new Intent(context, EventFilters.class);
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
    }
        //========================================================================================
    /*
    Method onNewIntent gets called after returning from CalendarActivity. It uses the user chosen
    date and collects the events on that date.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Date selectedDate = new Date();
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
    private void getEventsForFilters(Date selectedDate, int selectedDistance, String selectedName){
        list.setAdapter(null);
        searchedForADate = true;
        Date nextDay = new Date();
        nextDay.setTime(selectedDate.getTime() + 86400000);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Events");
        query.whereGreaterThanOrEqualTo("event_date", selectedDate);
        query.whereLessThan("event_date", nextDay);
        query.whereContains("event_location", selectedName);

        /*
        Next block is used for getting the last known location of the user. Copied from EventMap...
        Should be made cleaner and avoid code duplication...
        */
        //========================================================================================
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 400, 1000, (LocationListener) this);
        // Define the criteria how to select the location provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(PartyKalauz.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(PartyKalauz.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Toast.makeText(EventMap.this, "", Toast.LENGTH_SHORT).show(); //Explanation to the user
            }
            else{
                //This is where we request the user the permission
                ActivityCompat.requestPermissions(PartyKalauz.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_COARSE);
            }

            return;
        }
        else {
            mapPermissionGiven = true;
        }
        Location currentLatLngLocation = locationManager.getLastKnownLocation(provider);
        //=======================================================================================
        ParseGeoPoint currentLocation = new ParseGeoPoint();
        currentLocation.setLatitude(currentLatLngLocation.getLatitude());
        currentLocation.setLongitude(currentLatLngLocation.getLongitude());
        query.whereWithinKilometers("event_coordinates", currentLocation, selectedDistance);
        query.setLimit(1000);
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
                    Arrays.sort(myItems,Event.compareAttendees);
                    //Arrays.sort(myItems, Event.EventComparator.decending(Event.EventComparator.getComparator(Event.EventComparator.DATE_SORT, Event.EventComparator.ATTENDEES_SORT)));
                    currentEvents = myItems;
                    EventAdapter adapter = new EventAdapter(context, myItems);
                    list.setAdapter(adapter);

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
        query.setLimit(1000);
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
                    fabMap.show();
                    // Arrays.sort(myItems,Event.compareAttendees);
                    Arrays.sort(myItems, Event.EventComparator.decending(Event.EventComparator.getComparator(Event.EventComparator.DATE_SORT, Event.EventComparator.ATTENDEES_SORT)));
                    currentEvents = myItems;
                    EventAdapter adapter = new EventAdapter(context, myItems);
                    list.setAdapter(adapter);

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

        if(searchedForADate)
        {
            list.setAdapter(null);
            populateListView();
        }
        else
            super.onBackPressed();


    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

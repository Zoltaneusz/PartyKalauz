package partykalauz.hu.partykalauz;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;

import java.util.HashMap;
import java.util.Map;

public class EventMap extends FragmentActivity implements OnMapReadyCallback, LocationListener {


    private GoogleMap mMap;
    Event[] currentEvents;
    private LocationManager locationManager;
    private String provider;
    final int PERMISSION_COARSE = 0;
    private GoogleApiClient client;
    boolean mapPermissionGiven = false;
    double lat;
    double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //================================= Analytics ============================================
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("platform", "Android");
        ParseAnalytics.trackEventInBackground("MapOpened", properties);
        //========================================================================================
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 400, 1000, (LocationListener) this);
        // Define the criteria how to select the location provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        if (ActivityCompat.checkSelfPermission(EventMap.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(EventMap.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

               // Toast.makeText(EventMap.this, "", Toast.LENGTH_SHORT).show(); //Explanation to the user
            }
            else{
                    //This is where we request the user the permission
                ActivityCompat.requestPermissions(EventMap.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_COARSE);
            }

            return;
        }
        else {
            mapPermissionGiven = true;
        }

        Location location = locationManager.getLastKnownLocation(provider);
        if(mapPermissionGiven == true) {
            lat = 47.504292;
            lng = 19.058779;
        }
        else {
            lat = 47.504292;
            lng = 19.058779;
        }

        // Original maps activity
        mMap = googleMap;
        Intent intent = getIntent();
        String json = intent.getStringExtra("JSON");
        String place = intent.getStringExtra("PLACE");

        if(json == null) {  // If coming from EventView (WebView)
            double latFromEventView = intent.getDoubleExtra("LATITUDE", 47.504292);
            double lngFromEventView = intent.getDoubleExtra("LONGITUDE", 19.058779);
            mMap.addMarker(new MarkerOptions().position(new LatLng(latFromEventView, lngFromEventView)).title(place));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latFromEventView, lngFromEventView), 14.0f));
        }
        else {
            Gson gson = new Gson();
            currentEvents = gson.fromJson(json, Event[].class);
            showMarkers();
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 14.0f));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentEvents[0].eventCoordinates, 12.0f));
        }

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentEvents[0].eventCoordinates, 12.0f));
        mMap.setMyLocationEnabled(true);
    }

    public void showMarkers()
    {
        for (Event currentEvent : currentEvents) {
            if (currentEvent.eventCoordinates != null) {
                mMap.addMarker(new MarkerOptions().position(currentEvent.eventCoordinates).title(currentEvent.place));
            }
        }
    }

    /**
     * This method gets called after the user decided about the permission
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_COARSE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mapPermissionGiven = true; // permission granted

                } else {
                    mapPermissionGiven = false; // permission denied

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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


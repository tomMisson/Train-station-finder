package com.example.thomas.hackathonproject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, Style.OnStyleLoaded{
    private MapView mapView;
    MapboxMap map ;
    double lat = 0.0;
    double lng = 0.0;
    TextView txt;
    String markerIcon = "suitcase-15";
    List<SymbolOptions> markers = new ArrayList<>();
    StationWebServiceClient client = new StationWebServiceClient();
    SymbolManager sm;

    /**
     * Runs when app is opened
     * @param savedInstanceState previous state the application was in
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.api_key));
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        //Defines a list of permissions to check for at runtime
        String[] requestedPermissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        };

        boolean ok = true;
        for(int i=0; i<requestedPermissions.length; i++)
        {
            //Checks to see if permission has been granted for the app to use
            int result = ActivityCompat.checkSelfPermission(this,requestedPermissions[i]);
            if(result != PackageManager.PERMISSION_GRANTED){
                ok = false;
            }
        }

        //Requests app permission if not perviously granted
        if(!ok){
            ActivityCompat.requestPermissions(this, requestedPermissions,1);
            System.exit(0);
        }
        else
        {
            //If location is granted then pull current location
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) { }

                @Override
                public void onProviderEnabled(String provider) { }

                @Override
                public void onProviderDisabled(String provider) { }
            });
        }
    }

    /**
     * Sets presets for Map options
     * @param mapboxMap current instance of the Mapbox map
     */
    public void onMapReady(@NonNull MapboxMap mapboxMap){
        map = mapboxMap;
        mapboxMap.setStyle(Style.OUTDOORS, this);

        mapboxMap.setCameraPosition(
                new CameraPosition.Builder().target(new LatLng(lat,lng)).zoom(12).build()
        );
    }

    /**
     * Creates the initial style of the map and initial person marker
     * @param style Style context
     */
    @Override
    public void onStyleLoaded(@NonNull Style style)
    {
        //Manager to add symbols to the map
        sm = new SymbolManager(mapView,map,style);

        sm.setIconAllowOverlap(true);
        sm.setIconIgnorePlacement(true);

        sm.addClickListener(new OnSymbolClickListener() {
            @Override
            public void onAnnotationClick(Symbol symbol) {
                Context context = getApplicationContext();
                CharSequence text = symbol.getTextAnchor();
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });

        // Add symbol at specified lat/lon

        Symbol symbol = sm.create(new SymbolOptions()
                        .withLatLng(new LatLng(lat,lng))
                        .withIconImage(markerIcon)
                        .withIconSize(2.0f)
                        .withTextAnchor("You")
        );
    }

    /**
     * Overide default onStart method to start the map aswell
     */
    @Override
    public void onStart(){
        super.onStart();
        mapView.onStart();
    }

    /**
     * Overide default onPause method to pause the map aswell
     */
    public void onPause()
    {
        super.onPause();
        mapView.onPause();
    }

    /**
     * Overide default onResume method to resume the map aswell
     */
    public void onResume()
    {
        super.onResume();
        mapView.onResume();
    }

    /**
     * Click handler for button that gets location and makes a call to the Web service
     * @param v view to get context from
     */
    public void onClick(View v)
    {
        txt = findViewById(R.id.textView);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new task().execute(client.urlBuilder(lat,lng));
        }
    }

    /**
     * display results takes a list of stations and appends them to the text view
     * @param list List of stations to add to text view
     */
    public void displayResults(List<Station> list)
    {
        txt.setText("");
        sm.deleteAll();
        markers.add(new SymbolOptions()
                .withLatLng(new LatLng(lat,lng))
                .withIconImage(markerIcon)
                .withIconSize(2.0f)
                .withTextAnchor("You")
        );
        for(Station s: list)
        {
            txt.append(s.toString(lat,lng)+"\r\n");

            markers.add( new SymbolOptions()
                    .withLatLng(new LatLng(s.lat, s.lng))
                    .withIconImage(markerIcon)
                    .withIconSize(2.0f)
                    .withTextAnchor(s.name)
            );
            for(SymbolOptions marker: markers)
            {
                Symbol symbol = sm.create(marker);
            }
        }
        map.setCameraPosition(
                new CameraPosition.Builder().target(new LatLng(lat,lng)).zoom(11.5).build()
        );
    }

    /**
     * An Async task to manage the fetching of data in the background
     */
    class task extends AsyncTask<URL, Void, List<Station>> {

        /**
         * Activity to run in the background (fetching data from URL)
         * @param urls URL to fetch data from
         * @return Returns a list of stations from the web handler
         */
        @Override
        protected List<Station> doInBackground(URL... urls) {
            return new StationWebServiceClient().getStationsByURL(urls[0]);
        }

        /**
         * Takes the list of stations and passes it to the display results handler
         * @param stations list of stations returned from the doInBackground method
         */
        @Override
        protected void onPostExecute(List<Station> stations) {
            displayResults(stations);
        }
    }
}

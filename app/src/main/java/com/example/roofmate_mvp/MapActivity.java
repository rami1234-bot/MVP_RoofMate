package com.example.roofmate_mvp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.firebase.database.collection.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapActivity extends AppCompatActivity {

    private MapView map;
    private GeoPoint selectedPoint;
    private SearchView searchView;
    private Button btnSubmitLocation;
    private ImageView centerDot;
    private static final String GEOCODING_API_URL = "https://nominatim.openstreetmap.org/search?q=%s&format=json&addressdetails=1";

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        setContentView(R.layout.activity_map);

        map = findViewById(R.id.map);
        searchView = findViewById(R.id.searchView);
        btnSubmitLocation = findViewById(R.id.btnSubmitLocation);
        centerDot = findViewById(R.id.centerDot);

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        GeoPoint startPoint = new GeoPoint(48.8583, 2.2944); // Default location (Paris)
        map.getController().setZoom(15);
        map.getController().setCenter(startPoint);

        // SearchView query listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform location search here (geocoding)
                new GeocodingTask().execute(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Button to submit the current map center as the selected location
        btnSubmitLocation.setOnClickListener(v -> {
            selectedPoint = (GeoPoint) map.getMapCenter();
            if (selectedPoint != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("latitude", selectedPoint.getLatitude());
                resultIntent.putExtra("longitude", selectedPoint.getLongitude());
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Please select a location first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class GeocodingTask extends AsyncTask<String, Void, GeoPoint> {
        @Override
        protected GeoPoint doInBackground(String... params) {
            String address = params[0];
            try {
                String urlString = String.format(GEOCODING_API_URL, address.replace(" ", "%20"));
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                String response = stringBuilder.toString();
                JSONArray jsonResponse = new JSONArray(response);
                if (jsonResponse.length() > 0) {
                    JSONObject location = jsonResponse.getJSONObject(0);
                    double lat = location.getDouble("lat");
                    double lon = location.getDouble("lon");
                    return new GeoPoint(lat, lon);
                }
            } catch (Exception e) {
                Log.e("GeocodingTask", "Error in geocoding", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(GeoPoint geoPoint) {
            if (geoPoint != null) {
                map.getController().setCenter(geoPoint);
                map.invalidate();
                Toast.makeText(MapActivity.this, "Location found: (" + geoPoint.getLatitude() + ", " + geoPoint.getLongitude() + ")", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MapActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

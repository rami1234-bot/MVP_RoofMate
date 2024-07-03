package com.example.roofmate_mvp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class HomeInfo extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "HomeInfo";
    private Toolbar toolbar;
    private TextView tvHouseName, tvHouseDescription;
    private Button btnToggleWishlist, btnContactOwner;
    private MapView mapView;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String homeId, ownerid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_info);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        toolbar = findViewById(R.id.tlbr);
        tvHouseName = findViewById(R.id.tv_house_name);
        tvHouseDescription = findViewById(R.id.tv_house_description);
        btnToggleWishlist = findViewById(R.id.btn_add_to_wishlist);
        btnContactOwner = findViewById(R.id.btn_contact_owner);
        mapView = findViewById(R.id.map);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Home Information");
        }

        // Fetch homeId from Intent
        Intent intent = getIntent();
        if (intent != null) {
            homeId = intent.getStringExtra("home_id");
            ownerid = intent.getStringExtra("ownerid");
            String homeName = intent.getStringExtra("home_name");
            String homeDescription = intent.getStringExtra("home_description");

            Log.d(TAG, "Received homeId: " + homeId);
            Log.d(TAG, "Received ownerId: " + ownerid);

            // Display home information
            tvHouseName.setText(homeName);
            tvHouseDescription.setText(homeDescription);

            // Check if home is in wishlist and update button text
            checkWishlistStatus();

            // Set click listeners
            btnToggleWishlist.setOnClickListener(v -> toggleWishlist());
            btnContactOwner.setOnClickListener(this);

            // Initialize and display the map
            initializeMap();
        } else {
            Log.e(TAG, "Intent is null");
            Toast.makeText(this, "Intent is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeMap() {
        Configuration.getInstance().load(getApplicationContext(), getPreferences(MODE_PRIVATE));
        mapView.setMultiTouchControls(true);

        // Set default map center and zoom
        GeoPoint startPoint = new GeoPoint(48.8583, 2.2944); // Default to Paris
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(startPoint);

        // Enable current location
        MyLocationNewOverlay myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);

        // Fetch and display home location on the map
        fetchHomeLocation();
    }

    private void fetchHomeLocation() {
        DatabaseReference homeRef = mDatabase.child("homes").child(homeId);
        homeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                    double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                    if (latitude != 0 && longitude != 0) {
                        GeoPoint homeLocation = new GeoPoint(latitude, longitude);
                        mapView.getController().setCenter(homeLocation);
                        addMarker(homeLocation);
                    } else {
                        Toast.makeText(HomeInfo.this, "Home location not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeInfo.this, "Home data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to fetch home location", databaseError.toException());
                Toast.makeText(HomeInfo.this, "Failed to fetch home location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMarker(GeoPoint point) {
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }

    private void toggleWishlist() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference userRef = mDatabase.child("users").child(userId);
            Toast.makeText(HomeInfo.this, "Added to wishlist", Toast.LENGTH_SHORT).show();

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        if (user.getWishlist() != null && user.getWishlist().contains(homeId)) {
                            // Remove from wishlist
                            user.removeFromWishlist(homeId);
                            userRef.setValue(user); // Update user object in database
                            btnToggleWishlist.setText("Add to Wishlist");
                            Toast.makeText(HomeInfo.this, "Removed from wishlist", Toast.LENGTH_SHORT).show();
                        } else {
                            // Add to wishlist
                            user.addToWishlist(homeId);
                            userRef.setValue(user); // Update user object in database
                            btnToggleWishlist.setText("Remove from Wishlist");
                            Toast.makeText(HomeInfo.this, "Added to wishlist", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Failed to read user data", databaseError.toException());
                    Toast.makeText(HomeInfo.this, "Failed to read user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void checkWishlistStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = mDatabase.child("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null && user.getWishlist() != null && user.getWishlist().contains(homeId)) {
                        btnToggleWishlist.setText("Remove from Wishlist");
                    } else {
                        btnToggleWishlist.setText("Add to Wishlist");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Failed to read user data", databaseError.toException());
                    Toast.makeText(HomeInfo.this, "Failed to read user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_contact_owner) {
            Intent intent = new Intent(this, Profile.class);
            intent.putExtra("userid", ownerid); // Assuming ownerid is declared somewhere in your activity
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDetach();
    }
}

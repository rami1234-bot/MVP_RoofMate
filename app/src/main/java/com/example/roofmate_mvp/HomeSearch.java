package com.example.roofmate_mvp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeSearch extends BaseActivity {

    private static final String FIREBASE_HOMES_REFERENCE = "homes";

    private EditText etMinPrice, etMaxPrice, etMinRooms, etMaxRooms;
    private Button btnApplyFilter;
    private SearchView searchView;
    private ListView listView;

    private FirebaseDatabase database;
    private List<Home> homeList;
    private HomeAdapter homeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_search);

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance();

        // Initialize UI components
        initializeUIComponents();

        // Load all homes from Firebase
        loadHomes();

        // Set button click listener
        setApplyFilterButtonListener();

        // Set SearchView query listener
        setSearchViewQueryListener();

        // Set ListView item click listener
        setListViewItemClickListener();
    }

    private void initializeUIComponents() {
        etMinPrice = findViewById(R.id.et_min_price);
        etMaxPrice = findViewById(R.id.et_max_price);
        etMinRooms = findViewById(R.id.et_min_rooms);
        etMaxRooms = findViewById(R.id.et_max_rooms);
        btnApplyFilter = findViewById(R.id.btn_apply_filter);
        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listView);

        homeList = new ArrayList<>();
        homeAdapter = new HomeAdapter(this, homeList);
        listView.setAdapter(homeAdapter);

        Toolbar toolbar = findViewById(R.id.tlbr);
        setSupportActionBar(toolbar);
    }

    private void loadHomes() {
        database.getReference(FIREBASE_HOMES_REFERENCE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                homeList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Home home = dataSnapshot.getValue(Home.class);
                    if (home != null) {
                        homeList.add(home);
                    }
                }
                homeAdapter.notifyDataSetChanged();
                if (homeList.isEmpty()) {
                    Toast.makeText(HomeSearch.this, "No homes found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeSearch.this, "Failed to load homes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setApplyFilterButtonListener() {
        btnApplyFilter.setOnClickListener(v -> {
            String minPriceStr = etMinPrice.getText().toString().trim();
            String maxPriceStr = etMaxPrice.getText().toString().trim();
            String minRoomsStr = etMinRooms.getText().toString().trim();
            String maxRoomsStr = etMaxRooms.getText().toString().trim();

            if (minPriceStr.isEmpty() || maxPriceStr.isEmpty() || minRoomsStr.isEmpty() || maxRoomsStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int minPrice = Integer.parseInt(minPriceStr);
                int maxPrice = Integer.parseInt(maxPriceStr);
                int minRooms = Integer.parseInt(minRoomsStr);
                int maxRooms = Integer.parseInt(maxRoomsStr);
                filterHomes(minPrice, maxPrice, minRooms, maxRooms);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterHomes(int minPrice, int maxPrice, int minRooms, int maxRooms) {
        List<Home> filteredList = new ArrayList<>();
        for (Home home : homeList) {
            if (home.getRent() >= minPrice && home.getRent() <= maxPrice &&
                    home.getRooms() >= minRooms && home.getRooms() <= maxRooms) {
                filteredList.add(home);
            }
        }
        homeAdapter.updateList(filteredList);
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No homes found matching the criteria", Toast.LENGTH_SHORT).show();
        }
    }

    private void setSearchViewQueryListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterHomesByName(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterHomesByName(newText);
                return true;
            }
        });
    }

    private void filterHomesByName(String name) {
        List<Home> filteredList = new ArrayList<>();
        for (Home home : homeList) {
            if (home.getName().toLowerCase().contains(name.toLowerCase())) {
                filteredList.add(home);
            }
        }
        homeAdapter.updateList(filteredList);
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No homes found matching the search criteria", Toast.LENGTH_SHORT).show();
        }
    }

    private void setListViewItemClickListener() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Home selectedHome = homeList.get(position);
            Intent intent = new Intent(HomeSearch.this, HomeInfo.class);
            intent.putExtra("home_name", selectedHome.getName());
            intent.putExtra("home_id",selectedHome.getId());
            intent.putExtra("home_description", selectedHome.getDisk());
            intent.putExtra("owneruid", selectedHome.getOwnerid());
            startActivity(intent);
        });
    }

}

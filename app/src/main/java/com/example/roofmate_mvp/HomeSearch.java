package com.example.roofmate_mvp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
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

public class HomeSearch extends AppCompatActivity {

    private EditText etMinPrice, etMaxPrice;
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
        etMinPrice = findViewById(R.id.et_min_price);
        etMaxPrice = findViewById(R.id.et_max_price);
        btnApplyFilter = findViewById(R.id.btn_apply_filter);
        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listView);

        homeList = new ArrayList<>();
        homeAdapter = new HomeAdapter(this, homeList);
        listView.setAdapter(homeAdapter);
        Toolbar toolbar = findViewById(R.id.tlbr);
        setSupportActionBar(toolbar);
        // Load all homes from Firebase
        loadHomes();

        // Set button click listener
        btnApplyFilter.setOnClickListener(v -> {
            String minPriceStr = etMinPrice.getText().toString().trim();
            String maxPriceStr = etMaxPrice.getText().toString().trim();

            if (minPriceStr.isEmpty() || maxPriceStr.isEmpty()) {
                Toast.makeText(this, "Please enter both min and max prices", Toast.LENGTH_SHORT).show();
                return;
            }

            int minPrice = Integer.parseInt(minPriceStr);
            int maxPrice = Integer.parseInt(maxPriceStr);

            filterHomes(minPrice, maxPrice);
        });

        // Set SearchView query listener
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

        // Set ListView item click listener
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Home selectedHome = homeList.get(position);
            Intent intent = new Intent(HomeSearch.this, HomeInfo.class);
            intent.putExtra("home_name", selectedHome.getName());
            intent.putExtra("home_description", selectedHome.getDisk());
            startActivity(intent);
        });
    }

    private void loadHomes() {
        database.getReference("homes").addValueEventListener(new ValueEventListener() {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeSearch.this, "Failed to load homes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterHomes(int minPrice, int maxPrice) {
        List<Home> filteredList = new ArrayList<>();
        for (Home home : homeList) {
            if (home.getRent() >= minPrice && home.getRent() <= maxPrice) {
                filteredList.add(home);
            }
        }
        homeAdapter.updateList(filteredList);
    }

    private void filterHomesByName(String name) {
        List<Home> filteredList = new ArrayList<>();
        for (Home home : homeList) {
            if (home.getName().toLowerCase().contains(name.toLowerCase())) {
                filteredList.add(home);
            }
        }
        homeAdapter.updateList(filteredList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.tool1) {
            // Handle action for Tool 1
            return true;
        } else if (id == R.id.tool2) {
            Intent intent = new Intent(HomeSearch.this, Profile.class);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            intent.putExtra("uid", currentUser.getUid());
            startActivity(intent);
            return true;
        } else if (id == R.id.tool3) {
            Intent intent = new Intent(HomeSearch.this, AddHome.class);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            intent.putExtra("uid", currentUser.getUid());
            startActivity(intent);
            return true;
        } else if (id == R.id.tool4) {
            Intent intent = new Intent(HomeSearch.this, HomeSearch.class);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            intent.putExtra("uid", currentUser.getUid());
            startActivity(intent);
            return true;
        } else if (id == R.id.tool5) {
            // Handle action for Tool 5
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}

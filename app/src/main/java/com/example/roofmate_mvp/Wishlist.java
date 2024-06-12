package com.example.roofmate_mvp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;

public class Wishlist extends AppCompatActivity {

    private ListView listView;
    private List<String> wishlistIds; // List to store home IDs in the wishlist
    private List<Home> wishlistHomes; // List to store homes in the wishlist
    private HomeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.tlbr);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.listView);
        wishlistIds = new ArrayList<>();
        wishlistHomes = new ArrayList<>();
        adapter = new HomeAdapter(this, wishlistHomes);
        listView.setAdapter(adapter);

        // Load wishlist from the database
        loadWishlist();
    }

    private void loadWishlist() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference wishlistRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("wishlist");

            wishlistRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    wishlistIds.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String homeId = snapshot.getValue(String.class);
                        if (homeId != null) {
                            wishlistIds.add(homeId);
                        }
                    }
                    // Once wishlist IDs are loaded, load the corresponding homes
                    loadWishlistHomes();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Wishlist.this, "Failed to load wishlist", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadWishlistHomes() {
        DatabaseReference homesRef = FirebaseDatabase.getInstance().getReference("homes");
        homesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                wishlistHomes.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Home home = snapshot.getValue(Home.class);
                    if (home != null && wishlistIds.contains(home.getId())) {
                        wishlistHomes.add(home);
                    }
                }
                adapter.notifyDataSetChanged(); // Notify adapter of data change
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Wishlist.this, "Failed to load homes from wishlist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class HomeAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private List<Home> homeList;

        public HomeAdapter(Context context, List<Home> homeList) {
            inflater = LayoutInflater.from(context);
            this.homeList = homeList;
        }

        @Override
        public int getCount() {
            return homeList.size();
        }

        @Override
        public Object getItem(int position) {
            return homeList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_home, parent, false);
                holder = new ViewHolder();
                holder.nameTextView = convertView.findViewById(R.id.name);
                holder.rentTextView = convertView.findViewById(R.id.rent);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Home home = homeList.get(position);

            holder.nameTextView.setText(home.getName());
            holder.rentTextView.setText(String.format("$%d", home.getRent()));

            return convertView;
        }

        private static class ViewHolder {
            TextView nameTextView;
            TextView rentTextView;
        }
    }
}

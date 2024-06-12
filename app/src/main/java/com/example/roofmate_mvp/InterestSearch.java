package com.example.roofmate_mvp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roofmate_mvp.Profile;
import com.example.roofmate_mvp.R;
import com.example.roofmate_mvp.User;
import com.example.roofmate_mvp.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InterestSearch extends BaseActivity implements UserAdapter.OnUserClickListener{
    private SearchView searchViewByInterests;
    private ListView listViewUsersByInterests;
    private UserAdapter userAdapter;

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest_search);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        // Initialize views
        searchViewByInterests = findViewById(R.id.searchViewByInterests);
        listViewUsersByInterests = findViewById(R.id.listViewUsersByInterests);

        userAdapter = new UserAdapter(this, new ArrayList<>());
        userAdapter.setOnUserClickListener(this);

        listViewUsersByInterests.setAdapter(userAdapter);

        // Set click listener for the search bar
        searchViewByInterests.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showInterestSelectionDialog();
                }
            }
        });
    }

    //  when search bar is pressed
    private void showInterestSelectionDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_intrests);
        dialog.setTitle("Select Interests to search");

        Button selectButton = dialog.findViewById(R.id.saveInterestsButton);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] interests = getSelectedInterests(dialog);
                searchUsersByInterests(interests);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // getting the list of selected interests for the search
    private String[] getSelectedInterests(Dialog dialog) {
        List<String> selectedInterests = new ArrayList<>();

        if (((CheckBox) dialog.findViewById(R.id.interest_music)).isChecked()) {
            selectedInterests.add("Music");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_sports)).isChecked()) {
            selectedInterests.add("Sports");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_travel)).isChecked()) {
            selectedInterests.add("Travel");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_reading)).isChecked()) {
            selectedInterests.add("Reading");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_cooking)).isChecked()) {
            selectedInterests.add("Cooking");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_gaming)).isChecked()) {
            selectedInterests.add("Gaming");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_dancing)).isChecked()) {
            selectedInterests.add("Dancing");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_photography)).isChecked()) {
            selectedInterests.add("Photography");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_art)).isChecked()) {
            selectedInterests.add("Art");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_fishing)).isChecked()) {
            selectedInterests.add("Fishing");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_gardening)).isChecked()) {
            selectedInterests.add("Gardening");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_hiking)).isChecked()) {
            selectedInterests.add("Hiking");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_knitting)).isChecked()) {
            selectedInterests.add("Knitting");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_cycling)).isChecked()) {
            selectedInterests.add("Cycling");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_yoga)).isChecked()) {
            selectedInterests.add("Yoga");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_fitness)).isChecked()) {
            selectedInterests.add("Fitness");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_swimming)).isChecked()) {
            selectedInterests.add("Swimming");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_baking)).isChecked()) {
            selectedInterests.add("Baking");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_writing)).isChecked()) {
            selectedInterests.add("Writing");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_meditation)).isChecked()) {
            selectedInterests.add("Meditation");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_boardgames)).isChecked()) {
            selectedInterests.add("Board Games");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_volunteering)).isChecked()) {
            selectedInterests.add("Volunteering");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_languagelearning)).isChecked()) {
            selectedInterests.add("Language Learning");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_astrology)).isChecked()) {
            selectedInterests.add("Astrology");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_technology)).isChecked()) {
            selectedInterests.add("Technology");
        }
        if (((CheckBox) dialog.findViewById(R.id.interest_cars)).isChecked()) {
            selectedInterests.add("Cars");
        }

        return selectedInterests.toArray(new String[0]);
    }

    // comparing the gotten list to the users in the database
    private void searchUsersByInterests(final String[] interests) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> matchingUsers = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && hasAllInterests(user, interests)) {
                        matchingUsers.add(user);
                    }
                }
                userAdapter.setUsers(matchingUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(InterestSearch.this, "Failed to search users: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean hasAllInterests(User user, String[] interests) {
        List<String> userInterests = user.getInterests();
        for (String interest : interests) {
            if (!userInterests.contains(interest)) {
                return false;
            }
        }
        return true;
    }
    // Handle item click here
    @Override
    public void onUserClick(User user) {
        Intent intent = new Intent(InterestSearch.this, Profile.class);
        intent.putExtra("userid", user.getUserid());
        startActivity(intent);
    }

}
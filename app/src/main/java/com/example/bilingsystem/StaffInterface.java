package com.example.bilingsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.bilingsystem.Model.Users;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StaffInterface extends AppCompatActivity {

    //alternative way of building Firebase Recycler, View holder is inside Adapter class
    private DatabaseReference databaseReference;
    private String fullname;
    private List<Users> user_list;
    private RecyclerView recyclerView;
    Adapter_Users adapter; // Object of the Adapter class

   // private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_interface);

        //    mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //mUsers.keepSynced(true);

        user_list = new ArrayList<>();

        recyclerView = findViewById(R.id.staff_recycler);
        // To display the Recycler view linearly
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this));


        //query firebase then pass it to adapter
        FirebaseRecyclerOptions<Users> options
                = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(databaseReference.child("users"), Users.class)
                .build();
        adapter = new Adapter_Users(options);
        // Connecting Adapter class with the Recycler view*/
        recyclerView.setAdapter(adapter);


        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Users users = postSnapshot.getValue(Users.class);
                    user_list.add(users);

                }

                Log.d("staff-int-debug","size "+ user_list.size());
                Log.d("staff-int-debug","first in list "+ user_list.get(1).getLname());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Function to tell the app to start getting
    // data from database on starting of the activity
    @Override protected void onStart()
    {
        super.onStart();
        adapter.startListening();
    }

    // Function to tell the app to stop getting
    // data from database on stoping of the activity
    @Override protected void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }

}
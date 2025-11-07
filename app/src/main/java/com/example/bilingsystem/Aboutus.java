package com.example.bilingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.bilingsystem.Model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Aboutus extends AppCompatActivity {
    //initialize variable
    DrawerLayout drawerLayout;
    private FirebaseAuth wAuth;
    FirebaseUser firebaseUser;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        //Hooks
        drawerLayout = findViewById(R.id.drawable_layout_about);


        wAuth = FirebaseAuth.getInstance();
        firebaseUser = wAuth.getCurrentUser();

        //Go to your specific database directory or Child
        dbRef = FirebaseDatabase.getInstance().getReference().child("users");

        View includedLayout_top_toolbar = findViewById(R.id.top_toolbar);
        TextView tv_top_toolbar = includedLayout_top_toolbar.findViewById(R.id.tv_top_toolbar);
        tv_top_toolbar.setText("About Us");

        View includedLayout = findViewById(R.id.drawable_layout);
        TextView current_user = (TextView)includedLayout.findViewById(R.id.txtview_curr_user);
        TextView current_acc_no = (TextView )includedLayout.findViewById(R.id.txtview_curr_acc_no);

        if(wAuth.getCurrentUser()!=null){
            dbRef.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Users users = snapshot.getValue(Users.class);
                        String name = users.getFname() + " " + users.getMname().substring(0, 1) + ". " + users.getLname();

                        current_user.setText(name);
                        current_acc_no.setText("Acc No. " + users.getAccno());

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
    public void ClickMenu(View view){
        //Open drawer
        ProfileActivity.openDrawer(drawerLayout);
    }

    @Override
    public void onBackPressed() {
        // overrides back button so it won't go to previous activity
    }


    public void ClickLogo(View view){
        //Close drawer
        ProfileActivity.closeDrawer(drawerLayout);
    }

    public void ClickHome(View view){
        //Redirect activity to Home
        ProfileActivity.redirectActivity(this, ProfileActivity.class);
    }

    public void ClickBillingNotice(View view){
        //Redirect activity to Billing notice
        ProfileActivity.redirectActivity(this, BillingNotice.class);

    }

    public void ClickAboutus(View view){
        //Recreate activity
        recreate();
    }

    public void ClickLogOut(View view){
        //Close App
        ProfileActivity.ClickLogOut(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Close Drawer
        ProfileActivity.closeDrawer(drawerLayout);
    }
}
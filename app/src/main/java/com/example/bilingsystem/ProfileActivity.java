package com.example.bilingsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilingsystem.Model.Users;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    //initialize variable
    DrawerLayout drawerLayout;

    private FirebaseAuth wAuth;
    FirebaseUser firebaseUser;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Hooks
        drawerLayout = findViewById(R.id.drawable_layout_profile);

        wAuth = FirebaseAuth.getInstance();
        firebaseUser = wAuth.getCurrentUser();

        //Go to your specific database directory or Child
        dbRef = FirebaseDatabase.getInstance().getReference().child("users");

        View includedLayout_top_toolbar = findViewById(R.id.top_toolbar);
        TextView tv_top_toolbar = includedLayout_top_toolbar.findViewById(R.id.tv_top_toolbar);
        tv_top_toolbar.setText("Profile");

        View includedLayout = findViewById(R.id.drawable_layout);


        Button btn_edit_info = (Button) findViewById(R.id.btn_edit_info);

        //get current user from firebase
        //get current user from firebase

        TextView tv_nav_drwr_username = (TextView) includedLayout.findViewById(R.id.txtview_curr_user);
        TextView tv_nav_drwr_acc_no = (TextView) includedLayout.findViewById(R.id.txtview_curr_acc_no);

        TextView tv_profile_name = (TextView) findViewById(R.id.tv_home_name);
        TextView tv_acc_no = (TextView) findViewById(R.id.tv_home_acc_no_);
        TextView tv_address = (TextView) findViewById(R.id.tv_home_address);

        TextView tv_recent_total_amount_due = findViewById(R.id.tv_recent_total_amount_due);
        TextView tv_recent_total_after_due = findViewById(R.id.tv_recent_total_after_due);

        LinearLayout total_layout = (LinearLayout) findViewById(R.id.total_layout);


        if (wAuth.getCurrentUser() != null) {
            dbRef.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Users users = snapshot.getValue(Users.class);
                        String name = users.getFname() + " " + users.getMname().substring(0, 1) + ". " + users.getLname();

                        tv_nav_drwr_username.setText(name);
                        tv_nav_drwr_acc_no.setText("Acc No. " + users.getAccno());

                        //set text values on Home
                        tv_profile_name.setText(name);
                        tv_address.setText("Address:  " + users.getAddress());
                        tv_acc_no.setText("Account no: " + users.getAccno());

                        //set values on profile total
                        Log.d("Profile", users.getRecent_due_date()+" profile due");
                        Log.d("Profile", users.getRecent_total_amt_aftr_due()+" profile after");
                        Log.d("Profile", users.getRecent_total_amt_due()+" profile total");
                        Log.d("Profile", snapshot.child("recent_due_date").getValue()+" profile due date");

                            if (( !users.getRecent_due_date().equals("") ||
                                    !users.getRecent_total_amt_aftr_due().equals("") ||
                                    !users.getRecent_total_amt_aftr_due().equals(""))) {

                                total_layout.setVisibility(View.VISIBLE);

                                String total_amt = ShowSelectedBill.dec_fmt.format(
                                        Double.parseDouble(
                                        users.getRecent_total_amt_due()));
                                String date = users.getRecent_due_date();
                                tv_recent_total_amount_due.setText(total_amt);
                                tv_recent_total_after_due.setText( String.valueOf(users.getRecent_total_amt_aftr_due()));

                                //push notif
                                Push_Notif push_notif = new Push_Notif(ProfileActivity.this);
                                push_notif.sendNotification("Water Bill", "Your bill with total amount " + total_amt + " PHP "+
                                        " is due on " + date , 4);

                            }



                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        //edit profile button
        btn_edit_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert_builder = new AlertDialog.Builder(ProfileActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.alertdialogue_edit_profile, null);


                TextInputLayout txt_input_fname = alertLayout.findViewById(R.id.text_input_fname);
                ;
                TextInputLayout txt_input_mname = alertLayout.findViewById(R.id.text_input_mname);
                ;
                TextInputLayout txt_input_lname = alertLayout.findViewById(R.id.text_input_lname);
                ;
                TextInputLayout txt_input_address = alertLayout.findViewById(R.id.text_input_address);

                //get user data and setValues to edit texts in alert dailogue
                if (wAuth.getCurrentUser() != null) {
                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) { //singleValueEvent stops after onDataChange is executed
                            if (dataSnapshot.hasChild(firebaseUser.getUid())) { //addValueEvent keeps on listening and is useful for sorting recycler
                                //childevent listener for list
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    if (postSnapshot.getKey().equals(firebaseUser.getUid())) {
                                        Users users = postSnapshot.getValue(Users.class);

                                        txt_input_fname.getEditText().setText(users.getFname());
                                        txt_input_mname.getEditText().setText(users.getMname());
                                        txt_input_lname.getEditText().setText(users.getLname());
                                        txt_input_address.getEditText().setText(users.getAddress());
                                    }

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }

                alert_builder.setView(alertLayout);
                alert_builder.setCancelable(false);

                alert_builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // leave it empty it'll be overrided bellow
                    }
                });

                alert_builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = alert_builder.create();
                dialog.show();

                //recolor buttons of alert dialogue
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(getResources().getColor(R.color.design_default_color_on_secondary));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(R.color.design_default_color_on_secondary));

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (validateTxtInput(txt_input_fname) || validateTxtInput(txt_input_mname) ||
                                        validateTxtInput(txt_input_lname) || validateTxtInput(txt_input_address)) {

                                    Toast.makeText(view.getContext(), "Fields can't be empty!",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    //saves data in db

                                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) { //singleValueEvent stops after onDataChange is executed
                                            if (dataSnapshot.hasChild(firebaseUser.getUid())) { //addValueEvent keeps on listening and is useful for sorting recycler
                                                //childevent listener for list

                                                //edit stuff in db
                                                dbRef.child(firebaseUser.getUid()).child("fname").setValue(txt_input_fname.getEditText().getText().toString());
                                                dbRef.child(firebaseUser.getUid()).child("mname").setValue(txt_input_mname.getEditText().getText().toString());
                                                dbRef.child(firebaseUser.getUid()).child("lname").setValue(txt_input_lname.getEditText().getText().toString());
                                                dbRef.child(firebaseUser.getUid()).child("address").setValue(txt_input_address.getEditText().getText().toString());

                                                Toast.makeText(view.getContext(), "Profile saved!",
                                                        Toast.LENGTH_LONG).show();

                                                dialog.dismiss();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                }


                                //then set text to ui
                            }
                        });

            }
        });



    }

    @Override
    public void onBackPressed() {
        // overrides back button so it won't go to previous activity
    }

    public void ClickMenu(View view) {
        //Open drawer
        openDrawer(drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        //Open drawer layout
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void ClickLogo(View view) {
        //close drawer
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        //closer drawer layout
        //check condition
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            //when drawer is open
            //close drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void ClickHome(View view) {
        //recreate activity
        recreate();
    }

    public void ClickBillingNotice(View view) {
        //redirect activity to billing notice

        //goto and passkey to Viewing activity

        redirectActivity(this, BillingNotice.class);
    }

    public void ClickAboutus(View view) {
        //redirect activity to about us
        redirectActivity(this, Aboutus.class);
    }

    public static void ClickLogOut(Activity activity) {

        //Initialized alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //set title
        builder.setTitle("Logout");
        //set message
        builder.setMessage("Are you sure you want to logout?");
        //positive yes button
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //goto login page
                redirectActivity(activity, MainActivity.class);

            }
        });
        // negative no button
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Dismiss dialog
                dialog.dismiss();
            }
        });
        //show dialog
        builder.show();
    }


    public static void redirectActivity(Activity activity, Class aclass) {
        //Initialize intent
        Intent intent = new Intent(activity, aclass);
        //set flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //start activity
        activity.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //close drawer
        closeDrawer(drawerLayout);
    }

    private boolean validateTxtInput(TextInputLayout eText) {

        if (TextUtils.isEmpty(eText.getEditText().getText().toString().trim())) {
            eText.setError("Required!");
            eText.requestFocus();
            return true;
        } else {
            return false;
        }
    }

}
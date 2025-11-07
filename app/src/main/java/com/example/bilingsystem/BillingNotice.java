package com.example.bilingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilingsystem.Model.Bill;
import com.example.bilingsystem.Model.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class BillingNotice extends AppCompatActivity {
    //initialize variable

    RecyclerView recyclerView;
    DrawerLayout drawerLayout;
    private FirebaseAuth wAuth;
    FirebaseUser firebaseUser;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_notice);

        //Hooks
        drawerLayout = findViewById(R.id.drawable_layout_billing);

        wAuth = FirebaseAuth.getInstance();
        firebaseUser = wAuth.getCurrentUser();

        //Go to your specific database directory or Child
        dbRef = FirebaseDatabase.getInstance().getReference().child("users");

        //

        View includedLayout_top_toolbar = findViewById(R.id.top_toolbar);
        TextView tv_top_toolbar = includedLayout_top_toolbar.findViewById(R.id.tv_top_toolbar);
        tv_top_toolbar.setText("Billing");

        //for nav drawer
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
        //for nav drawer

        //codes for show bill included layout
        View includedLayout_show_bill = findViewById(R.id.layout_show_bill);
        recyclerView = includedLayout_show_bill.findViewById(R.id.selected_bill_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String selected_key = firebaseUser.getUid();
        Log.d("BillingNotice","uid: "+ selected_key);

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();


        //checks if user has bills
        Query query = dbref.child("users").child(selected_key).child("bills");

            query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    if (snapshot.getValue() != null) {
                        try {

                            DatabaseReference dbref_user_bill = FirebaseDatabase.getInstance().getReference();
                            dbref_user_bill = FirebaseDatabase.getInstance().getReference();
                            FirebaseRecyclerOptions<Bill> options
                                    = new FirebaseRecyclerOptions.Builder<Bill>()
                                    .setQuery(query, Bill.class)
                                    .build();


                            FirebaseRecyclerAdapter<Bill, Viewholder_bills> fbr_Adapter =
                                    new FirebaseRecyclerAdapter<Bill, Viewholder_bills>(options) {

                                        @NonNull
                                        @Override
                                        public Viewholder_bills onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                                            View bill_view = LayoutInflater.from(parent.getContext())
                                                    .inflate(R.layout.card_view_bills, parent, false);
                                            return new Viewholder_bills(bill_view, BillingNotice.this);

                                        }

                                        @Override
                                        protected void onBindViewHolder(@NonNull Viewholder_bills holder, int position, @NonNull Bill model) {

//                        public void setItem(String bill_no, String date_from,
//                                String date_to, String date_due, String meter_no,
//                                String current_reading, String prev_reading,
//                                String  consumption, String other_consumption, String total_consumption,
//                                String maintenance_fee, String current_due, String total_amount_due,
//                                String penalty_after_due, String total_amount_after_due)

                                            Log.d("showselectedbill",model.getBill_no()+" asd"); //checking if model is null

                                            holder.setItem(
                                                    String.valueOf(model.getBill_no()),
                                                    String.valueOf(model.getPeriod_from()),
                                                    String.valueOf(model.getPeriod_to()),
                                                    String.valueOf(model.getDue_date()),
                                                    String.valueOf(model.getMeter_no()),
                                                    String.valueOf(model.getCurrent_reading()),
                                                    String.valueOf(model.getPrev_reading()),
                                                    String.valueOf(model.getConsumption()),
                                                    String.valueOf(model.getOther_consumption()),
                                                    String.valueOf(model.getTotal_consumption()),
                                                    String.valueOf(model.getMaintenance_fee()),
                                                    String.valueOf(model.getCurr_due()),
                                                    String.valueOf(model.getTotal_amount_due()),
                                                    String.valueOf(model.getPenalty()),
                                                    String.valueOf(model.getTotal_after_due())
                                            );

                                        }
                                    };

                            recyclerView.setAdapter(fbr_Adapter);
                            fbr_Adapter.startListening();


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(BillingNotice.this, "You have no bills yet!",
                                Toast.LENGTH_LONG).show();
                        Log.e("TAG ", " Bill is null.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("onCancelled", " cancelled");
            }

        });





    }

    public void ClickMenu(View view){
        //Open drawer
        ProfileActivity.openDrawer(drawerLayout);
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
        //Recreate activity
        recreate();
    }

    public void ClickAboutus(View view){
        //Redirect activity to about us
        ProfileActivity.redirectActivity(this, Aboutus.class);
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

    @Override
    public void onBackPressed() {
        // overrides back button so it won't go to previous activity
    }
}
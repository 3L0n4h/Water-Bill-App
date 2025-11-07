package com.example.bilingsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class Billing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertdialogue_new_bill);

        Bundle bundle= getIntent().getExtras();

        String selected_acc_no = (String) bundle.get("xtra_acc_no"); //getString for string, getInt for int.

        String selected_name = bundle.getString("xtra_name");
        String selected_address = bundle.getString("xtra_address");

        Log.d("Billing-debug","data in bundle "+selected_acc_no+" "+ selected_name+" "+selected_address);

        String selected_acc = getIntent().getStringExtra("curr_acc_no");
        Log.d("Billing-debug"," "+ selected_acc);

    }

    @Override
    public void onBackPressed() {
        // overrides back button so it won't go to previous activity
    }

}
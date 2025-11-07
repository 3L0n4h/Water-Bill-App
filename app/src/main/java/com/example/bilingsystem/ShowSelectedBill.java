package com.example.bilingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilingsystem.Model.Bill;
import com.example.bilingsystem.Model.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.StringValue;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static com.example.bilingsystem.StaffUI.isEmptyEditText;
import static com.example.bilingsystem.StaffUI.isZeroEditText;

public class ShowSelectedBill extends AppCompatActivity {

    RecyclerView recyclerView;
    private DatabaseReference dbref;
    private FirebaseDatabase firebasedb = FirebaseDatabase.getInstance();
    String selected_key;
    public static DecimalFormat dec_fmt = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_selected_bill);


        recyclerView = findViewById(R.id.selected_bill_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShowSelectedBill.this)); //StaffUI.this

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        selected_key = (String) bundle.get("user-key");
        Log.d("ViewSelectedUserBill", "user key from intent: " + selected_key);


        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
        Query query_bills = dbref.child("users").child(selected_key).child("bills");

        dbref = FirebaseDatabase.getInstance().getReference();
        FirebaseRecyclerOptions<Bill> options
                = new FirebaseRecyclerOptions.Builder<Bill>()
                .setQuery(query_bills, Bill.class)
                .build();


        FirebaseRecyclerAdapter<Bill, Viewholder_bills> fbr_Adapter =
                new FirebaseRecyclerAdapter<Bill, Viewholder_bills>(options) {

                    @NonNull
                    @Override
                    public Viewholder_bills onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        Intent bill_intent = getIntent();

                        View bill_view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.card_view_bills, parent, false);
                        return new Viewholder_bills(bill_view, ShowSelectedBill.this, bill_intent);

                    }

                    @Override
                    protected void onBindViewHolder(@NonNull Viewholder_bills holder, int position, @NonNull Bill model) {


//                        public void setItem( String bill_no, String date_from,
//                                String date_to, String date_due, String meter_no,
//                                String current_reading, String prev_reading,
//                                String  consumption, String other_consumption, String total_consumption,
//                                String maintenance_fee, String current_due, String total_amount_due,
//                                String penalty_after_due, String total_amount_after_due)

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
                                String.valueOf(dec_fmt.format(model.getMaintenance_fee())),
                                String.valueOf(dec_fmt.format(model.getCurr_due())),
                                String.valueOf(dec_fmt.format(model.getTotal_amount_due())),
                                String.valueOf(dec_fmt.format(model.getPenalty())),
                                String.valueOf(dec_fmt.format(model.getTotal_after_due()))
                        );

                       //check if admin

                        //get Values in selected Card
                        final String position_key = getRef(position).getKey(); //card position
                        Log.d("Showbill", "position key value is " + position_key);
                        int bill_no = getItem(position).getBill_no();
                        String date_from = getItem(position).getPeriod_from();
                        String date_to = getItem(position).getPeriod_to();
                        String due_date = getItem(position).getDue_date();
                        int meter_no = getItem(position).getMeter_no();
                        int curr_read = getItem(position).getCurrent_reading();
                        int prev_read = getItem(position).getPrev_reading();
                        int consumption = getItem(position).getConsumption();
                        int other_consume = getItem(position).getOther_consumption();
                        int total_consume = getItem(position).getTotal_consumption();
                        double maint_fee = getItem(position).getMaintenance_fee();
                        double curr_due = getItem(position).getCurr_due();
                        double total_amt_due = getItem(position).getTotal_amount_due();
                        double penalty = getItem(position).getPenalty();
                        double total_after_due = getItem(position).getTotal_after_due();


                        //make new obj bill to pass the values
                        Bill current_bill = new Bill(bill_no, date_from, date_to, due_date, meter_no, curr_read, prev_read, consumption,
                                other_consume, total_consume, maint_fee, curr_due, total_amt_due, penalty, total_after_due);

                        //bill constructor
//                        int bill_no, String period_from, String period_to, String due_date,int meter_no, int current_reading,
//                        int prev_reading, int consumption, int other_consumption, int total_consumption, double maintenance_fee,
//                        double curr_due,double total_amount_due, double penalty, double total_after_due


                        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                show_edit_dialogue(holder.context, position, position_key, current_bill);

                            }
                        });

                        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                DatabaseReference dbref_del = FirebaseDatabase.getInstance().getReference()
                                        .child("users").child(selected_key).child("bills");

                                dbref_del.addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String snapshot_key = snapshot.getKey();

                                        for (DataSnapshot snap : snapshot.getChildren()) {
                                            Log.d("keysnapfor", snap.getKey());
                                            Log.d("snapfor key to delete", snap.getValue() + "");

                                            String bn = snap.child("bill_no").getValue().toString();
                                            Log.d("bnequals", bn.equals(bill_no) + " bill no" + bill_no);
                                            if (bn.equals(String.valueOf(bill_no))) {
                                                dbref_del.child(snap.getKey()).removeValue();

                                                //check if recent is deleted
                                                DatabaseReference check_ifrecent_del = FirebaseDatabase.getInstance().
                                                        getReference().child("users").child(selected_key);

                                                check_ifrecent_del.child("recent_due_date").addListenerForSingleValueEvent(
                                                        new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (snapshot.getValue().equals(due_date)) {
                                                                    Map<String, Object> updates = new HashMap<String, Object>();
                                                                    updates.put("recent_due_date", "");
                                                                    updates.put("recent_total_amt_due", "");
                                                                    updates.put("recent_total_amt_aftr_due", "");

                                                                    DatabaseReference dbref_find_bill = FirebaseDatabase.getInstance().
                                                                            getReference().child("users").child(selected_key);
                                                                    dbref_find_bill.updateChildren(updates);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        }
                                                );


                                            }
                                        }
                                        Toast.makeText(view.getContext(), "Record Deleted!", Toast.LENGTH_LONG).show();
                                        //refresh screen
                                        ShowSelectedBill.this.recreate(); // test if refresh works

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                //toast here
                            }
                        });

                    }


                };

        recyclerView.setAdapter(fbr_Adapter);
        fbr_Adapter.startListening();
    }

    private void show_edit_dialogue(Context context, int position, String position_key, Bill current_bill) {

        //shows dialog box for updating data
        //initialize all dialogue layout contents
        LayoutInflater inflater = getLayoutInflater();
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(context);

        View alertLayout = inflater.inflate(R.layout.alertdialogue_bill_update, null);
        alert_builder.setView(alertLayout);


        TextView tv_bill_no = alertLayout.findViewById(R.id.tv_update_bill_no);

        EditText et_period_from = alertLayout.findViewById(R.id.et_update_period_from);
        EditText et_period_to = alertLayout.findViewById(R.id.et_update_period_to);
        EditText et_due_date = alertLayout.findViewById(R.id.et_update_due_date);

        EditText et_meter_no = alertLayout.findViewById(R.id.et_update_bill_meter_no);
        EditText et_current_reading = alertLayout.findViewById(R.id.et_update_current_reading);

        TextView tv_prev_reading = alertLayout.findViewById(R.id.tv_udpate_show_prev_reading);

        EditText et_consumption = alertLayout.findViewById(R.id.et_update_consumption);
        EditText et_other_consumption = alertLayout.findViewById(R.id.et_update_other_consume);

        TextView tv_total_consumption = alertLayout.findViewById(R.id.tv_update_total_consume);

        EditText et_maintenance = alertLayout.findViewById(R.id.et_update_meter_maintenance);
        EditText et_curr_due = alertLayout.findViewById(R.id.et_update_current_due);
        TextView tv_total_amount_due = alertLayout.findViewById(R.id.tv_update_total_amount_due);

        EditText et_penalty = alertLayout.findViewById(R.id.et_update_penalty);
        TextView tv_total_amount_after_due = alertLayout.findViewById(R.id.tv_update_after_due);

        int curr_bill_no = current_bill.getBill_no();
        //set to text values from Bill object
        tv_bill_no.setText(String.valueOf(current_bill.getBill_no()));

        et_period_from.setText(current_bill.getPeriod_from());
        et_period_to.setText(current_bill.getPeriod_to());
        et_due_date.setText(current_bill.getDue_date());

        et_meter_no.setText(String.valueOf(current_bill.getMeter_no()));
        et_current_reading.setText(String.valueOf(current_bill.getCurrent_reading()));

        tv_prev_reading.setText(String.valueOf(current_bill.getPrev_reading()));

        et_consumption.setText(String.valueOf(current_bill.getConsumption()));
        et_other_consumption.setText(String.valueOf(current_bill.getOther_consumption()));

        tv_total_consumption.setText(String.valueOf(current_bill.getTotal_consumption()));

        et_maintenance.setText(String.valueOf(current_bill.getMaintenance_fee()));
        et_curr_due.setText(String.valueOf(current_bill.getCurr_due()));
        tv_total_amount_due.setText(String.valueOf(current_bill.getTotal_amount_due()));

        et_penalty.setText(String.valueOf(current_bill.getPenalty()));
        tv_total_amount_after_due.setText(String.valueOf(current_bill.getTotal_after_due()));

        //show datepicker
        StaffUI.showDatePicker(et_period_from);
        StaffUI.showDatePicker(et_period_to);
        StaffUI.showDatePicker(et_due_date);

        //textWatcher for computing totals
        TextWatcher watcher_totalConsumption = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(et_consumption.getText().toString().trim())
                        || !TextUtils.isEmpty(et_other_consumption.getText().toString().trim())
                ) {
                    int firstValue = TextUtils.isEmpty(et_consumption.getText().toString().trim()) ? 0 : Integer.parseInt(et_consumption.getText().toString().trim());
                    int secondValue = TextUtils.isEmpty(et_other_consumption.getText().toString().trim()) ? 0 : Integer.parseInt(et_other_consumption.getText().toString().trim());

                    int answer = firstValue + secondValue;

                    Log.i("total consumption ", String.valueOf(answer));
                    tv_total_consumption.setText(String.valueOf(answer));
                } else {
                    tv_total_consumption.setText("");
                }

            }
        };

        TextWatcher watcher_total_amt_due = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(et_curr_due.getText().toString().trim())
                        || !TextUtils.isEmpty(et_maintenance.getText().toString().trim())
                ) {
                    double firstValue = TextUtils.isEmpty(et_curr_due.getText().toString().trim()) ? 0 : Double.parseDouble(et_curr_due.getText().toString().trim());
                    double secondValue = TextUtils.isEmpty(et_maintenance.getText().toString().trim()) ? 0 : Double.parseDouble(et_maintenance.getText().toString().trim());

                    double answer = firstValue + secondValue;
                    double roundOff_ans = Math.round(answer * 100.0) / 100.0;

                    Log.i("total amount due ", String.valueOf(roundOff_ans));
                    tv_total_amount_due.setText(String.valueOf(dec_fmt.format(roundOff_ans)));

                } else {
                    tv_total_amount_due.setText("");
                }

            }
        };

        TextWatcher watcher_total_amt_after_due = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(tv_total_amount_due.getText().toString().trim())
                        || !TextUtils.isEmpty(et_penalty.getText().toString().trim())

                ) {
                    double firstValue = TextUtils.isEmpty(tv_total_amount_due.getText().toString().trim()) ? 0 : Double.parseDouble(tv_total_amount_due.getText().toString().trim());
                    double secondValue = TextUtils.isEmpty(et_penalty.getText().toString().trim()) ? 0 : Double.parseDouble(et_penalty.getText().toString().trim());

                    double answer = firstValue + secondValue;
                    double roundOff_ans = Math.round(answer * 100.0) / 100.0;

                    Log.i("total after due ", String.valueOf(roundOff_ans));
                    tv_total_amount_after_due.setText(String.valueOf(dec_fmt.format(roundOff_ans)));

                } else {
                    tv_total_amount_after_due.setText("");
                }
            }
        };
        //textWatcher for computing totals

        //asign textwatcher to editTexts
        et_consumption.addTextChangedListener(watcher_totalConsumption);
        et_other_consumption.addTextChangedListener(watcher_totalConsumption);

        et_maintenance.addTextChangedListener(watcher_total_amt_due);
        et_curr_due.addTextChangedListener(watcher_total_amt_due);

        et_penalty.addTextChangedListener(watcher_total_amt_after_due);
        et_maintenance.addTextChangedListener(watcher_total_amt_after_due);
        et_curr_due.addTextChangedListener(watcher_total_amt_after_due);
        //asign textwatcher to editTexts

        alert_builder.setCancelable(false);

        alert_builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // leave it empty it'll be override bellow
            }
        });

        alert_builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog_update = alert_builder.create();
        dialog_update.show();

        dialog_update.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(getResources().getColor(R.color.design_default_color_on_secondary));
        dialog_update.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(getResources().getColor(R.color.design_default_color_on_secondary));

        dialog_update.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isEmptyEditText(et_period_from) || isEmptyEditText(et_period_to) ||
                                isEmptyEditText(et_due_date) || isEmptyEditText(et_meter_no) ||
                                isEmptyEditText(et_current_reading) ||
                                isEmptyEditText(et_consumption) || isEmptyEditText(et_other_consumption) ||
                                isEmptyEditText(et_maintenance) || isEmptyEditText(et_curr_due) ||
                                isEmptyEditText(et_penalty)) {

                        } else {
                            if (isZeroEditText(et_current_reading) || isZeroEditText(et_consumption) ||
                                    isZeroEditText(et_curr_due) || isZeroEditText(et_penalty)) {
                                Toast.makeText(view.getContext(), "Amount cannot be 0!",
                                        Toast.LENGTH_LONG).show();
                            } else {

                                //gets value from ui
                                int bill_no = Integer.parseInt(String.valueOf(tv_bill_no.getText()));
                                String period_from = String.valueOf(et_period_from.getText());
                                String period_to = String.valueOf(et_period_to.getText());
                                String due_date = String.valueOf(et_due_date.getText());
                                //parse date here if you're have to change it to timestamp

                                int meter_no = Integer.parseInt(String.valueOf(et_meter_no.getText()));
                                int current_reading = Integer.parseInt(String.valueOf(et_current_reading.getText())); //this becomes N/A

                                int consumption = Integer.parseInt(String.valueOf(et_consumption.getText()));
                                int other_consumption = Integer.parseInt(String.valueOf(et_other_consumption.getText()));
                                Log.d("numformat", tv_total_consumption.getText().getClass().getName()+" "+" total consumption data type");
                                Log.d("numformat", tv_total_consumption.getText().toString().getClass().getName()+" "+" total consumption data type after to Str");

                                int total_consumption = Integer.parseInt(String.valueOf(tv_total_consumption.getText()).trim()); //there was Numberformat exception input string error here

                                double maintenance_fee = Double.parseDouble(et_maintenance.getText().toString());
                                double current_due = Double.parseDouble(et_curr_due.getText().toString());
                                double total_amount_due = Double.parseDouble(tv_total_amount_due.getText().toString());
                                double penalty = Double.parseDouble(et_penalty.getText().toString());
                                double total_after_due = Double.parseDouble(tv_total_amount_after_due.getText().toString());

                                int prev_reading;
                                Log.d("tv-prev-reading", (tv_prev_reading.getText().toString().equals("N/A") + " " + tv_prev_reading.getText()));
                                if (tv_prev_reading.getText().toString().equals("N/A")) {

                                    prev_reading = 0;
                                } else {
                                    prev_reading = Integer.parseInt(String.valueOf(tv_prev_reading.getText()));
                                }


                                Bill user_bill = new Bill(bill_no, period_from, period_to, due_date, meter_no, current_reading, prev_reading,
                                        consumption, other_consumption, total_consumption, maintenance_fee, current_due, total_amount_due,
                                        penalty, total_after_due);

                                //save update to db
                                Log.d("user-key", selected_key + "");
                                Log.d("curr-bill", curr_bill_no + "");
                                String curr_bill = String.valueOf(curr_bill_no).trim();
                                DatabaseReference dbref_find_bill = FirebaseDatabase.getInstance().
                                        getReference().child("users").child(selected_key);

                                dbref_find_bill.child("bills").child(position_key).addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        bill_no, period_from, period_to, due_date, meter_no, current_reading, prev_reading,
//                                                consumption, other_consumption, total_consumption, maintenance_fee, current_due, total_amount_due,
//                                                penalty, total_after_due
                                        Log.d("snapshotBill", snapshot.getValue() + "");
                                        HashMap<String, Object> chosen_bill = new HashMap<>();
                                        chosen_bill.put("bill_no", bill_no);
                                        chosen_bill.put("consumption",consumption);
                                        chosen_bill.put("curr_due", current_due);
                                        chosen_bill.put("current_reading", current_reading);
                                        chosen_bill.put("due_date", due_date);
                                        chosen_bill.put("maintenance_fee", maintenance_fee);
                                        chosen_bill.put("meter_no", meter_no);
                                        chosen_bill.put("other_consumption", other_consumption);
                                        chosen_bill.put("penalty", penalty);
                                        chosen_bill.put("period_from", period_from);
                                        chosen_bill.put("period_to", period_to);
                                        chosen_bill.put("prev_reading", prev_reading);
                                        chosen_bill.put("total_after_due",total_after_due);
                                        chosen_bill.put("total_amount_due", total_amount_due);
                                        chosen_bill.put("total_consumption",total_consumption);
//

                                        dbref_find_bill.child("bills").child(position_key).updateChildren(chosen_bill).addOnCompleteListener(

                                                new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(view.getContext(), "Record has been updated!", Toast.LENGTH_LONG).show();
                                                            ShowSelectedBill.this.recreate();
                                                        } else {
                                                            Toast.makeText(view.getContext(), "Failed to update record!", Toast.LENGTH_LONG).show();
                                                            Log.d("updateFailed", task.getException().getMessage());
                                                        }
                                                    }
                                                });

                                        //checks if recent bill is edited
                                        //then update date on user child
                                        if (dbref_find_bill.child("recent_due_date").equals(due_date)) {

                                            Map<String, Object> updates = new HashMap<String, Object>();
                                            updates.put("recent_due_date", due_date);
                                            updates.put("recent_total_amt_due", total_amount_due + "");
                                            updates.put("recent_total_amt_aftr_due", total_after_due + "");
                                            dbref_find_bill.updateChildren(updates);
                                            startActivity(new Intent(view.getContext(), StaffUI.class));
                                        }


                                        dialog_update.dismiss();
                                        //refresh screen
                                        ShowSelectedBill.this.recreate();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                    }
                }
        );
    }


}
package com.example.bilingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilingsystem.Model.Bill;
import com.example.bilingsystem.Model.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StaffUI extends AppCompatActivity {

    private DatabaseReference dbref;
    private FirebaseDatabase firebasedb = FirebaseDatabase.getInstance();
    RecyclerView recyclerView;
    Users user;
    private static DecimalFormat decimal_format = new DecimalFormat("0.00");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_ui);

        recyclerView = findViewById(R.id.staff_ui_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //StaffUI.this

        user = new Users();

        // firebasedb.getReference("Billing-record"); //should be unique billing-id

        dbref = FirebaseDatabase.getInstance().getReference();
        FirebaseRecyclerOptions<Users> options
                = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(dbref.child("users"), Users.class)
                .build();


        FirebaseRecyclerAdapter<Users, Viewholder_users> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Users, Viewholder_users>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull Viewholder_users holder, int position, @NonNull Users model) {
                        //UsersModel:,String accno, String fname, String mname, String lname, String address


                        holder.setitem(model.getAccno(), model.getFname(), model.getMname(), model.getLname(), model.getAddress());

                        //codes for knowing which card is selected
                        //gets item position then the value in the card
                        final String position_key = getRef(position).getKey(); //get firebase key

                        String acc_no = getItem(position).getAccno();
                        String name = getItem(position).getFname() + " " + getItem(position).getMname().substring(0, 1) +"."
                                + " " + getItem(position).getLname();
                        String addrss = getItem(position).getAddress();

                        //get current user query
                        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("users");
                        Query query = dbref.orderByChild("accno").equalTo(acc_no);

                        holder.btn_new_bill.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //pass data to billing activity
                                //shows dialog box for inserting new data
                                //initialize all dialogue layout contents
                                LayoutInflater inflater = getLayoutInflater();

                                AlertDialog.Builder alert_builder = new AlertDialog.Builder(holder.context);

                                View alertLayout = inflater.inflate(R.layout.alertdialogue_new_bill, null);
                                alert_builder.setView(alertLayout);

                                //constructor int bill_no, String period_from, String period_to, String due_date, int meter_reading,
                                // int consumption, int other_consumption, int total_consumption,
                                // double maintenance_fee, double total_amount_due, double penalty, double total_after_due

                                TextView tv_dialog_acc_no = alertLayout.findViewById(R.id.tv_dialog_acc_no);
                                TextView tv_dialog_name = alertLayout.findViewById(R.id.tv_dialog_name);
                                TextView tv_dialog_address = alertLayout.findViewById(R.id.tv_dialog_address);
                                TextView tv_bill_no = alertLayout.findViewById(R.id.tv_bill_no);

                                EditText et_period_from = alertLayout.findViewById(R.id.et_period_from);
                                EditText et_period_to = alertLayout.findViewById(R.id.et_period_to);
                                EditText et_due_date = alertLayout.findViewById(R.id.et_due_date);

                                EditText et_meter_no = alertLayout.findViewById(R.id.et_bill_meter_no);
                                EditText et_current_reading = alertLayout.findViewById(R.id.et_current_reading);

                                TextView tv_prev_reading =  alertLayout.findViewById(R.id.tv_show_prev_reading);

                                EditText et_consumption = alertLayout.findViewById(R.id.et_consumption);
                                EditText et_other_consumption = alertLayout.findViewById(R.id.et_other_consume);

                                TextView tv_total_consumption = alertLayout.findViewById(R.id.tv_total_consume);

                                EditText et_maintenance = alertLayout.findViewById(R.id.et_meter_maintenance);
                                EditText et_curr_due = alertLayout.findViewById(R.id.et_current_due);
                                TextView tv_total_amount_due = alertLayout.findViewById(R.id.tv_total_amount_due);

                                EditText et_penalty = alertLayout.findViewById(R.id.et_penalty);
                                TextView tv_total_amount_after_due = alertLayout.findViewById(R.id.tv_after_due);

                                //fills in alert dialogue with values
                                int bill_no = new Random().nextInt(1999999 - 1000000) + 100000; //generate random num

                                tv_dialog_acc_no.setText("Account No: " + acc_no);
                                tv_dialog_name.setText("Name: " + name);
                                tv_dialog_address.setText("Address: " + addrss);
                                tv_bill_no.setText(bill_no + "");

                                //fills in alert dialogue with values

                                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("users");
                                Query query_check_bill = dbref.orderByChild("accno").equalTo(acc_no);
                                query_check_bill.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            String key = "";
                                            Boolean withbills = false;
                                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                                key = snap.getKey();//user key
                                                Log.d("dbref-view-btn", " key is " + key);
                                                if (snap.hasChild("bills")) {
                                                    withbills = true;
                                                }else{
                                                    withbills = false;
                                                    tv_prev_reading.setText("N/A");
                                                }
                                            }


                                            //get previous reading then show it on alert dialogue
                                            //get previous bill by using user key
                                            Query query_prev_bill = dbref.child(key).child("bills").orderByKey().limitToLast(1);
                                            query_prev_bill.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                   Log.d("q-prev-bill", dataSnapshot.getValue()+"");
                                                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                                       String previous_reading = snap.child("current_reading").getValue().toString();
                                                       tv_prev_reading.setText(previous_reading);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    Log.d("q-prev-bill", "error: "+databaseError);
                                                }
                                            });


                                        } else {
                                            Log.d("dbref-view-btn", " snapshot doesn't exist!");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });



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

                                            Log.e("totalconsume fmt test", answer+"");
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

                                            Log.e("total amount due ", String.valueOf(roundOff_ans));
                                            Log.e("data type of DF", decimal_format.format(roundOff_ans).getClass().getName());
                                            tv_total_amount_due.setText(String.valueOf(decimal_format.format(roundOff_ans)));

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

                                            Log.e("total after due ", String.valueOf(roundOff_ans));
                                            tv_total_amount_after_due.setText(String.valueOf(decimal_format.format(roundOff_ans)));

                                        } else {
                                            tv_total_amount_after_due.setText("");
                                        }
                                    }
                                };

                                //textWatcher for computing totals
                                et_consumption.addTextChangedListener(watcher_totalConsumption);
                                et_other_consumption.addTextChangedListener(watcher_totalConsumption);

                                et_maintenance.addTextChangedListener(watcher_total_amt_due);
                                et_curr_due.addTextChangedListener(watcher_total_amt_due);

                                et_penalty.addTextChangedListener(watcher_total_amt_after_due);

                                //show date picker when edit text is clicked
                                showDatePicker(et_period_from);
                                showDatePicker(et_period_to);
                                showDatePicker(et_due_date);

                                // disallow cancel of AlertDialog on click of back button and outside touch
                                // it's a way around to so it can check if edit texts are not null
                                alert_builder.setCancelable(false);

                                alert_builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
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

                                AlertDialog dialog = alert_builder.create();
                                dialog.show();


                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                                        .setTextColor(getResources().getColor(R.color.design_default_color_on_secondary));
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                        .setTextColor(getResources().getColor(R.color.design_default_color_on_secondary));

                                //alert save button
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                if (isEmptyEditText(et_period_from) || isEmptyEditText(et_period_to) ||
                                                        isEmptyEditText(et_due_date) || isEmptyEditText(et_meter_no) ||
                                                        isEmptyEditText(et_current_reading) ||
                                                        isEmptyEditText(et_consumption) || isEmptyEditText(et_other_consumption) ||
                                                        isEmptyEditText(et_maintenance) || isEmptyEditText(et_curr_due) ||
                                                        isEmptyEditText(et_penalty)) {

                                                    Toast.makeText(view.getContext(), "Fill in the fields are required!",
                                                            Toast.LENGTH_LONG).show();
                                                } else {

                                                   Log.d("DateType b4 isZero","etcurrread "+et_current_reading.getClass().getName()
                                                   +" etconsum " +  et_consumption.getClass().getName()+ " etcurrdue "+ et_curr_due.getClass().getName()+" penalty"+
                                                           et_penalty.getClass().getName());


                                                    if (isZeroEditText(et_current_reading) ||
                                                            isZeroEditText(et_consumption) ||
                                                            isZeroEditText(et_curr_due) ||
                                                            isZeroEditText(et_penalty)) { //when going back, numformat exception assumption Decimal formatting makes this string
                                                        Toast.makeText(view.getContext(), "Cannot be 0!",
                                                                Toast.LENGTH_LONG).show();
                                                    } else {

                                                        //get values from editText

                                                        int bill_no = Integer.parseInt(String.valueOf(tv_bill_no.getText()));
                                                        String period_from = String.valueOf(et_period_from.getText());
                                                        String period_to = String.valueOf(et_period_to.getText());
                                                        String due_date = String.valueOf(et_due_date.getText());
                                                        //parse date here if you're have to change it to timestamp

                                                        int meter_no = Integer.parseInt(String.valueOf(et_meter_no.getText()));
                                                        int current_reading = Integer.parseInt(String.valueOf(et_current_reading.getText())); //this becomes N/A

                                                        int consumption = Integer.parseInt(String.valueOf(et_consumption.getText()));
                                                        int other_consumption = Integer.parseInt(String.valueOf(et_other_consumption.getText()));
                                                        int total_consumption = Integer.parseInt(String.valueOf(tv_total_consumption.getText()));

                                                        double maintenance_fee = Double.parseDouble(et_maintenance.getText().toString());
                                                        double current_due = Double.parseDouble(et_curr_due.getText().toString());
                                                        double total_amount_due = Double.parseDouble(tv_total_amount_due.getText().toString());
                                                        double penalty = Double.parseDouble(et_penalty.getText().toString());
                                                        double total_after_due = Double.parseDouble(tv_total_amount_after_due.getText().toString());

                                                        int prev_reading;
                                                        Log.d("tv-prev-reading", ( tv_prev_reading.getText().toString().equals("N/A") +" "+ tv_prev_reading.getText()));
                                                        if(tv_prev_reading.getText().toString().equals("N/A")){

                                                            prev_reading = 0;
                                                        }else{ prev_reading = Integer.parseInt(String.valueOf(tv_prev_reading.getText()));}

                                                    //                 Bill constructor   int bill_no, String period_from, String period_to, String due_date,int meter_no, int current_reading,
                                                    //                int prev_reading, int consumption, int other_consumption, int total_consumption, double maintenance_fee,
                                                    //                double curr_due,double total_amount_due, double penalty, double total_after_due


                                                        Bill user_bill = new Bill(bill_no, period_from, period_to, due_date, meter_no, current_reading, prev_reading,
                                                                consumption, other_consumption, total_consumption, maintenance_fee, current_due, total_amount_due,
                                                                penalty, total_after_due);

                                                        //save to firebase given acc_no as key


                                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                                if (dataSnapshot.exists()) {
                                                                    String key = "";
                                                                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                                                        key = snap.getKey();
                                                                        Log.d("dbref-alert", "key is " + key);
                                                                    }

                                                                    dbref.child(key).child("bills").push().setValue(user_bill)
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        //  progressDialog.dismiss();
                                                                                        Toast.makeText(view.getContext(), "Record Saved!", Toast.LENGTH_LONG).show();
                                                                                        dialog.dismiss();
                                                                                        //refresh screen
                                                                                        StaffUI.this.recreate();
//                                                                                        Activity activity = (Activity) view.getContext();
//                                                                                        activity.recreate();
                                                                                    } else {
                                                                                        String message = task.getException().getMessage();
                                                                                        Toast.makeText(view.getContext(), "Record not saved.  " + message, Toast.LENGTH_LONG).show();
                                                                                    }
                                                                                }
                                                                            });

                                                                    //store recent bill due date, total amount due, total amount after due on user node
                                                                    // so we can easily retrieve it without giving heavy load on query
                                                                    DatabaseReference dbref_recent = dbref.child(key);
                                                                    Map<String, Object> updates = new HashMap<String, Object>();
                                                                    updates.put("recent_due_date", due_date);
                                                                    updates.put("recent_total_amt_due", total_amount_due+"");
                                                                    updates.put("recent_total_amt_aftr_due", total_after_due+"");

                                                                    dbref_recent.updateChildren(updates);

                                                                } else {
                                                                    Log.d("dbref-alert", "accno: datasnapshot doesn't exist");
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                                Log.d("dbref-query", "error.");
                                                            }
                                                        });
                                                    }


                                                }
                                            }
                                        }
                                );
                            }
                        });


                        //checks each user if their bills is empty, then disables btn_view
                        if(model.getRecent_total_amt_due() != null && !model.getRecent_total_amt_due().equals("")){
                            holder.btn_view_bills.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                String key = "";
                                                Boolean hasbills = false;
                                                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                                    key = snap.getKey();
                                                    Log.d("dbref-view-btn", " key is " + key);
                                                    if (snap.hasChild("bills")) {
                                                        hasbills = true;
                                                    }
                                                }
                                                //go and passkey to Viewing activity
                                                Intent intent = new Intent(view.getContext(), ShowSelectedBill.class);
                                                intent.putExtra("user-key", key);
                                                startActivity(intent);
                                            } else {
                                                Log.d("dbref-view-btn", " snapshot doesn't exist!");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }
                            });
                        }else{
                            //disable and change color of view button
                            holder.btn_view_bills.setTextColor(Color.parseColor("#FF88BCB8"));
                            holder.btn_view_bills.setBackgroundColor(Color.parseColor("#e3e7f2"));
                            holder.btn_view_bills.setEnabled(false);
                        }

                    }

                    @NonNull
                    @Override
                    public Viewholder_users onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view
                                = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.card_view_user, parent, false);
                        return new Viewholder_users(view, StaffUI.this);
                    }

                };


        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }


    @Override
    public void onBackPressed() {
        //Initialized alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set title
        builder.setTitle("Logout");
        //set message
        builder.setMessage("Are you sure you want to logout");
        //positive yes button
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //reset isAdmin Variable then goto login page
                MainActivity.isAdmin = false;
                startActivity(new Intent(StaffUI.this, MainActivity.class));

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


    public static boolean isEmptyEditText(EditText eText) {
        if (TextUtils.isEmpty(eText.getText().toString())) {
            eText.setError("Required!");
            eText.requestFocus();
            return true;
        } else {
            return false;
        }
    }

    public static boolean isZeroEditText(EditText eText) {
        String strng = String.valueOf(eText.getText());
        if ( Double.parseDouble(strng) == 0) {
            eText.setError("value cannot be 0!");
            eText.requestFocus();
            return true;
        } else {
            return false;
        }
    }


    static void showDatePicker(EditText editText) {

        editText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.d("datePicker", "edit text with date is clicked");
                        //Calendar now = Calendar.getInstance();
                        final Calendar c = Calendar.getInstance();

                        DatePickerDialog dpd = new DatePickerDialog(v.getContext(),
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int month, int day) {

                                        Calendar calendar = Calendar.getInstance();

                                        calendar.set(year, month, day);
                                        String dateString = new SimpleDateFormat("MMMM dd, yyyy").format(calendar.getTime());
                                        editText.setText(dateString);

                                        //  editText.setText((month + 1) + "-" + day + "-" + year);
                                    }
                                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
                        dpd.show();

                    }
                }

        );

    }

}
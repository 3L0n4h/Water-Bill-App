package com.example.bilingsystem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Viewholder_bills extends RecyclerView.ViewHolder{
    private static DecimalFormat dec_fmt = new DecimalFormat("0.00");

    //declare items in card
    TextView tv_bill_no, tv_date_from, tv_date_to, tv_date_due, tv_meter_no, tv_current_reading,
            tv_prev_reading, tv_consumption,tv_other_consumption, tv_total_consumption, tv_maintenance_fee,
            tv_current_due, tv_total_amount_due, tv_penalty_after_due, tv_total_amount_after_due;

    Button btn_edit, btn_delete;
    LinearLayout admin_layout;
    Context context;
    private Intent intent;

    public Viewholder_bills(@NonNull View itemView, Context context) {
        super(itemView);
        this.context = context;
    }

    public Viewholder_bills(@NonNull View itemView, Context context, Intent intent) {
        super(itemView);
        this.context = context;
        this.intent = intent;
    }


//    int bill_no, String period_from, String period_to, String due_date,int meter_no, int current_reading,
//    int prev_reading, int consumption, int other_consumption, int total_consumption, double maintenance_fee,
//    double curr_due,double total_amount_due, double penalty, double total_after_due, String status

    public void setItem(String bill_no, String date_from,
                        String date_to, String date_due, String meter_no,
                        String current_reading, String prev_reading,
                        String  consumption, String other_consumption, String total_consumption,
                        String maintenance_fee, String current_due, String total_amount_due,
                        String penalty_after_due, String total_amount_after_due){

        //init
        tv_bill_no = itemView.findViewById(R.id.tv_card_bill_no);
        tv_date_from = itemView.findViewById(R.id.tv_card_period_from);
        tv_date_to = itemView.findViewById(R.id.tv_card_period_to);
        tv_date_due = itemView.findViewById(R.id.tv_card_due_date);
        tv_meter_no = itemView.findViewById(R.id.txt_card_meter_no);
        tv_current_reading = itemView.findViewById(R.id.txt_card_current_reading);
        tv_prev_reading = itemView.findViewById(R.id.tv_card_prev_reading);
        tv_consumption = itemView.findViewById(R.id.tv_card_consumption);
        tv_other_consumption = itemView.findViewById(R.id.tv_card_other_consume);
        tv_total_consumption = itemView.findViewById(R.id.tv_card_total_consume);
        tv_maintenance_fee = itemView.findViewById(R.id.tv_card_meter_maintenance);
        tv_current_due = itemView.findViewById(R.id.tv_card_current_due);
        tv_total_amount_due = itemView.findViewById(R.id.tv_card_total_amount_due);
        tv_penalty_after_due = itemView.findViewById(R.id.tv_card_penalty);
        tv_total_amount_after_due = itemView.findViewById(R.id.tv_card_after_due);

        Log.d("VH-bill-isadmin", ""+MainActivity.isAdmin); //false even if admin

        //checks if user is admin
//
//        Bundle bundle = intent.getExtras();
//        Boolean bundle_isadmin = (Boolean) bundle.get("isAdmin");
//        Log.d("isadmin-bundle", bundle_isadmin+" ");

        if(MainActivity.isAdmin) {
            admin_layout = (LinearLayout) itemView.findViewById(R.id.adminLayout_UpdateDelete);
            admin_layout.setVisibility(View.VISIBLE);
            btn_edit = itemView.findViewById(R.id.btn_card_edit_bill);
            btn_delete = itemView.findViewById(R.id.btn_card_del_bill);
        }

        //setText
        tv_bill_no.setText(bill_no);
        tv_date_from.setText(date_from);
        tv_date_to.setText(date_to);
        tv_date_due.setText(date_due);
        tv_meter_no.setText(meter_no);
        tv_current_reading.setText(current_reading);
        tv_prev_reading.setText(prev_reading);
        tv_consumption.setText(consumption);
        tv_other_consumption.setText(other_consumption);
        tv_total_consumption.setText(total_consumption);
        tv_maintenance_fee.setText( dec_fmt.format(Double.parseDouble(maintenance_fee)));
        tv_current_due.setText(dec_fmt.format(Double.parseDouble(current_due)));
        tv_total_amount_due.setText(dec_fmt.format(Double.parseDouble(total_amount_due)));
        tv_penalty_after_due.setText(dec_fmt.format(Double.parseDouble(penalty_after_due)));
        tv_total_amount_after_due.setText(dec_fmt.format(Double.parseDouble(total_amount_after_due)));

    }

}

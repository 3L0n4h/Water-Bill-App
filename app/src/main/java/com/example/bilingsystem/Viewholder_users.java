package com.example.bilingsystem;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Viewholder_users extends RecyclerView.ViewHolder{

    //declare items in card_view_user
    TextView card_accno, card_concessionaire, card_address;
    Button btn_new_bill;
    Button btn_view_bills;

    Context context;

    public Viewholder_users(@NonNull View itemView, Context context) {
        super(itemView);
        this.context = context;
    }

    //set items to user cardview
    public void setitem(String accno, String fname, String mname, String lname, String address ){
        //init
        card_accno = itemView.findViewById(R.id.textview_card_accno);
        card_concessionaire = itemView.findViewById(R.id.textview_card_concessionaire);
        card_address = itemView.findViewById(R.id.textview_card_address);

        btn_new_bill = itemView.findViewById(R.id.btn_card_billing);
        btn_view_bills = itemView.findViewById(R.id.btn_card_view_bill);

        //settext
        card_accno.setText(accno);
        card_concessionaire.setText(fname+" "+mname.substring(0,1)+". "+lname);
        card_address.setText(address);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}

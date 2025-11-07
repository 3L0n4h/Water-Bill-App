package com.example.bilingsystem;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilingsystem.Model.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

// FirebaseRecyclerAdapter is a class provided by
// FirebaseUI. it provides functions to bind, adapt and show
// database contents in a Recycler View
public class Adapter_Users extends FirebaseRecyclerAdapter<
        Users, Adapter_Users.personsViewholder> {

    private String selected_acc_no;

    public Adapter_Users(
            @NonNull FirebaseRecyclerOptions<Users> options)
    {
        super(options);
    }


    // Function to bind the view in Card view(here
    // "person.xml") iwth data in
    // model class(here "person.class")
    @Override
    protected void onBindViewHolder(@NonNull personsViewholder holder, int position, @NonNull Users model) {
        // Add firstname from model class (here
        // "Users.class")to appropriate view in Card
        // view (here "card_view.xml")

        holder.account_no.setText(model.getAccno());
        holder.concessionaire.setText(model.getFname()+" "+model.getMname()+" "+ model.getLname());
        holder.address.setText(model.getAddress());

        //gets item position then the value in the card
        final String position_key = getRef(position).getKey();
        Log.d("Staff-ui-debug", "position key value is "+position_key);
        String acc_no = getItem(position).getAccno();
        String name = getItem(position).getFname() +" "+ getItem(position).getMname().substring(0,1)
                +" "+getItem(position).getLname();
        String addrss = getItem(position).getAddress();

        Log.d("adapter_user_accno"," "+ model.getAccno());
        Log.d("adapter_user_concess"," "+ model.getFname()+" "+model.getMname()+" "+ model.getLname());
        Log.d("adapter_user_address"," "+ model.getAddress());


        Log.i("adapter-users"," current user is "+acc_no+" "+name+" "+addrss+" positionkey"+ position_key);

        //for button in card
//        holder.btn_goto_bill.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //pass data to billing activity
//
//                Intent intent =new Intent(view.getContext(), Billing.class);
//                intent.putExtra("xtra_acc_no", acc_no);
//                intent.putExtra("xtra_name", name);
//                intent.putExtra("xtra_address", addrss);
//                view.getContext().startActivity(intent);
//            }
//        });

//
//        btn_goto_bill.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(),Billing.class);
//                //save user acc no.
//                intent.putExtra("curr_acc_no", String.valueOf(account_no));
//                v.getContext().startActivity(intent);
//            }
//        });
    }



    // Function to tell the class about the Card view "card_view.xml")in
    // which the data will be shown
    @NonNull
    @Override
    public personsViewholder
    onCreateViewHolder(@NonNull ViewGroup parent,
                       int viewType)
    {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_user, parent, false);
        return new Adapter_Users.personsViewholder(view);
    }

    // Sub Class to create references of the views in Card
    // view (here "card_view.xml")
    class personsViewholder
            extends RecyclerView.ViewHolder {
        TextView account_no, concessionaire, address;
        Button btn_goto_bill;
        public personsViewholder(@NonNull View itemView)
        {
            super(itemView);

            account_no = itemView.findViewById(R.id.textview_card_accno);
            concessionaire = itemView.findViewById(R.id.textview_card_concessionaire);
            address = itemView.findViewById(R.id.textview_card_address);




        }

    }
}

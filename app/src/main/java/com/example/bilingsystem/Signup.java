package com.example.bilingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilingsystem.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class Signup extends AppCompatActivity implements View.OnClickListener {

    private TextView reg_login,reg_btn,editTextAccNo;
    private EditText editTextfname, editTextmname, editTextlname, editTextAddress, editTextemail, editTextpassword;
    private ProgressBar progressbar;
    int account_number;
    private FirebaseAuth wAuth;
    private DatabaseReference db_ref_users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        wAuth = FirebaseAuth.getInstance();

        db_ref_users = FirebaseDatabase.getInstance().getReference("users");


        reg_login = (Button) findViewById(R.id.reg_login_btn);
        reg_login.setOnClickListener(this);
        reg_btn = (Button) findViewById(R.id.reg_btn);
        reg_btn.setOnClickListener(this);

       // editTextAccNo = (TextView) findViewById(R.id.accountnumber);

        editTextmname = (EditText) findViewById(R.id.middlename);
        editTextfname = (EditText) findViewById(R.id.textview_accno);
        editTextlname = (EditText) findViewById(R.id.textview_concessionaire);
        editTextAddress = (EditText) findViewById(R.id.address);
        editTextemail = (EditText) findViewById(R.id.email);
        editTextpassword = (EditText) findViewById(R.id.password);

        progressbar = (ProgressBar) findViewById(R.id.progressbar);

        //generate random number for account number
        account_number = new Random().nextInt(1999999 - 1000000) + 100000;
       // editTextAccNo.setText(String.valueOf(account_number));
       // editTextAccNo.setEnabled(false);

        Log.i("signup-debug","dbref ");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.reg_login_btn:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.reg_btn:
                reg_btn();
                break;
        }
    }

    private void reg_btn() {
        String email= editTextemail.getText().toString().trim();
        String accno = String.valueOf(account_number);
        String fname= editTextfname.getText().toString().trim();
        String mname= editTextmname.getText().toString().trim();
        String lname= editTextlname.getText().toString().trim();
        String address= editTextAddress.getText().toString().trim();
        String password = editTextpassword.getText().toString().trim();


        if(fname.isEmpty()){
            editTextfname.setError("First Name is Required!!");
            editTextfname.requestFocus();
            return;
        }

        if(mname.isEmpty()){
            editTextmname.setError("Middle Name is Required!!");
            editTextmname.requestFocus();
            return;
        }

        if(lname.isEmpty()){
            editTextlname.setError("Last Name is Required!!");
            editTextlname.requestFocus();
            return;
        }

        if(address.isEmpty()){
            editTextAddress.setError("Address is Required!!");
            editTextlname.requestFocus();
            return;
        }

        if(email.isEmpty()){
            editTextemail.setError("Email is Required!!");
            editTextemail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextemail.setError("Please Provide a Valid Email");
            editTextemail.requestFocus();
        }

        if(password.isEmpty()){
            editTextpassword.setError("Password is Required!!");
            editTextpassword.requestFocus();
            return;
        }

        if (password.length() <6){
            editTextpassword.setError("Password should be (6) Character!!");
            editTextpassword.requestFocus();
            return;
        }


        progressbar.setVisibility(View.VISIBLE);
        wAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Users users = new Users(accno, fname, mname, lname, address, email, password, "","","");

                            db_ref_users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(Signup.this,"User has been successfully registered!!",Toast.LENGTH_LONG).show();
                                    progressbar.setVisibility(View.GONE);
                                }
                            });

                            //should go to login page
                            startActivity(new Intent(Signup.this, MainActivity.class));

                            Log.d("signup-debug", "createUserWithEmail:success");
                           // FirebaseUser user = wAuth.getCurrentUser();
                            progressbar.setVisibility(View.GONE);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("signup-debug", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Signup.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                            progressbar.setVisibility(View.GONE);
                        }
                    }
                });


    }
}
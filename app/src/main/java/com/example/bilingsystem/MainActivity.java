package com.example.bilingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button register;
    private EditText editTextEmail, editTextPassword;
    private Button login, forgot_pwd;
    private String admin_mail, admin_password;
    public static Boolean isAdmin = false;
    private FirebaseAuth wAuth;
    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //checks if user has internet
        if (!isNetworkAvailable()) {

            AlertDialog alertDialog_networcheck = new AlertDialog.Builder(this).create();

            alertDialog_networcheck.setTitle("Check your network!");
            alertDialog_networcheck.setMessage("This app requires internet connection. " +
                    "Connect to the internet and restart the App.");
            alertDialog_networcheck.setButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    //exit app
                    MainActivity.this.finish();
                    System.exit(0);

                }
            });

            alertDialog_networcheck.show();

        } else {

            //check internet permission is enabled
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.INTERNET}, 5622);
                }

            } else {

                register = (Button) findViewById(R.id.signupscreen);
                register.setOnClickListener(this);

                login = (Button) findViewById(R.id.login);
                login.setOnClickListener(this);

                forgot_pwd = (Button) findViewById(R.id.forgot_passwoord);
                forgot_pwd.setOnClickListener(this);

                editTextEmail = (EditText) findViewById(R.id.email);
                editTextPassword = (EditText) findViewById(R.id.password);

                progressbar = (ProgressBar) findViewById(R.id.progressbar);
                wAuth = FirebaseAuth.getInstance();

            }

        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signupscreen:
                startActivity(new Intent(this, Signup.class));
                break;

            case R.id.login:
                userLogin();
                break;

            case R.id.forgot_passwoord:
                Log.d("forgot-pwd"," forgot pwd is clicked");
                startActivity(new Intent(MainActivity.this, ForgotPassword.class));
                break;
        }
    }

    private void gotoForgotPwd() {
        Log.d("forgot-pwd"," forgot pwd is clicked");
        startActivity(new Intent(MainActivity.this, ForgotPassword.class));
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is Required!!");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please Enter Valid Email!!");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextEmail.setError("Password is Required!!");
            editTextEmail.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextEmail.setError("Password should be (6) Character!!");
            editTextEmail.requestFocus();
            return;
        }

        progressbar.setVisibility(View.VISIBLE);

        DatabaseReference dbref_admin = FirebaseDatabase.getInstance().getReference().child("admins");

        dbref_admin.addListenerForSingleValueEvent(new ValueEventListener() {  //recoded to show button in view bills if privelege is admin
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        //checks if user is one of the admins
                        Boolean emailCheck = email.equals(snap.child("email").getValue());
                        Boolean passwordCheck = password.equals(snap.child("password").getValue());
                        if (emailCheck && passwordCheck)
                        {
                            isAdmin = !isAdmin; //set admin to true
                        }
                    }

                    if (isAdmin) {
                        Log.d("login-info", " you are an admin");
                        Intent intent = new Intent(MainActivity.this, StaffUI.class);
                        intent.putExtra("isAdmin", isAdmin);
                        startActivity(intent);
                        //  startActivity(new Intent(MainActivity.this, StaffUI.class));

                    }else{
                        wAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    if (user.isEmailVerified()) {
                                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                                        intent.putExtra("isAdmin", isAdmin);
                                        startActivity(intent);

                                        // startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                                        //redirect to user profile
                                    } else {
                                        user.sendEmailVerification();


                                        Toast.makeText(MainActivity.this, "Check your Email to Verify your Account!!", Toast.LENGTH_LONG).show();
                                        progressbar.setVisibility(View.GONE);
                                    }

                                } else {
                                    Toast.makeText(MainActivity.this, "Failed to login! Try Again!", Toast.LENGTH_LONG).show();
                                    progressbar.setVisibility(View.GONE);
                                }
                            }
                        });

                    }

                } else {
                    Log.d("login-snap", " No admin found in the databaase.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        // exit app
      //  finish();
        finishAffinity();
    }

}
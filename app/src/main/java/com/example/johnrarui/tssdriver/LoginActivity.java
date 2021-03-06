package com.example.johnrarui.tssdriver;

import android.app.ProgressDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.support.annotation.NonNull;

import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;

import android.view.View;

import android.widget.Button;

import android.widget.EditText;

import android.widget.TextView;

import android.widget.Toast;



import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {



    EditText email, password;

    Button signIn, signUp;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    String Email, Password,token;

    ProgressDialog progressDialog;



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);



        mAuth = FirebaseAuth.getInstance();



        email = (EditText) findViewById(R.id.editTextEmail);

        password = (EditText) findViewById(R.id.editTextPassword);





        signIn = (Button) findViewById(R.id.buttonSignIn);

        signUp = (Button) findViewById(R.id.buttonSignUp);



        progressDialog=new ProgressDialog(this);
        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get extra data included in the Intent
                token = intent.getStringExtra("token");
                Toast.makeText(context, token, Toast.LENGTH_SHORT).show();

                // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        };
        LocalBroadcastManager.getInstance(LoginActivity.this).registerReceiver(
                mMessageReceiver, new IntentFilter("TokenUpdates"));



        signIn.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {



                Email = email.getText().toString();

                Password=password.getText().toString();



                if (Email.equals("")||Password.equals("")){

                    Toast.makeText(LoginActivity.this, "please fill in all your details", Toast.LENGTH_SHORT).show();





                }else{

                    progressDialog.setMessage("loading...");

                    progressDialog.setCancelable(false);

                    progressDialog.show();

                    mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override

                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()){

                                Toast.makeText(LoginActivity.this, "Unable to sign in", Toast.LENGTH_SHORT).show();

                                progressDialog.dismiss();

                            }else{
                                DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("tokens");
                                Map map =new HashMap();
                                map.put("token",token);
                                myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Intent intent=new Intent(LoginActivity.this,MapsActivity.class);

                                        startActivity(intent);
                                    }
                                });


                                Toast.makeText(LoginActivity.this, "Signin successful", Toast.LENGTH_SHORT).show();

                                progressDialog.dismiss();

                            }

                        }

                    });

                }

            }

        });

        signUp.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

//                Intent intent=new Intent(LoginActivity.this,SignUpActivity.class);
//
//                startActivity(intent);

            }

        });



        mAuthListener= new FirebaseAuth.AuthStateListener() {

            @Override

            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null&&token==null) {

                    Intent intent=new Intent(LoginActivity.this,MapsActivity.class);

                    startActivity(intent);

                    // User is signed in

                } else {

                    // User is signed out

                    Toast.makeText(LoginActivity.this, "no user is signed in", Toast.LENGTH_SHORT).show();

                }

            }

        };

    }



    @Override

    public void onStart() {

        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

    }



    @Override

    public void onStop() {

        super.onStop();

        if (mAuthListener != null) {

            mAuth.removeAuthStateListener(mAuthListener);

        }

    }

}
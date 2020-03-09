/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter.whatsapp;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText usernamEditText;
    EditText passwordEditText;
    TextView loginTextView;
    Button signUpButton;

    Boolean signUpModeActive = true;

    //  onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginTextView = findViewById(R.id.loginTextView);
        loginTextView.setOnClickListener(this);

        login();
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

//   1.Sign up clicked
    public void signupClicked(View view){
        usernamEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        if(usernamEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")){
            Toast.makeText(this, "Username or Password needed", Toast.LENGTH_SHORT).show();
        }
        else{
            if(signUpModeActive) {

              //1.1Sign Up
                ParseUser user = new ParseUser();
                user.setUsername(usernamEditText.getText().toString());
                user.setPassword(passwordEditText.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("Sign Up", "Success");
                            login();
                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else{
                //2.Login
                ParseUser.logInInBackground(usernamEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(user != null){
                            Log.i("Login","OK");
                            login();
                        }
                        else{
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

//   2.1.on click for Login Text - we can also assign the text a method
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.loginTextView){
            Log.i("Switch","Tapped");

            signUpButton = findViewById(R.id.signUpButton);

            if(signUpModeActive){
                signUpModeActive = false;
                signUpButton.setText("Login");
                loginTextView.setText("Or, Sign Up");
            }
            else{
                signUpModeActive = true;
                signUpButton.setText("Sign Up");
                loginTextView.setText("Or, Login");
            }
        }
    }


//    Move to User list Activity
    public void login(){
        if(ParseUser.getCurrentUser() != null ){
            startActivity(new Intent(getApplicationContext(), UserListActivity.class));
        }
    }
}
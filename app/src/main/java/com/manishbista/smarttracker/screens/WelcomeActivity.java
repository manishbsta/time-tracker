package com.manishbista.smarttracker.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.manishbista.smarttracker.R;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnReg;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        btnReg = findViewById(R.id.btnSignUpStart);
        btnLogin = findViewById(R.id.btnSignInStart);

        btnLogin.setOnClickListener(this);
        btnReg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if( id == R.id.btnSignUpStart) {
            startActivity(new Intent(WelcomeActivity.this, RegistrationActivity.class));
        } else if (id == R.id.btnSignInStart) {
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
        }
    }
}
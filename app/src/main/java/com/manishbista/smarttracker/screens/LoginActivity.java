package com.manishbista.smarttracker.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.manishbista.smarttracker.R;

public class LoginActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextInputLayout etEmail;
    private TextInputLayout etPassword;
    private Button btnLogin;

    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmailLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);

        mLoginProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        //set up toolbar
        toolbar = findViewById(R.id.toolbarLogin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Log in");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getEditText().getText().toString().trim();
                String password = etPassword.getEditText().getText().toString().trim();

                if (validation(email, password)) {
                    mLoginProgress.setTitle("Logging in");
                    mLoginProgress.setMessage("...just a second now.");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    signIn(email, password);
                }
            }
        });
    }

    private boolean validation(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            etEmail.getEditText().requestFocus();
            etEmail.getEditText().setError("Email address is empty");
            return false;
        } else if (TextUtils.isEmpty(password)) {
            etPassword.getEditText().requestFocus();
            etPassword.getEditText().setError("Password is empty");
            return false;
        }

        return true;
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mLoginProgress.dismiss();
                            Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginIntent);
                            finish();
                        } else {
                            mLoginProgress.hide();
                            Toast.makeText(LoginActivity.this, "Check your email and password again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
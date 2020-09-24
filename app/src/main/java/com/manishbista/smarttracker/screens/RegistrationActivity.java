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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.manishbista.smarttracker.R;

import java.util.HashMap;
import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference dbReference;

    private Toolbar mtoolbar;
    private TextInputLayout etDisplayName;
    private TextInputLayout etEmail;
    private TextInputLayout etPassword;
    private Button btnReg;

    private ProgressDialog mRegProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        etDisplayName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnReg = findViewById(R.id.btnRegister);

        mRegProgress = new ProgressDialog(this);

        mtoolbar = findViewById(R.id.toolbarReg);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Account Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String displayName = etDisplayName.getEditText().getText().toString().trim();
                String email = etEmail.getEditText().getText().toString().trim();
                String password = etPassword.getEditText().getText().toString().trim();

                if (validation(displayName, email, password)) {
                    mRegProgress.setTitle("Creating your account");
                    mRegProgress.setMessage("Please hold on a sec or two...");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    register(displayName, email, password);
                }
            }
        });
    }

    private boolean validation(String displayName, String email, String password) {
        if (TextUtils.isEmpty(displayName)) {
            Objects.requireNonNull(etDisplayName.getEditText()).requestFocus();
            etDisplayName.getEditText().setError("Display name is empty!");
            return false;
        } else if (TextUtils.isEmpty(email)) {
            Objects.requireNonNull(etEmail.getEditText()).requestFocus();
            etEmail.getEditText().setError("Email address is empty!");
            return false;
        } else if (TextUtils.isEmpty(password)) {
            Objects.requireNonNull(etPassword.getEditText()).requestFocus();
            etPassword.getEditText().setError("Password is empty!");
            return false;
        }
        return true;
    }

    private void register(final String displayName, final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            String uid = currentUser.getUid();
                            dbReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name", displayName);
                            userMap.put("email", email);
                            userMap.put("status", "Hey! Let's chat - ping me or send me a friend request.");
                            userMap.put("image", "empty");
                            userMap.put("thumb_image", "empty");

                            dbReference.setValue(userMap)
                                    .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            mRegProgress.dismiss();
                                            Intent regIntent = new Intent(RegistrationActivity.this, MainActivity.class);
                                            regIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(regIntent);
                                            finish();
                                        }
                                    });
                        } else {
                            mRegProgress.hide();
                            Toast.makeText(RegistrationActivity.this, "Registration Failed! Please check your details and try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
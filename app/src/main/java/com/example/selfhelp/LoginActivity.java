package com.example.selfhelp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import utils.JournalApi;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private Button createButton;
    private Button loginbutton;
    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;

    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseUser user;

    CollectionReference collectionReference = firebaseFirestore.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        firebaseAuth = FirebaseAuth.getInstance();
        createButton = findViewById(R.id.createAccountButton);
        loginbutton = findViewById(R.id.loginButton);
        emailEditText = findViewById(R.id.emailLogin);
        passwordEditText = findViewById(R.id.passwordLogin);
        progressBar = findViewById(R.id.progressBarLogin);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class));
                finish();
            }
        });
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                loginWithEmailIdPassword(email,password);
            }
        });
    }

    private void loginWithEmailIdPassword(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isComplete()){
                    user = firebaseAuth.getCurrentUser();
                    Log.d(TAG, "onComplete: Working now");
                    assert user != null;
                    String userId = user.getUid();
                    collectionReference.whereEqualTo("userId",userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            for(QueryDocumentSnapshot queryDocumentSnapshot: value){
                                JournalApi.getInstance().setUserId(queryDocumentSnapshot.getString("userId"));
                                JournalApi.getInstance().setUserName(queryDocumentSnapshot.getString("username"));
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(LoginActivity.this,PostJournalThought.class));
                            finish();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth!=null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
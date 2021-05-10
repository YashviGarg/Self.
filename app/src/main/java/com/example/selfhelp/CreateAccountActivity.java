package com.example.selfhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import utils.JournalApi;

public class CreateAccountActivity extends AppCompatActivity {
    private static final String TAG = "CreateAccountActivity";
    private Button createAccountButton;
    private EditText emailCreateAccount;
    private EditText passwordCreateAccount;
    private EditText usernameCreateAccount;
    private FirebaseUser currentUser;
    private ProgressBar progressBar;
    JournalApi journalApi;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        firebaseAuth = FirebaseAuth.getInstance();
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        journalApi = JournalApi.getInstance();

        createAccountButton = findViewById(R.id.createNewAccountButton);
        emailCreateAccount = findViewById(R.id.emailCreateAccount);
        passwordCreateAccount = findViewById(R.id.passwordCreateAccount);
        usernameCreateAccount = findViewById(R.id.usernameCreateAccount);
        progressBar = findViewById(R.id.progressBarCreateAccount);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailCreateAccount.getText().toString().trim();
                String password =  passwordCreateAccount.getText().toString().trim();
                String username = usernameCreateAccount.getText().toString().trim();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)){
                    createNewUserAccount(email,password,username);
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(CreateAccountActivity.this, "All fields required!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser!=null){
                    //task if user present
                }else{

                }
            }
        };
    }
    private void createNewUserAccount(String email, String password, final String username) {
        progressBar.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isComplete()) {
                        Log.d(TAG,"Working now");
                        currentUser = firebaseAuth.getCurrentUser();
                        assert currentUser != null;
                        final String currentUserId = currentUser.getUid();

                        Map<String, String> user = new HashMap<>();
                        user.put("userId", currentUserId);
                        user.put("username", username);

                        collectionReference.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                                        if(Objects.requireNonNull(task.getResult()).exists()){
                                            progressBar.setVisibility(View.INVISIBLE);


                                            JournalApi journalApi = JournalApi.getInstance();
                                            String name = task.getResult().getString("username");
                                            journalApi.setUserId(currentUserId);
                                            journalApi.setUserName(name);
                                            Intent intent = new Intent(CreateAccountActivity.this,PostJournalThought.class);
                                            startActivity(intent);
                                            Log.d(TAG,"Successful login");
                                            finish();
                                        }else{
                                            Log.d(TAG,"Error while adding a user!");

                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreateAccountActivity.this, "Incorrect!", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(CreateAccountActivity.this, "Enter all fields!", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth!=null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

}
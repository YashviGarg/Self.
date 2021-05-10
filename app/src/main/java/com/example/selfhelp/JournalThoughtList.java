package com.example.selfhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.selfhelp.model.Journal;
import com.example.selfhelp.ui.JournalRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import utils.JournalApi;

public class JournalThoughtList extends AppCompatActivity {
    private static final String TAG ="JournalThoughtList" ;
    private TextView emptyTextView;
    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth.AuthStateListener authStateListener;
    List<Journal> journalList = new ArrayList<>();
    private JournalRecyclerAdapter journalRecyclerAdapter;
    private RecyclerView recyclerView;



    CollectionReference collectionReference = db.collection("Journal");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_thought_list);

        firebaseAuth = FirebaseAuth.getInstance();
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);


        emptyTextView = findViewById(R.id.textView_empty);
        recyclerView = findViewById(R.id.recyclerViewlist);



        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.addItemMenuBar:
                if(currentUser!=null && firebaseAuth!=null){
                    startActivity(new Intent(JournalThoughtList.this,PostJournalThought.class));
                    finish();
                }
                break;
            case R.id.signOut:
                if(currentUser!=null && firebaseAuth!=null){
                    firebaseAuth.signOut();

                    startActivity(new Intent(JournalThoughtList.this,MainActivity.class));
                    finish();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        String userId = JournalApi.getInstance().getUserId();
        collectionReference.whereEqualTo("userId",userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    Log.d(TAG, "onSuccess: error ");
                    for(QueryDocumentSnapshot journals : queryDocumentSnapshots){
                        Journal journal = journals.toObject(Journal.class);
                        journalList.add(journal);
                    }
                    journalRecyclerAdapter = new JournalRecyclerAdapter(JournalThoughtList.this,journalList);
                    recyclerView.setAdapter(journalRecyclerAdapter);
                    journalRecyclerAdapter.notifyDataSetChanged();

                }else{
                    emptyTextView.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(JournalThoughtList.this, "An error has occured", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
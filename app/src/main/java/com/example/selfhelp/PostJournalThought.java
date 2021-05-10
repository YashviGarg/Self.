package com.example.selfhelp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.selfhelp.model.Journal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.Objects;

import utils.JournalApi;

public class PostJournalThought extends AppCompatActivity {
    private static final int REQUEST_CODE = 1 ;
    private static final String TAG = "PostJournalThought";
    private EditText titleEditText;
    private EditText thoughtEditText;
    private Button saveButton;
    private TextView nameTextView;
    private TextView dateTextView;
    private ImageView postCameraImageView;
    private ImageView imageViewPost;
    private ProgressBar progressBar;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser user;

    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    CollectionReference collectionReference = db.collection("Journal");
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_journal_thought);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);


        titleEditText = findViewById(R.id.editTextTitlePostJournal);
        thoughtEditText = findViewById(R.id.editTextPersonThoughtPostJournal);
        nameTextView = findViewById(R.id.nameJournalPostTextView);
        dateTextView = findViewById(R.id.dataJournalPostTextView);
        saveButton = findViewById(R.id.savePostJournalButton);
        postCameraImageView = findViewById(R.id.postImageIconImageView);
        imageViewPost = findViewById(R.id.imagePostedImageView);
        progressBar = findViewById(R.id.progressBarPost);

        nameTextView.setText(JournalApi.getInstance().getUserName());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                saveDataInFirebase();
            }
        });
        postCameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };

    }

    private void saveDataInFirebase() {
        final String title = titleEditText.getText().toString().trim();
        final String thought = thoughtEditText.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thought) && imageUri!=null) {
            final StorageReference filePath = storageReference
                    .child("journal_image")
                    .child("myimage_" + Timestamp.now().getSeconds());
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Journal journal = new Journal();
                            journal.setTitle(title);
                            journal.setThoughts(thought);
                            journal.setTimeAdded(new Timestamp(new Date()));
                            journal.setUserId(JournalApi.getInstance().getUserId());
                            journal.setUsername(JournalApi.getInstance().getUserName());
                            journal.setImageUrl(url);
                            collectionReference.add(journal).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(PostJournalThought.this, JournalThoughtList.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.INVISIBLE);

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.toString());

                        }
                    });
                }
            });
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Field missing or photo missing!", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            if(data!=null){
                imageUri = data.getData();
                imageViewPost.setImageURI(imageUri);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        user=firebaseAuth.getCurrentUser();
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
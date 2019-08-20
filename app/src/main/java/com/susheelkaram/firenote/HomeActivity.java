package com.susheelkaram.firenote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.susheelkaram.firenote.adapters.NotesAdapter;
import com.susheelkaram.firenote.model.Note;
import com.susheelkaram.firenote.utils.Constants;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private String phone;
    private String email;
    private String uid;
    private String notesPath;

    private Context mContext = this;

    private TextView textWelcomeNote;
    private Button btnSignOut;
    private FloatingActionButton fabAddNote;
    private RecyclerView recyclerNoteList;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;

    private NotesAdapter notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();

        textWelcomeNote = (TextView) findViewById(R.id.text_WelcomeMessage);
        fabAddNote = (FloatingActionButton) findViewById(R.id.fab_AddNewNote);
        recyclerNoteList = (RecyclerView) findViewById(R.id.recycler_NoteList);

        if (mUser == null) {
            return;
        }

        // Getting User Details
        uid = mUser.getUid();
        phone = mUser.getPhoneNumber();
        email = mAuth.getCurrentUser().getEmail();

        // Setting welcome Note
        textWelcomeNote.setText("Hi! " + phone);

        // Setting up RecyclerView
        recyclerNoteList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        setupRecyclerNoteList();

        // Add new Note button
        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, NoteEditorActivity.class);
                i.putExtra(Constants.KEY_EDITOR_TYPE, Constants.VAL_EDITOR_NEW);
                startActivity(i);
            }
        });
    }


    // Setting up FirestoreRecyclerAdapter with Query and Setting adapter
    private void setupRecyclerNoteList() {
        notesPath = "users/" + uid + "/notes";
        Query notesQuery = mFirestore.collection(notesPath).orderBy("timeStamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(notesQuery, Note.class)
                .build();

        notesAdapter = new NotesAdapter(options, this);

        Log.d("Fire_Count", "Item count is " + notesAdapter.getItemCount());

        recyclerNoteList.setAdapter(notesAdapter);
    }

    // Sign out the Current user
    private void signOut() {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // Menu Setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_SignOut:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        notesAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        notesAdapter.stopListening();
    }
}

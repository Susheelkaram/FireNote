package com.susheelkaram.firenote.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.susheelkaram.firenote.model.Note;

/**
 * Created by Susheel Kumar Karam
 * Website - SusheelKaram.com
 */
public class FireStoreAssistant {
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private String notesPath;
    private String uid;

    public FireStoreAssistant(Context context) {
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null) {
            uid = mAuth.getCurrentUser().getUid();
        }
        notesPath = "users/" + uid + "/notes";
    }

    public void saveNote(Note note) {
        CollectionReference notesRef = mFirestore.collection(notesPath);

        notesRef.add(note)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "Note added Successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "Failed to Add Note", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void deleteNote(String noteId) {
        String path = notesPath + "/" + noteId;
        Log.d("DELETE_PATH", path);

        DocumentReference noteDocumentRef = mFirestore.document(path);


        noteDocumentRef.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(mContext, "Note Deleted!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(mContext, "Couldn't Delete note", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateNote(String noteId, Note updatedNote) {
        String path = notesPath + "/" + noteId.trim();

        DocumentReference noteDocumentRef = mFirestore.document(path);

        noteDocumentRef.set(updatedNote)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(mContext, "Note updated!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(mContext, "Failed to Update note", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}

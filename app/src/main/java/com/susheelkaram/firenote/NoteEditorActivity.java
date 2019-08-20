package com.susheelkaram.firenote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.susheelkaram.firenote.model.Note;
import com.susheelkaram.firenote.utils.Constants;
import com.susheelkaram.firenote.utils.FireStoreAssistant;

public class NoteEditorActivity extends AppCompatActivity {
    private EditText inputNoteTitle;
    private EditText inputNoteContent;
    private Button btnSaveNote;
    private Button btnDeleteNote;
    private FloatingActionButton fabEdit;

    private FireStoreAssistant fireStoreAssistant;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

    private Context mContext = this;

    // Edit mode
    private String noteTitle;
    private String noteContent;
    private long noteTime;
    private String noteId;
    private boolean isInEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        fireStoreAssistant = new FireStoreAssistant(mContext);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        inputNoteTitle = (EditText) findViewById(R.id.input_NoteTitle);
        inputNoteContent = (EditText) findViewById(R.id.input_NoteContent);
        btnSaveNote = (Button) findViewById(R.id.button_SaveNote);
        btnDeleteNote = (Button) findViewById(R.id.button_DeleteNote);
        fabEdit = (FloatingActionButton) findViewById(R.id.fab_EditNote);

        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            if (extras.getString(Constants.KEY_EDITOR_TYPE).equals(Constants.VAL_EDITOR_UPDATE)) {
                isInEditMode = true;
                noteTitle = extras.getString(Constants.KEY_NOTE_TITLE);
                noteContent = extras.getString(Constants.KEY_NOTE_CONTENT);
                noteTime = extras.getLong(Constants.KEY_NOTE_TIME);
                noteId = extras.getString(Constants.KEY_NOTE_ID);

                // Showing/ Hiding buttons
                enableViewMode();

                // Setting data
                inputNoteTitle.setText(noteTitle);
                inputNoteContent.setText(noteContent);
            }
        }


        // SAVE Button
        btnSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInEditMode) {
                    updateNote();
                    enableViewMode();
                    return;
                }
                saveNote();
                finish();
            }
        });


        // DELETE button
        btnDeleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote(noteId);
                finish();
            }
        });


        // EDIT button
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEditMode();
            }
        });

    }

    private void saveNote() {
        String title = inputNoteTitle.getText().toString();
        String content = inputNoteContent.getText().toString();
        long time = System.currentTimeMillis();

        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setTimeStamp(time);

        fireStoreAssistant.saveNote(note);
    }

    private void updateNote() {
        String title = inputNoteTitle.getText().toString();
        String content = inputNoteContent.getText().toString();
        long time = System.currentTimeMillis();

        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setTimeStamp(time);

        fireStoreAssistant.updateNote(noteId, note);
    }

    private void deleteNote(String noteId) {
        fireStoreAssistant.deleteNote(noteId);
    }

    private void enableViewMode() {
        fabEdit.show();
        btnSaveNote.setVisibility(View.GONE);
        btnDeleteNote.setVisibility(View.GONE);
        inputNoteTitle.setFocusableInTouchMode(false);
        inputNoteTitle.clearFocus();
        inputNoteContent.setFocusableInTouchMode(false);
        inputNoteContent.clearFocus();
    }

    private void enableEditMode() {
        fabEdit.hide();
        btnSaveNote.setVisibility(View.VISIBLE);
        btnDeleteNote.setVisibility(View.VISIBLE);
        inputNoteTitle.setFocusableInTouchMode(true);
        inputNoteContent.setFocusableInTouchMode(true);
        inputNoteContent.requestFocus();
    }

}

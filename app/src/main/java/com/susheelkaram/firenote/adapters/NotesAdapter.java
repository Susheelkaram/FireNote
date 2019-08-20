package com.susheelkaram.firenote.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.susheelkaram.firenote.NoteEditorActivity;
import com.susheelkaram.firenote.R;
import com.susheelkaram.firenote.model.Note;
import com.susheelkaram.firenote.utils.Constants;

import java.text.SimpleDateFormat;

/**
 * Created by Susheel Kumar Karam
 * Website - SusheelKaram.com
 */
public class NotesAdapter extends FirestoreRecyclerAdapter<Note, NotesAdapter.NoteHolder> {
    private Context mContext;

    public NotesAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context context) {
        super(options);
        mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteHolder noteHolder, int position, @NonNull Note note) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, ''yy 'at' h:mm aaa");
        String timeStamp = sdf.format(note.getTimeStamp());


        noteHolder.textNoteTitle.setText(note.getTitle());
        noteHolder.textNoteContent.setText(note.getContent());
        noteHolder.textNoteTime.setText(timeStamp);

        noteHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchNoteEditor(getSnapshots().getSnapshot(position).getId(), note);
            }
        });
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteHolder(v);
    }

    class NoteHolder extends RecyclerView.ViewHolder{
      TextView textNoteTitle;
      TextView textNoteContent;
      TextView textNoteTime;

      public NoteHolder(@NonNull View itemView) {
          super(itemView);
          textNoteTitle = (TextView) itemView.findViewById(R.id.text_NoteItemTitle);
          textNoteContent = (TextView) itemView.findViewById(R.id.text_NoteItemContent);
          textNoteTime = (TextView) itemView.findViewById(R.id.text_NoteItemTime);
      }
  }

    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
        super.onError(e);
        Log.d("NotesAdapter", e.getMessage());
    }

    private void launchNoteEditor(String noteId, Note note){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_EDITOR_TYPE, Constants.VAL_EDITOR_UPDATE);
        bundle.putString(Constants.KEY_NOTE_ID, noteId);
        bundle.putString(Constants.KEY_NOTE_TITLE, note.getTitle());
        bundle.putString(Constants.KEY_NOTE_CONTENT, note.getContent());
        bundle.putLong(Constants.KEY_NOTE_TIME, note.getTimeStamp());

        Intent i = new Intent(mContext, NoteEditorActivity.class);
        i.putExtras(bundle);
        mContext.startActivity(i);
    }
}

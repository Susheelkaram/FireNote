package com.susheelkaram.firenote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class HomeActivity extends AppCompatActivity {
    private String phone;
    private String email;
    private String uid;

    private TextView textUserId;
    private Button buttonSignOut;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        textUserId = (TextView) findViewById(R.id.text_WelcomeMessage);
        buttonSignOut = (Button) findViewById(R.id.button_SignOut);

        if(mUser != null){
            uid = mUser.getUid();
            phone = mUser.getPhoneNumber();
            email = mAuth.getCurrentUser().getEmail();
            for(UserInfo user: mUser.getProviderData()){
                Toast.makeText(this, user.getProviderId(), Toast.LENGTH_SHORT).show();
            }
        }

        String welcomeMessage = "Hi! " + email
                + "\nUID: " + uid
                + "\nPhone: " + phone;
        textUserId.setText(welcomeMessage);

//        if(getIntent().getStringExtra(Constants.EXTRA_PHONE) != null){
//            userId = getIntent().getStringExtra(Constants.EXTRA_PHONE)
//                     + " " + getIntent().getStringExtra(Constants.EXTRA_EMAIL);
//            textUserId.setText("Hi \n" + userId);
//        }

        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}

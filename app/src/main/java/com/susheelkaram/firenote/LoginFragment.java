package com.susheelkaram.firenote;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    EditText inputLoginPhone;
    EditText inputLoginPassword;
    Button btnLogin;

    FirebaseAuth mAuth;
    Context mContext;


    public LoginFragment() {}

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inputLoginPhone = (EditText) view.findViewById(R.id.input_LoginPhoneNo);
        inputLoginPassword = (EditText) view.findViewById(R.id.input_LoginPassword);
        btnLogin = (Button) view.findViewById(R.id.button_Login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(areValidCredentials()){
                    String phone = inputLoginPhone.getText().toString();
                    String password = inputLoginPassword.getText().toString();
                    login(phone, password);
                }
            }
        });
    }

    private void login(String phoneNo, String password){
        String email = Constants.COUNTRY_CODE + phoneNo + "@" + Constants.DOMAIN;
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else {
                        String message = task.getException().getMessage();
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private boolean areValidCredentials(){
        String phone = inputLoginPhone.getText().toString();
        String password = inputLoginPassword.getText().toString();

        if(phone.isEmpty() || phone.length() != 10){
            inputLoginPhone.setError("Enter a valid Mobile No.");
            return false;
        }
        if(password.isEmpty()){
            inputLoginPassword.setError("Enter valid password");
            return false;
        }
        return true;
    }
}

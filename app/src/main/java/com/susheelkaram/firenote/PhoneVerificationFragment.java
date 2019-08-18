package com.susheelkaram.firenote;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneVerificationFragment extends Fragment {

    EditText inputPhone;
    EditText inputOtp;
    EditText inputPassword;
    Button buttonSendOtp;
    Button buttonSignUp;
    TextView textMobileVerifiedMessage;

    Context mContext;

    private boolean isOtpVerified = false;

    private FirebaseAuth mAuth;
    private String verificationId;
    private String COUNTRY_CODE = "+91";

    public PhoneVerificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_phone_verification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textMobileVerifiedMessage = (TextView) view.findViewById(R.id.text_MobileVerifiedMessage);
        inputPhone = (EditText) view.findViewById(R.id.input_PhoneNo);
        inputOtp = (EditText) view.findViewById(R.id.input_Otp);
        inputPassword = (EditText) view.findViewById(R.id.input_Password);
        buttonSignUp = (Button) view.findViewById(R.id.button_SignUp);
        buttonSendOtp = (Button) view.findViewById(R.id.button_SendOtp);


        buttonSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNo = inputPhone.getText().toString().trim();
                if (phoneNo != null && phoneNo.length() == 10) {
                    sendVerificationCode(phoneNo);
                } else {
                    inputPhone.setError("Invalid Phone number");
                    inputPhone.requestFocus();
                }
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    // Final Sign up Method
    private void signUp() {
        String userInputCode = inputOtp.getText().toString().trim();

        if (isOtpVerified && mAuth.getCurrentUser() != null) {
            linkCredentials();
            return;
        }
        if (!userInputCode.isEmpty()) {
            verifyCode(userInputCode);
        } else {
            inputOtp.setError("Invalid OTP!");
        }
    }

    private void linkCredentials() {
        String phone = mAuth.getCurrentUser().getPhoneNumber();
        String password = inputPassword.getText().toString();

        if (!isValidPassword(password)) {
            inputPassword.setError("Password should be atleast 8 Characters");
            return;
        }

        AuthCredential emailCredential = getEmailCredential(phone, password);
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.linkWithCredential(emailCredential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d("SIGN_UP", "Credential linking SUCCESSFUL");

                        String phone = mAuth.getCurrentUser().getPhoneNumber();
                        String email = mAuth.getCurrentUser().getEmail();

                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("SIGN_UP", "Credential linking FAILED");
                    }
                }
            });
        }
    }

    // Sends OTP
    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                COUNTRY_CODE + phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                phoneAuthCallbacks
        );
    }

    // Verifies OTP
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    // Sign in with OTP
    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String phone = mAuth.getCurrentUser().getPhoneNumber();
                            String password = inputPassword.getText().toString();
                            if (isValidPassword(password)) {
                                AuthCredential emailCredential = getEmailCredential(phone, password);
                                linkCredentials();
                            }
                        } else {
                            Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // PhoneAuth Callbacks
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneAuthCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(@NonNull String verifId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(verifId, forceResendingToken);
                    verificationId = verifId;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    inputOtp.setText(phoneAuthCredential.getSmsCode());

                    buttonSendOtp.setVisibility(View.GONE);
                    inputOtp.setVisibility(View.GONE);
                    inputPhone.setEnabled(false);

                    textMobileVerifiedMessage.setVisibility(View.VISIBLE);

                    signInWithCredential(phoneAuthCredential);
                    isOtpVerified = true;
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };

    // Get Email Credential
    private AuthCredential getEmailCredential(String phoneNo, String password) {
        String fullEmail = phoneNo + "@" + Constants.DOMAIN;
        Log.d("EMAIL_SIGINUP", "Your email is " + fullEmail);
        return EmailAuthProvider.getCredential(fullEmail, password);
    }

    // Checks if Password meets guidelines
    private boolean isValidPassword(String password) {
        if (password.isEmpty() || password.length() < 8) {
            return false;
        }
        return true;
    }


}

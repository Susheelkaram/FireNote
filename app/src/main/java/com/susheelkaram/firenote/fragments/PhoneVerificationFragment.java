package com.susheelkaram.firenote.fragments;


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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.susheelkaram.firenote.utils.Constants;
import com.susheelkaram.firenote.HomeActivity;
import com.susheelkaram.firenote.R;

import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneVerificationFragment extends Fragment {

    private EditText inputPhone;
    private EditText inputOtp;
    private EditText inputPassword;
    private EditText inputName;
    private Button buttonSignUp;
    private Button buttonVerifyOtp;
    private TextInputLayout inputOtpContainer;
    private TextView textMobileVerifiedMessage;

    private Context mContext;

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
        inputName = (EditText) view.findViewById(R.id.input_Name);
        inputOtp = (EditText) view.findViewById(R.id.input_Otp);
        inputOtpContainer = (TextInputLayout) view.findViewById(R.id.layout_InputOtpContainer);
        inputPassword = (EditText) view.findViewById(R.id.input_Password);
        buttonSignUp = (Button) view.findViewById(R.id.button_SignUp);
        buttonVerifyOtp = (Button) view.findViewById(R.id.button_VerifyOtp);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        buttonVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    // Final Sign up Method
    private void signUp() {
        String phone = inputPhone.getText().toString();

        if (isValidData()) {
            sendVerificationCode(phone);

            inputPhone.setEnabled(false);
            inputName.setEnabled(false);
            inputPassword.setEnabled(false);
        }
    }

    private void verifyOtp() {
        // If OTP is automatically verfied by Instant Verification
        if (isOtpVerified && mAuth.getCurrentUser() != null) {
            linkCredentials();
            return;
        }

        String userInputCode = inputOtp.getText().toString();

        // Manually verifying code
        if (!userInputCode.isEmpty()) {
            verifyCode(userInputCode);
        } else {
            inputOtp.setError("Invalid OTP!");
        }
    }

    // Links Phone with Email/Password
    private void linkCredentials() {
        String phone = mAuth.getCurrentUser().getPhoneNumber();
        String password = inputPassword.getText().toString().trim();
        String name = inputName.getText().toString();

        AuthCredential emailCredential = getEmailCredential(phone, password);
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.linkWithCredential(emailCredential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d("SIGN_UP", "Credential linking SUCCESSFUL");

                        FirebaseUser user = mAuth.getCurrentUser();
                        String name = inputName.getText().toString().trim();

                        // Setting Display Name
                        UserProfileChangeRequest nameChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();
                        user.updateProfile(nameChangeRequest);

                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else {
                        if(mAuth.getCurrentUser() != null){
                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
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
                            AuthCredential emailCredential = getEmailCredential(phone, password);
                            linkCredentials();
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

                    inputOtpContainer.setVisibility(View.GONE);
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

    // Checks if user entered Valid Phone, Name & Password
    private boolean isValidData() {
        String phone = inputPhone.getText().toString().trim();
        String name = inputName.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (phone.isEmpty() || name.isEmpty() || password.isEmpty()) {
            Toast.makeText(mContext, "Enter valid Phone, Name & Password", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (phone.length() != 10) {
            inputPhone.setError("Phone no. should be 10 digit number.");
            return false;
        }
        if (password.length() < 8) {
            inputPassword.setError("Password should be atleast 8 Characters long");
            return false;
        }
        return true;
    }
}

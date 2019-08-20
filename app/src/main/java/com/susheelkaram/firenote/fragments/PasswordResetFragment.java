package com.susheelkaram.firenote.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.LinearLayout;
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
import com.susheelkaram.firenote.HomeActivity;
import com.susheelkaram.firenote.R;
import com.susheelkaram.firenote.utils.Constants;

import java.util.concurrent.TimeUnit;

public class PasswordResetFragment extends Fragment {
    Context mContext;

    private EditText inputPhone;
    private EditText inputOtp;
    private EditText inputNewPassword;
    private EditText inputRetypeNewPassword;
    private TextInputLayout containerInputOtp;
    private LinearLayout containerNewPassword;
    private Button btnSendOtp;
    private Button btnVerifyOtp;
    private Button btnConfirmPassword;
    private TextView textMobileVerified;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String verificationId;
    private String COUNTRY_CODE = "+91";
    private boolean isOtpVerified = false;

    public PasswordResetFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_password_reset, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inputPhone = (EditText) view.findViewById(R.id.input_ResetPhone);
        inputOtp = (EditText) view.findViewById(R.id.input_ResetOtp);
        inputNewPassword = (EditText) view.findViewById(R.id.input_ResetNewPassword);
        inputRetypeNewPassword = (EditText) view.findViewById(R.id.input_ResetNewPasswordRetype);
        containerInputOtp =(TextInputLayout) view.findViewById(R.id.layout_ResetInputOtpContainer);
        containerNewPassword = (LinearLayout) view.findViewById(R.id.layout_NewPasswordContainer);
        btnSendOtp = (Button) view.findViewById(R.id.button_ResetSendOtp);
        btnVerifyOtp = (Button) view.findViewById(R.id.button_ResetVerifyOtp);
        btnConfirmPassword = (Button) view.findViewById(R.id.button_ConfirmPasswordReset);
        textMobileVerified = (TextView) view.findViewById(R.id.text_ResetMobileVerifiedMessage);

        btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPhoneValid()){
                    String phone = inputPhone.getText().toString().trim();
                    sendVerificationCode(phone);
                }
            }
        });

        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOtp();
            }
        });

        btnConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidPassword()){
                    confirmPassword();
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

//                    inputOtpContainer.setVisibility(View.GONE);

                    onOtpVerified();
                    signInWithCredential(phoneAuthCredential);
                    isOtpVerified = true;
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };

    // Sign in with OTP
    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            textMobileVerified.setVisibility(View.VISIBLE);
                            isOtpVerified = true;
                        } else {
                            Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    // Confirm Password change
    private void confirmPassword(){
        FirebaseUser user = mAuth.getCurrentUser();

        if (user.getEmail() != null) {
            updatePassword();
            return;
        }
        linkCredentials();
    }

    // Update password
    private void updatePassword() {
        String password = inputNewPassword.getText().toString().trim();

        FirebaseUser mUser = mAuth.getCurrentUser();

        mUser.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "Password Successfully Changed", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Create Password
    private void linkCredentials() {
        String phone = mAuth.getCurrentUser().getPhoneNumber();
        String password = inputNewPassword.getText().toString().trim();

        AuthCredential emailCredential = getEmailCredential(phone, password);
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.linkWithCredential(emailCredential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d("SIGN_UP", "Credential linking SUCCESSFUL");

                        FirebaseUser user = mAuth.getCurrentUser();

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

    // Get Email Credential
    private AuthCredential getEmailCredential(String phoneNo, String password) {
        String fullEmail = phoneNo + "@" + Constants.DOMAIN;
        Log.d("EMAIL_SIGINUP", "Your email is " + fullEmail);
        return EmailAuthProvider.getCredential(fullEmail, password);
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

    // Sends OTP
    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                COUNTRY_CODE + phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                phoneAuthCallbacks
        );
        containerInputOtp.setVisibility(View.VISIBLE);
        btnVerifyOtp.setVisibility(View.VISIBLE);
    }

    // Verifies OTP
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }


    // DATA VALIDATION
    // Phone
    private boolean isPhoneValid(){
        String phone = inputPhone.getText().toString().trim();
        if(phone.isEmpty() || phone.length() != 10){
            inputPhone.setError("Enter valid 10-digit Phone no.");
            return false;
        }
        return true;
    }

    private boolean isValidPassword(){
        String newPassword = inputNewPassword.getText().toString().trim();
        String newPasswordRetype = inputRetypeNewPassword.getText().toString().trim();

        if(newPassword.isEmpty() || newPassword.length() < 8){
            inputNewPassword.setError("Password should atleast 8 characters");
            return false;
        }
        if(!newPassword.equals(newPasswordRetype)){
            inputRetypeNewPassword.setError("Passwords doesn't match");
            return false;
        }
        return true;
    }

    private void onOtpVerified(){
        inputPhone.setEnabled(false);
        textMobileVerified.setVisibility(View.VISIBLE);
        containerNewPassword.setVisibility(View.VISIBLE);
        containerInputOtp.setVisibility(View.GONE);
        btnVerifyOtp.setVisibility(View.GONE);
    }
}

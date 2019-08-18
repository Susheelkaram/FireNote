package com.susheelkaram.firenote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.auth.User;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class MainActivity extends AppCompatActivity {
//    //    @BindView(R.id.input_PhoneNo)
//    EditText inputPhone;
//    //    @BindView(R.id.input_Otp)
//    EditText inputOtp;
//    EditText inputPassword;
//    //    @BindView(R.id.button_SendOtp)
//    Button buttonSendOtp;
//    //    @BindView(R.id.button_SignUp)
//    Button buttonSignUp;

    //    private boolean isOtpVerified = false;
//
//    private String verificationId;
//    private String COUNTRY_CODE = "+91";
    private FirebaseAuth mAuth;

    FrameLayout layoutLoginSignupContainer;
    Button btnLoginScreen;
    Button btnSignUpScreen;
    Button btnForgotPasswordScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        layoutLoginSignupContainer = (FrameLayout) findViewById(R.id.layout_LoginSignupContainer);
        btnSignUpScreen = (Button) findViewById(R.id.button_SignUpScreen);
        btnLoginScreen = (Button) findViewById(R.id.button_LoginScreen);
        btnForgotPasswordScreen = (Button) findViewById(R.id.button_ForgotPassword);


        PhoneVerificationFragment phoneVerificationFragment = new PhoneVerificationFragment();
        LoginFragment loginFragment = new LoginFragment();

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.layout_LoginSignupContainer, loginFragment)
                .commit();


        btnLoginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.layout_LoginSignupContainer, loginFragment)
                        .commit();
                btnLoginScreen.setVisibility(View.GONE);
                btnForgotPasswordScreen.setVisibility(View.VISIBLE);
                btnSignUpScreen.setVisibility(View.VISIBLE);
            }
        });
        btnSignUpScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.layout_LoginSignupContainer, phoneVerificationFragment)
                        .commit();
                btnLoginScreen.setVisibility(View.VISIBLE);
                btnForgotPasswordScreen.setVisibility(View.GONE);
                btnSignUpScreen.setVisibility(View.GONE);
            }
        });


//        ButterKnife.bind(MainActivity.this);

//        inputPhone = (EditText) findViewById(R.id.input_PhoneNo);
//        inputOtp = (EditText) findViewById(R.id.input_Otp);
//        inputPassword = (EditText) findViewById(R.id.input_Password);
//        buttonSignUp = (Button) findViewById(R.id.button_SignUp);
//        buttonSendOtp = (Button) findViewById(R.id.button_SendOtp);
//

//        buttonSendOtp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String phoneNo = inputPhone.getText().toString().trim();
//                if (phoneNo != null && phoneNo.length() == 10) {
//                    sendVerificationCode(phoneNo);
//                } else {
//                    inputPhone.setError("Invalid Phone number");
//                    inputPhone.requestFocus();
//                }
//            }
//        });
//
//        buttonSignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signUp();
//            }
//        });

    }

//    // Final Sign up Method
//    private void signUp() {
//        String userInputCode = inputOtp.getText().toString().trim();
//
//        if (isOtpVerified && mAuth.getCurrentUser() != null) {
//            linkCredentials();
//            return;
//        }
//        if (!userInputCode.isEmpty()) {
//            verifyCode(userInputCode);
//        } else {
//            inputOtp.setError("Invalid OTP!");
//        }
//    }
//
//    private void linkCredentials() {
//        String phone = mAuth.getCurrentUser().getPhoneNumber();
//        String password = inputPassword.getText().toString();
//        if(!isValidPassword(password)){
//            inputPassword.setError("Password should be atleast 8 Characters");
//            return;
//        }
//        AuthCredential emailCredential = getEmailCredential(phone, password);
//        FirebaseUser user = mAuth.getCurrentUser();
//
//        if (user != null) {
//            user.linkWithCredential(emailCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                @Override
//                public void onComplete(@NonNull Task<AuthResult> task) {
//                    if (task.isSuccessful()) {
//                        Log.d("SIGN_UP", "Credential linking SUCCESSFUL");
//
//                        String phone = mAuth.getCurrentUser().getPhoneNumber();
//                        String email = mAuth.getCurrentUser().getEmail();
//
//                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//                        intent.putExtra(Constants.EXTRA_PHONE, phone);
//                        intent.putExtra(Constants.EXTRA_EMAIL, email);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                    } else {
//                        Log.d("SIGN_UP", "Credential linking FAILED");
//                    }
//                }
//            });
//        }
//    }
//
//    // Sends OTP
//    private void sendVerificationCode(String phoneNumber) {
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                COUNTRY_CODE + phoneNumber,
//                60,
//                TimeUnit.SECONDS,
//                TaskExecutors.MAIN_THREAD,
//                phoneAuthCallbacks
//        );
//    }
//
//    // Verifies OTP
//    private void verifyCode(String code) {
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
//        signInWithCredential(credential);
//    }
//
//    // Sign in with OTP
//    private void signInWithCredential(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            String phone = mAuth.getCurrentUser().getPhoneNumber();
//                            String password = inputPassword.getText().toString();
//                            if (isValidPassword(password)) {
//                                AuthCredential emailCredential = getEmailCredential(phone, password);
//                                linkCredentials();
//                            }
//                        } else {
//                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//
//    // PhoneAuth Callbacks
//    private PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneAuthCallbacks =
//            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                @Override
//                public void onCodeSent(@NonNull String verifId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                    super.onCodeSent(verifId, forceResendingToken);
//                    verificationId = verifId;
//                }
//
//                @Override
//                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                    inputOtp.setText(phoneAuthCredential.getSmsCode());
//                    inputOtp.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_circle_black_24dp, 0);
//                    signInWithCredential(phoneAuthCredential);
//                    isOtpVerified = true;
//                }
//
//                @Override
//                public void onVerificationFailed(@NonNull FirebaseException e) {
//                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            };
//
//    // Get Email Credential
//    private AuthCredential getEmailCredential(String phoneNo, String password) {
//        String fullEmail = COUNTRY_CODE + phoneNo + "@" + Constants.DOMAIN;
//        Log.d("EMAIL_SIGINUP", "Your email is " + fullEmail);
//        return EmailAuthProvider.getCredential(fullEmail, password);
//    }
//
//    // Checks if Password meets guidelines
//    private boolean isValidPassword(String password) {
//        if (password.isEmpty() || password.length() < 8) {
//            return false;
//        }
//        return true;
//    }

    @Override
    protected void onStart() {
        super.onStart();

        // Checking if User has already logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String id = currentUser.getPhoneNumber();

            Intent i = new Intent(this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    }
}

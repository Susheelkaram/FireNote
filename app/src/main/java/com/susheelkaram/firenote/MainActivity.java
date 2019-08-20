package com.susheelkaram.firenote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.susheelkaram.firenote.fragments.LoginFragment;
import com.susheelkaram.firenote.fragments.PasswordResetFragment;
import com.susheelkaram.firenote.fragments.PhoneVerificationFragment;

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
        PasswordResetFragment passwordResetFragment = new PasswordResetFragment();

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.layout_LoginSignupContainer, loginFragment)
                .commit();

        // Launches Login form (Fragment)
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

        // Launches Signup form (Fragment)
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

        btnForgotPasswordScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.layout_LoginSignupContainer, passwordResetFragment)
                        .commit();
                btnLoginScreen.setVisibility(View.VISIBLE);
                btnForgotPasswordScreen.setVisibility(View.GONE);
                btnSignUpScreen.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Checking if User has already logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            String id = currentUser.getPhoneNumber();

            Intent i = new Intent(this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    }
}

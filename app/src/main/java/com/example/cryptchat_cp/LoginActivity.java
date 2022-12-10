package com.example.cryptchat_cp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kusu.loadingbutton.LoadingButton;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private EditText et_PhoneNumber, et_Code, et_Iso;
    private Button btnSend;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks Callbacks;
    private String VerficationId;

    public EditText otpView1, otpView2, otpView3, otpView4, otpView5, otpView6;
    public static int i = 0;
    String otpViewFinal;
    private static final String FORMAT = "%02d";
    CountDownTimer countDownTimer;
    TextView timerT;
    AlertDialog alertDialog;
    char[] otps = new char[6];

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        getSupportActionBar().hide();

        FirebaseApp.initializeApp(this);
        userIsLoggedIn();
        getPermissions();
        et_PhoneNumber = findViewById(R.id.phoneNumber);
//        mCode = findViewById(R.id.code);
        btnSend = findViewById(R.id.send);
        et_Iso = findViewById(R.id.iso);
        otpView1 = findViewById(R.id.et_1);
        otpView2 = findViewById(R.id.et_2);
        otpView3 = findViewById(R.id.et_3);
        otpView4 = findViewById(R.id.et_4);
        otpView5 = findViewById(R.id.et_5);
        otpView6 = findViewById(R.id.et_6);
        timerT = findViewById(R.id.timer);

        LoadingButton loadingButton = (LoadingButton) findViewById(R.id.send);
        et_listners();
        et_PhoneNumber.requestFocus();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(VerficationId != null){
                    verifyPhoneNumberWithCode();
                }else {
                    startPhoneNumberVerification();
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    ViewGroup viewGroup = findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.customview, viewGroup, false);
                    builder.setView(dialogView);
                    alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        Callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                    alertDialog.cancel();
                    timerT.setText("Verfication Failed");
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                VerficationId = verificationId;
                loadingButton.showLoading();
                btnSend.setEnabled(false);
                countTimer();
                alertDialog.cancel();
                otpView1.requestFocus();

            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential){
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user != null){
                        final DatabaseReference refUserDB = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());
                        refUserDB.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(!snapshot.exists()){
                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("phone",user.getPhoneNumber());
                                    userMap.put("name",user.getPhoneNumber());
                                    refUserDB.updateChildren(userMap);

                                }
                                userIsLoggedIn();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                timerT.setText("Task failed");
                            }
                        });
                    }
                }
            }
        });
    }
    private void verifyPhoneNumberWithCode(){
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerficationId, otpViewFinal);
                signInWithPhoneAuthCredential(credential);
    }

    private void userIsLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
           Intent i = new Intent(getApplicationContext(),bioVerification.class);
           startActivity(i);
           finish();
            return;
        }
    }

    private void startPhoneNumberVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
               et_Iso.getText().toString()+ et_PhoneNumber.getText().toString(),
                60,
                TimeUnit.SECONDS,
                this,
                Callbacks);
    }
    private void et_listners(){
        et_PhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                et_PhoneNumber.setLetterSpacing((float) 0.9);
                if(et_PhoneNumber.length() <= 9){
                    btnSend.setEnabled(false);
                }else{
                    btnSend.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() == 0)
                    et_PhoneNumber.setLetterSpacing((float) 0.0);
            }
        });
        otpView1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                otpView2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otpView2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                otpViewFinal +=otpView2.getText().toString();
                otpView3.requestFocus();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otpView3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                otpViewFinal +=otpView3.getText().toString();
                otpView4.requestFocus();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otpView4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                otpViewFinal +=otpView4.getText().toString();
                otpView5.requestFocus();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otpView5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                otpViewFinal +=otpView5.getText().toString();
                otpView6.requestFocus();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        otpView6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                otpViewFinal = otpView1.getText().toString()+otpView2.getText().toString()+otpView3.getText().toString()+otpView4.getText().toString()+otpView5.getText().toString()+otpView6.getText().toString();
                if(otpViewFinal.length() == 6){
                    verifyPhoneNumberWithCode();
                    countDownTimer.cancel();
                    timerT.setText("Verifying Otp");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
    public void countTimer(){
        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerT.setText(""+String.format(FORMAT, TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }
            public void onFinish() {
                timerT.setText("Time Out");
            }
        }.start();
    }
    private void getPermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
        }
    }
}
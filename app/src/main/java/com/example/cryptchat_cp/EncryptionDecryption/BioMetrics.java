package com.example.cryptchat_cp.EncryptionDecryption;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.example.cryptchat_cp.MainPageActivity;
import com.example.cryptchat_cp.Objects.MessageObject;
import com.example.cryptchat_cp.bioVerification;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class BioMetrics {
    ArrayList<MessageObject> messageList;
    int pos;
    private CancellationSignal cancellationSignal = null;
    FragmentManager applicationContext;
    Context context;
    Executor mainExecutor;
    KeyguardManager keyguardManager;
    PackageManager packageManager;
    ImageView bio;
    ImageView door;
    public BioMetrics(ArrayList<MessageObject> messageList, FragmentManager applicationContext, Context context, Executor mainExecutor, KeyguardManager keyguardManager, PackageManager packageManager, int pos) {
        this.messageList = messageList;
        this.applicationContext = applicationContext;
        this.context = context;
        this.mainExecutor = mainExecutor;
        this.keyguardManager = keyguardManager;
        this.packageManager = packageManager;
        this.pos = pos;
    }
    public BioMetrics(Context context, Executor mainExecutor, KeyguardManager keyguardManager, PackageManager packageManager, ImageView bio, ImageView door) {
        this.context = context;
        this.mainExecutor = mainExecutor;
        this.keyguardManager = keyguardManager;
        this.packageManager = packageManager;
        this.bio = bio;
        this.door = door;
    }
    String decrypted;
    public BiometricPrompt.AuthenticationCallback authenticationCallback;

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void decrypt() {
        authenticationCallback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(
                    int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                notifyUser("Authentication Error : " + errString);
            }
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                notifyUser("Message Decrypted");
                switch (messageList.get(pos).getAlgoNumber()){
                    case "1":
                        try {
                            decrypted = new ThreeDes(messageList.get(pos).getMessageId()).decryptA(messageList.get(pos).getEncData());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case "2":
                        decrypted = new AES(messageList.get(pos).getMessageId()).decrypt(messageList.get(pos).getEncData());
                        break;
                    case "3":
                        decrypted = new BlowFish(messageList.get(pos).getMessageId()).decryptA(messageList.get(pos).getEncData());
                        break;
                    case "4":
                        decrypted = new RSA(messageList.get(pos).getMessageId()).decryptA(messageList.get(pos).getEncData());
                        break;
                }
                DescDataBox obj = new DescDataBox(decrypted, messageList.get(pos).getMessage());
                obj.show(applicationContext, "");
            }
        };
        BiometricPrompt biometricPrompt = new BiometricPrompt
                .Builder(context)
                .setTitle("Decrypt Your Message")
                .setSubtitle("CryptChat - Secure Messenger")
                .setDescription("Touch your finger on sensor to login")
                .setNegativeButton("Cancel", mainExecutor, new DialogInterface.OnClickListener() {
                    @Override
                    public void
                    onClick(DialogInterface dialogInterface, int i) {
                        notifyUser("Authentication Cancelled");
                    }
                }).build();

        biometricPrompt.authenticate(
                getCancellationSignal(),
                mainExecutor,
                authenticationCallback);
        checkBiometricSupport();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void unlock() {
        authenticationCallback = new BiometricPrompt.AuthenticationCallback() {

            @Override
            public void onAuthenticationError(
                    int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                notifyUser("Authentication Error : " + errString);
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                bio.setVisibility(View.GONE);
                door.setVisibility(View.VISIBLE);
                notifyUser("Identity Verified");
                Intent i =new Intent(context,MainPageActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        };
        BiometricPrompt biometricPrompt = new BiometricPrompt
                .Builder(context)
                .setTitle("Login to CryptChat")
                .setSubtitle("Verify Identity")
                .setDescription("Touch your finger on sensor to login")
                .setNegativeButton("Cancel", mainExecutor, new DialogInterface.OnClickListener() {
                    @Override
                    public void
                    onClick(DialogInterface dialogInterface, int i) {
                        notifyUser("Authentication Cancelled");
                        Intent _i =new Intent(context,bioVerification.class);
                        _i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(_i);
                    }
                }).build();

        biometricPrompt.authenticate(
                getCancellationSignal(),
                mainExecutor,
                authenticationCallback);
        checkBiometricSupport();
    }
    private CancellationSignal getCancellationSignal() {
        cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(
                new CancellationSignal.OnCancelListener() {
                    @RequiresApi(api = Build.VERSION_CODES.P)
                    @Override
                    public void onCancel() {
                        notifyUser("Authentication was Cancelled by the user");
                        Intent i =new Intent(context,bioVerification.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }
                });
        return cancellationSignal;
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private Boolean checkBiometricSupport() {
        if (!keyguardManager.isDeviceSecure()) {
            notifyUser("Fingerprint authentication has not been enabled in settings");
            return false;
        }
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            notifyUser("Fingerprint Authentication Permission is not enabled");
            return false;
        }
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            return true;
        } else
            return true;
    }
    private void notifyUser(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}

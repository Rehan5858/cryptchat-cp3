package com.example.cryptchat_cp;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.cryptchat_cp.EncryptionDecryption.BioMetrics;

public class bioVerification extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio_verification);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        getSupportActionBar().hide();
        ImageView bio = findViewById(R.id.unlockL);
        ImageView door = findViewById(R.id.door);
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        BioMetrics bioObj = new BioMetrics(getApplicationContext(), getMainExecutor(), keyguardManager, getPackageManager(),bio,door);
        bioObj.unlock();
    }
}
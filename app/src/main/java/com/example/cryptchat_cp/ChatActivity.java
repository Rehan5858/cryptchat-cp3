package com.example.cryptchat_cp;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.biometrics.BiometricPrompt;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptchat_cp.EncryptionDecryption.BioMetrics;
import com.example.cryptchat_cp.EncryptionDecryption.DialogJavaInterface;
import com.example.cryptchat_cp.ExtClassFiles.SendNotification;
import com.example.cryptchat_cp.Objects.MessageObject;
import com.example.cryptchat_cp.RecAdapters.MessageAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements MessageAdapter.encDataInterface, DialogJavaInterface.DialogListner, MessageAdapter.bioMetrics {
    private RecyclerView rvChat;
    private RecyclerView.Adapter rvChatAdapter;
    private RecyclerView.LayoutManager rvChatLayoutManager;
    private CancellationSignal cancellationSignal = null;

    private BiometricPrompt.AuthenticationCallback authenticationCallback;

    ArrayList<MessageObject> messageList, messageListClone;
    String chatID, uName, uNotificationKey;
   public  Uri imgUri ;
    DatabaseReference mChatDB;
    ImageView colorpicker, colorFilter, cancelFilter, algoSelector;
    EditText mMessage;
    int t;
    int[] colorsArray;
    TextView filterView;
    String decrypted;
    View emptyView;
    View incldView;
    Button retryBtn;
    ShimmerFrameLayout shimmerFrameLayout;
    public MessageObject mMessageObj = null;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1 && requestCode == RESULT_OK && data != null && data.getData() != null){
            imgUri = data.getData();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        getSupportActionBar().hide();


        getSupportActionBar().hide();

        emptyView = findViewById(R.id.emptyViewChat);
//        emptyViewText = findViewById(R.id.emptyTextView);
        incldView = findViewById(R.id.incld_chat);
        retryBtn = incldView.findViewById(R.id.retryBtn);

        chatID = getIntent().getExtras().getString("chatID");
        uName = getIntent().getExtras().getString("uName");
        uNotificationKey = getIntent().getExtras().getString("uNotificationKey");

        shimmerFrameLayout = findViewById(R.id.shimmerChatAct);
        shimmerFrameLayout.startShimmer();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        checkInternetConnection();

        TextView uNameView = findViewById(R.id.uName);
        TextView uNameAvatarView = findViewById(R.id.uNameAvatar);
        filterView = findViewById(R.id.filterClick);
        uNameView.setText(uName);
        String[] uNameArr = uName.split("\\s");
        String avatarOne = String.valueOf(uNameArr[0].charAt(0));
        String avatarTwo;
        if (uNameArr.length > 1) {
            avatarTwo = String.valueOf(uNameArr[1].charAt(0));
        } else {
            avatarTwo = String.valueOf(uNameArr[0].charAt(uNameArr[0].length() - 1));
        }
        uNameAvatarView.setText(avatarOne.toUpperCase() + avatarTwo.toUpperCase());



        mChatDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID);

        filterView = findViewById(R.id.filterClick);
        filterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterMessage();
            }
        });
        mChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                incldView.setVisibility(View.GONE);
                if (!snapshot.getKey().equals(null)) {
                    messageList.clear();
                    for (DataSnapshot msgIdShot : snapshot.getChildren()) {
                        mChatDB.child(msgIdShot.getKey()).addValueEventListener(new ValueEventListener() {
                            String text = "", creatorID = "", colorPickerCode = "", encData = "", imgUri = "", algoNumber = "";
                            boolean exits = false;

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                if (snapshot.child("encData").getValue().toString().equals(null)) {

                            if (snapshot.child("text").getValue() != null) {
                                text = snapshot.child("text").getValue().toString();
                            }
                            if (snapshot.child("creator").getValue() != null) {
                                creatorID = snapshot.child("creator").getValue().toString();
                            }
                            if (snapshot.child("colorPicker").getValue() != null) {
                                colorPickerCode = snapshot.child("colorPicker").getValue().toString();
                            }
                            if (snapshot.child("encData").getValue() != null) {
                                encData = snapshot.child("encData").getValue().toString();
                            }
                                if (snapshot.child("algoNumber").getValue() != null) {
                                    algoNumber = snapshot.child("algoNumber").getValue().toString();
                                }



                                    mMessageObj = new MessageObject(snapshot.getKey(), creatorID, text, uName, colorPickerCode, encData, imgUri, algoNumber);
                                    shimmerFrameLayout.stopShimmer();
                                    messageList.add(mMessageObj);
                                    shimmerFrameLayout.setVisibility(View.GONE);
                                    rvChatLayoutManager.scrollToPosition(messageList.size() - 1);
                                }
//                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
//                        mChatAdapter.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        ImageView mSend = findViewById(R.id.send);
        colorpicker = findViewById(R.id.colorpicker);
        colorFilter = findViewById(R.id.colorFilter);
        reactionView(colorpicker);
        reactionView(colorFilter);

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        initializeRecyclerView();
//        getChatMessages();
        cancelFilter = findViewById(R.id.cancelFilter);
        cancelFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(getIntent());
            }
        });
    }

    private void checkInternetConnection() {
        if(!haveNetworkConnection()){
            incldView.setVisibility(View.VISIBLE);
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            retryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
        }else{
            incldView.setVisibility(View.GONE);
        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected()) haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
    private void filterMessage(){
        for (Iterator<MessageObject> iterator = messageList.iterator(); iterator.hasNext(); ) {
            MessageObject value = iterator.next();
            if(!value.getColorPickerCode().equals(String.valueOf(t))) {
                iterator.remove();
            }
        }
        rvChatAdapter.notifyDataSetChanged();
    }
    private void sendMessage() {
        mMessage = (EditText) findViewById(R.id.et_message);
        if (!(mMessage.getText().toString().isEmpty())) {
            DatabaseReference newMessageDb = mChatDB.push();
            Map newMessageMap = new HashMap();
            newMessageMap.put("text", mMessage.getText().toString());
            newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());
            newMessageMap.put("colorPicker", t);
            newMessageMap.put("encData", "");
            new SendNotification(mMessage.getText().toString(),"New data request from "+FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),uNotificationKey);
            newMessageDb.updateChildren(newMessageMap);
            rvChatLayoutManager.scrollToPosition(messageList.size() - 1);
        }
//        messageList.clear();
//        mChatAdapter.notifyDataSetChanged();
        mMessage.setText(null);
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void initializeRecyclerView() {
        messageList = new ArrayList<>();
        rvChat = findViewById(R.id.messageList);
        rvChat.setNestedScrollingEnabled(false);
        rvChat.setHasFixedSize(false);
        rvChatLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        rvChat.setLayoutManager(rvChatLayoutManager);
        rvChatAdapter = new MessageAdapter(messageList, this, this);
        rvChat.setAdapter(rvChatAdapter);
    }

    @Override
    public void getData(int pos) {
        openDialog(pos, messageList, mChatDB, rvChatAdapter);
    }

    @Override
    public void longPress(int pos) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete  ");
        alert.setIcon(R.drawable.ic_baseline_delete_24);
        alert.setMessage("Are you sure you want to delete this?");
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID);
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            if(ds.getKey().equals(messageList.get(pos).getMessageId())){
                                db.child(messageList.get(pos).getMessageId()).setValue(null);
                                rvChatAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
                dialog.cancel();
            }
        });
        alert.show();


    }

    public void openDialog(int pos, ArrayList<MessageObject> messageList, DatabaseReference mChatDB, RecyclerView.Adapter mChatAdapter) {
        DialogJavaInterface obj = new DialogJavaInterface(pos, messageList, mChatDB, mChatAdapter,uNotificationKey,uName);
        obj.show(getSupportFragmentManager(), "");
    }

    @Override
    public void applData(int pos) {
//        mChatAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)


    @Override
    public void decryptData(int pos) {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        BioMetrics obj = new BioMetrics(messageList,getSupportFragmentManager(),getApplicationContext(),getMainExecutor(),keyguardManager,getPackageManager(),pos);
        obj.decrypt();
    }



    private void reactionView(ImageView colorpicker){
         colorsArray = new int[]{
                R.drawable.color_picker_blue,
                R.drawable.color_picker_green,
                R.drawable.color_picker_orange,
                R.drawable.color_picker_yellow
        };
        try {
            ReactionsConfig config = new ReactionsConfigBuilder(getApplicationContext())
                    .withReactions(colorsArray)
                    .withReactionTexts(R.array.colorNames)
                    .withPopupColor(Color.GRAY)
                    .build();

            ReactionPopup popup = new ReactionPopup(getApplicationContext(), config, (position) -> {
                t = position;
                colorpicker.setImageResource(colorsArray[position]);

                return true;
            });
            colorpicker.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view, motionEvent);
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
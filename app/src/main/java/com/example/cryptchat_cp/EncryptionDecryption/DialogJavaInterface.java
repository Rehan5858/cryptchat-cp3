package com.example.cryptchat_cp.EncryptionDecryption;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptchat_cp.ExtClassFiles.SendNotification;
import com.example.cryptchat_cp.Objects.MessageObject;
import com.example.cryptchat_cp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
public class DialogJavaInterface extends AppCompatDialogFragment{
    private EditText enc_dataView;
    private DialogListner listner;
    int pos;
    public Uri imgview;
    String encrypted, notificationKey, uName, algoNumber = null;
    DatabaseReference mChatDB;
    Button rsa,threeDes,blowfish,aes;
    ImageView addPhotoview,slctdPhoto;
    ArrayList<MessageObject> messageList;
    RecyclerView.Adapter mChatAdapter;
    FirebaseStorage storage;
    StorageReference storageReference;
    Button algoSelector;
    MaterialButtonToggleGroup algoGroup;
    int whichOne;
    public DialogJavaInterface(int pos , ArrayList<MessageObject> messageList, DatabaseReference mChatDB, RecyclerView.Adapter mChatAdapter, String notificationKey, String uName){
        this.pos = pos;
        this.messageList = messageList;
        this.mChatDB = mChatDB;
        this.mChatAdapter = mChatAdapter;
        this.notificationKey = notificationKey;
        this.uName = uName;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listner = (DialogListner) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "Dialog listner");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.popup);

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog_interface, null);
        enc_dataView = view.findViewById(R.id.et_encData);
        addPhotoview = view.findViewById(R.id.addPhotoView);
        slctdPhoto = view.findViewById(R.id.slctd_PHoto);
        rsa = view.findViewById(R.id.rsaBtn);
        threeDes = view.findViewById(R.id.threeDesBtn);
        aes = view.findViewById(R.id.aesBtn);
        blowfish = view.findViewById(R.id.blowfishBtn);
        algoGroup = view.findViewById(R.id.algoGroup);
        ActivityResultLauncher<Intent> someActivityResultLaunchncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){

                            imgview = result.getData().getData();

                            Log.e("Picture Path", String.valueOf(imgview));
                            slctdPhoto.setVisibility(View.VISIBLE);
                            encrypted = " ";
                            slctdPhoto.setImageURI(imgview);
                        }
                    }
                }
        );

        algoGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if(!enc_dataView.getText().toString().isEmpty()) {
                    switch (checkedId) {
                        case R.id.threeDesBtn:
                                algoNumber = "1";
                            break;
                        case R.id.aesBtn:
                            algoNumber = "2";
                            break;
                        case R.id.blowfishBtn:
                            algoNumber = "3";
                            break;
                        case R.id.rsaBtn:
                            algoNumber = "4";
                            break;
                    }
                }else{
                    Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Type text to encrypt data", Snackbar.LENGTH_LONG);
                    snackBar.setBackgroundTint(Color.parseColor("#FF0000"));
                    snackBar.setTextColor(Color.WHITE);
                    snackBar.setAction("Close", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackBar.dismiss();
                        }
                    });
                    snackBar.setActionTextColor(Color.WHITE);
                    snackBar.show();
                }
                }

        });
        builder.setView(view).setTitle("Add Data")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Add Data", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!algoNumber.isEmpty()){
                            switch (algoNumber) {
                                case "1":
                                    try {
                                        encrypted = new ThreeDes(messageList.get(pos).getMessageId()).encryptA(enc_dataView.getText().toString());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case "2":
                                    encrypted = new AES(messageList.get(pos).getMessageId()).encrypt(enc_dataView.getText().toString());
                                    break;
                                case "3":
                                    encrypted = new BlowFish(messageList.get(pos).getMessageId()).encryptA(enc_dataView.getText().toString());
                                    break;
                                case "4":
                                    encrypted = new RSA(messageList.get(pos).getMessageId()).encryptA(enc_dataView.getText().toString());
                                    break;
                            }
                        if (!encrypted.equals(null)) {
                            new SendNotification("Requested Data: " + messageList.get(pos).getMessage(), "Encrypted data attached by " + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), notificationKey);
                            mChatDB.child(messageList.get(pos).getMessageId()).child("encData").setValue(encrypted);
                            mChatDB.child(messageList.get(pos).getMessageId()).child("algoNumber").setValue(algoNumber);
                        } else if (!encrypted.equals(null) && !imgview.equals(null)) {
                            new SendNotification("Requested Data: " + messageList.get(pos).getMessage(), "Encrypted data attached by " + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), notificationKey);
                            mChatDB.child(messageList.get(pos).getMessageId()).child("encData").setValue(encrypted);
                            mChatDB.child(messageList.get(pos).getMessageId()).child("algoNumber").setValue(algoNumber);
//                            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
//                            ref.putFile(imgview);
//                            mChatDB.child(messageList.get(pos).getMessageId()).child("uri").setValue(imgview);

                        }
                        listner.applData(pos);
                            addPhotoview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), 1);
                                    Intent i = new Intent();
                                    i.setType("image/*");
                                    i.setAction(Intent.ACTION_GET_CONTENT);
                                    someActivityResultLaunchncher.launch(i);
                                }
                            });
                    }else{
                                Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Select algorithm to encrypt data", Snackbar.LENGTH_LONG);
                                snackBar.setBackgroundTint(Color.parseColor("#FF0000"));
                                snackBar.setTextColor(Color.WHITE);
                                snackBar.setAction("Close", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                    snackBar.dismiss();
                                    }
                                    });
                                    snackBar.setActionTextColor(Color.WHITE);
                                    snackBar.show();
                        }
                        getActivity().finish();
                        startActivity(getActivity().getIntent());
                    }
                });





        return builder.create();
    }



    public interface DialogListner{
        void applData(int pos);
    }

}
//    Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Atleast select one user", Snackbar.LENGTH_LONG);
//                        snackBar.setBackgroundTint(Color.parseColor("#FF0000"));
//                                snackBar.setTextColor(Color.WHITE);
//                                snackBar.setAction("Close", new View.OnClickListener() {
//@Override
//public void onClick(View view) {
//        snackBar.dismiss();
//        }
//        });
//        snackBar.setActionTextColor(Color.WHITE);
//        snackBar.show();
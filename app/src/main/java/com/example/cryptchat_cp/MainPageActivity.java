package com.example.cryptchat_cp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptchat_cp.ExtClassFiles.CountryToPhonePrefix;
import com.example.cryptchat_cp.ExtClassFiles.DeviceInfoBox;
import com.example.cryptchat_cp.Objects.ChatObject;
import com.example.cryptchat_cp.RecAdapters.ChatListAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.onesignal.OneSignal;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.P)
public class MainPageActivity extends AppCompatActivity implements ChatListAdapter.chatListInterface {

    private RecyclerView rvChatList;
    private RecyclerView.Adapter rvChatListAdapter;
    private RecyclerView.LayoutManager rvChatListLayoutManager;
    ArrayList<ChatObject> chatList, chatList2, contactList;
    View emptyView;
    TextView emptyViewText;
    ShimmerFrameLayout shimmerFrameLayout;
    public ChatObject obj = null;
    LinearLayout interntConnectionLayout;
    boolean exits = false;
    private static final String ONESIGNAL_APP_ID = "6ca545f1-8aa9-4b3f-86c7-4fc64be3f40c";
    View incldView;
    ImageView imgView;
    Button retryBtn, closeBtn;
    boolean isConnected = true;
    String currentUserUid = FirebaseAuth.getInstance().getUid();

    SharedPreferences shref;
    SharedPreferences.Editor editor;
    Gson gson = new Gson();


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        getSupportActionBar().hide();

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);


        shref = getApplicationContext().getSharedPreferences("offlineData", Context.MODE_PRIVATE);
        editor = shref.edit();


        emptyView = findViewById(R.id.emptyView);
        emptyViewText = findViewById(R.id.emptyTextView);
        interntConnectionLayout = findViewById(R.id.interntConnectionLayout);
        ImageView iv_Logout = findViewById(R.id.logout);
        shimmerFrameLayout = findViewById(R.id.shimmerFacebook);
        shimmerFrameLayout.startShimmer();
        incldView = findViewById(R.id.icld_internet);
        imgView = findViewById(R.id.imageView);
        retryBtn = incldView.findViewById(R.id.retryBtn);
        closeBtn = incldView.findViewById(R.id.closeBtn);
        FloatingActionButton fabFindUser = findViewById(R.id.findUser);


        FirebaseDatabase.getInstance().getReference().child("user").child(currentUserUid).child("notificationKey").setValue(OneSignal.getDeviceState().getUserId());
        initializeRecyclerView();

        checkInternetConnection();


        fabFindUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), FindUserActivity.class));
            }
        });

        iv_Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OneSignal.disablePush(true);
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                return;
            }
        });
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TelephonyManager tManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
                getSupportActionBar().hide();
                DeviceInfoBox obj = new DeviceInfoBox("Device Name:   " + Build.DEVICE + "\n\n" + "Build Number:   " + Build.MODEL + "\n\n" + "Network Operator:   " + tManager.getNetworkOperatorName() + "\n\n" + "Manufacturer:   " + Build.MANUFACTURER + "\n\n" + "Mobile Number:   " + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                obj.show(getSupportFragmentManager(), "");
            }
        });

        getPermissions();
        getContactList();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("user").child(currentUserUid).child("chat");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                if (snapshot.exists()) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    incldView.setVisibility(View.GONE);
                    for (DataSnapshot parentI : snapshot.getChildren()) {
                        String s = parentI.getKey();
                        db.child(s).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String phone = snapshot.child("mob").getValue().toString();
                                String uNotificationKey = snapshot.child("uNotificationKey").getValue().toString();
                                obj = new ChatObject(s, phone, phone, uNotificationKey);
                                if (obj.getPhone().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                                    exits = true;
                                }
                                for (ChatObject itr : contactList) {
                                    if (itr.getPhone().equals(obj.getPhone())) {
                                        obj.setName(itr.getName());
                                    }
                                }

                                if (!exits) {
                                    chatList.add(obj);
//                                    Gson gson = new Gson();
//                                    String json = gson.toJson(chatList);
//                                    Log.d("LOggg",json);
//                                    editor.putString("chatListPref", json);
//                                    editor.commit();
                                    rvChatListAdapter.notifyDataSetChanged();
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                } else {
                    if (chatList.size() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                        emptyViewText.setVisibility(View.VISIBLE);
                        shimmerFrameLayout.setVisibility(View.GONE);
                        rvChatList.setVisibility(View.GONE);
                    } else {
                        rvChatList.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                        emptyViewText.setVisibility(View.GONE);
                    }
                    rvChatListAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        checkInternetConnection();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkInternetConnection();
//        Intent i = new Intent(getApplicationContext(),bioVerification.class);
//        startActivity(i);
//        finish();
    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//        checkInternetConnection();
//    }

    public void getContactList(){
        String ISOPrefix = getCountryISO();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        while(phones.moveToNext()) {
            @SuppressLint("Range") String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            @SuppressLint("Range") String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phone = phone.replace(" ", "");
            phone = phone.replace("-", "");
            phone = phone.replace("(", "");
            phone = phone.replace(")", "");

            if(!String.valueOf(phone.charAt(0)).equals("+"))
                phone = ISOPrefix + phone;

            ChatObject Obj = new ChatObject("",name,phone,"");

            contactList.add(Obj);
        }
    }
    private void getPermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
        }
    }
    private String getCountryISO(){
        String iso = null;
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if(telephonyManager.getNetworkCountryIso()!=null)
            if (!telephonyManager.getNetworkCountryIso().toString().equals(""))
                iso = telephonyManager.getNetworkCountryIso().toString();
        return CountryToPhonePrefix.getPhone(iso);
    }
    private void initializeRecyclerView() {
        chatList = new ArrayList<>();
        chatList2 = new ArrayList<>();
        contactList = new ArrayList<>();
        rvChatList = findViewById(R.id.chatList);
        rvChatList.setNestedScrollingEnabled(false);
        rvChatList.setHasFixedSize(false);
        rvChatListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false);
        rvChatList.setLayoutManager(rvChatListLayoutManager);
        rvChatListAdapter = new ChatListAdapter(chatList,this);
        rvChatList.setAdapter(rvChatListAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String json = gson.toJson(chatList);
        Log.d("LOggg",json);
        editor.putString("chatListPref", json);
        editor.commit();


    }

    private void checkInternetConnection() {
        if(!haveNetworkConnection()){

//            incldView.setVisibility(View.VISIBLE);
            shimmerFrameLayout.stopShimmer();
            rvChatList.setVisibility(View.GONE);
            shimmerFrameLayout.setVisibility(View.GONE);

            retryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    incldView.setVisibility(View.GONE);
//                    String response = shref.getString("chatListPref","");
//                    Type type = new TypeToken<ArrayList<ChatObject>>(){}.getType();
//                    chatList = gson.fromJson(response, type);
//                    Log.d("sizeoflist",response);
                    rvChatList.setVisibility(View.VISIBLE);
                    rvChatListAdapter.notifyDataSetChanged();
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
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    public void getChatListPos(int pos) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete  ");
        alert.setIcon(R.drawable.ic_baseline_delete_24);
        alert.setMessage("Are you sure you want to delete this?");
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), chatList.get(pos).getChatId(), Toast.LENGTH_SHORT).show();
                DatabaseReference userOne  = FirebaseDatabase.getInstance().getReference().child(currentUserUid).child("chat");
                userOne.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot cu : snapshot.getChildren()){
                            if(cu.getKey().equals(chatList.get(pos).getChatId())){
                                userOne.child(chatList.get(pos).getChatId()).setValue(null);
                                Toast.makeText(getApplicationContext(), "removed", Toast.LENGTH_SHORT).show();
                                rvChatListAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                DatabaseReference userTwo  = FirebaseDatabase.getInstance().getReference().child("chat");
                userTwo.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            if(ds.getKey().equals(chatList.get(pos).getChatId())){
                                Toast.makeText(getApplicationContext(), "removed", Toast.LENGTH_SHORT).show();
                                userTwo.child(chatList.get(pos).getChatId()).setValue(null);
                                rvChatListAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
                dialog.cancel();
            }
        });
        alert.show();
    }
    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
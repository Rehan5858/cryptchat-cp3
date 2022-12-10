package com.example.cryptchat_cp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptchat_cp.ExtClassFiles.CountryToPhonePrefix;
import com.example.cryptchat_cp.Objects.UserObject;
import com.example.cryptchat_cp.RecAdapters.UserListAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindUserActivity extends AppCompatActivity {

    private RecyclerView rvUserList;
    private RecyclerView.Adapter rvUserListAdapter;
    private RecyclerView.LayoutManager rvUserListLayoutManager;
    ArrayList<UserObject> userList,contactList;
    View  emptyView;
    UserObject objUser;
    TextView emptyTextView,searViewUserList;
    ShimmerFrameLayout shimmerFrameLayout;
    View incldView;
    Button retryBtn;
    boolean exits = false;
    @Override
    protected void onRestart() {
        super.onRestart();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_baseline_people_24);
        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getSupportActionBar().setTitle("  People uses CryptChat");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        contactList = new ArrayList<>();
        userList = new ArrayList<>();
        initializeRecyclerView();
        getContactList();
        incldView = findViewById(R.id.incld_fu);
        retryBtn = incldView.findViewById(R.id.retryBtn);
        searViewUserList = findViewById(R.id.searchViewUserList);
        shimmerFrameLayout = findViewById(R.id.shimmerFindUser);
        shimmerFrameLayout.startShimmer();

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
    private void getContactList(){
        String ISOPrefix = getCountryISO();

        Cursor phones = getContentResolver().query(Phone.CONTENT_URI,null,null,null,null);
        while(phones.moveToNext()){
            @SuppressLint("Range") String name = phones.getString(phones.getColumnIndex(Phone.DISPLAY_NAME));
            @SuppressLint("Range") String phone  = phones.getString(phones.getColumnIndex(Phone.NUMBER));

            phone = phone.replace(" ","");
            phone = phone.replace("-","");
            phone = phone.replace("(","");
            phone = phone.replace(")","");

            if(!String.valueOf(phone.charAt(0)).equals("+"))
                phone = ISOPrefix + phone;


            UserObject  objContact = new UserObject("",name,phone,"");


            contactList.add(objContact);
            getUserDetails(objContact);
        }
    }

    private void getUserDetails(UserObject objContact) {

        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user");
        Query query = mUserDB.orderByChild("phone").equalTo(objContact.getPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String  phone = "", name = "", uNotificationkey = "";

                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        if(childSnapshot.child("phone").getValue()!=null)
                            phone = childSnapshot.child("phone").getValue().toString();
                        if(childSnapshot.child("name").getValue()!=null)
                            name = childSnapshot.child("name").getValue().toString();

                        if(childSnapshot.child("notificationKey").getValue().toString() != null)
                            uNotificationkey = childSnapshot.child("notificationKey").getValue().toString();

                            objUser = new UserObject(childSnapshot.getKey(), name, phone, uNotificationkey);

                            for (UserObject objContactIterator : contactList) {
                                if (objContactIterator.getPhone().equals(objUser.getPhone())) {
                                    objUser.setName(objContactIterator.getName());
                                }
                            }

                            userList.add(objUser);



                                    shimmerFrameLayout.stopShimmer();

                    }
                    rvUserListAdapter.notifyDataSetChanged();
                }

                emptyView = findViewById(R.id.emptyViewfindUser);
                        emptyTextView = findViewById(R.id.emptyTextViewFindUser);
                        if(userList.isEmpty()){
                            emptyView.setVisibility(View.VISIBLE);
                            emptyTextView.setVisibility(View.VISIBLE);
                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            rvUserList.setVisibility(View.GONE);
                        }else{
                            emptyView.setVisibility(View.GONE);
                            emptyTextView.setVisibility(View.GONE);
                            rvUserList.setVisibility(View.VISIBLE);
                        }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    private String getCountryISO(){
        String iso = null;
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if(telephonyManager.getNetworkCountryIso()!=null)
            if (!telephonyManager.getNetworkCountryIso().toString().equals(""))
                iso = telephonyManager.getNetworkCountryIso().toString();
        return CountryToPhonePrefix.getPhone(iso);
    }
    private void initializeRecyclerView() {
        rvUserList = findViewById(R.id.userList);
        rvUserList.setNestedScrollingEnabled(false);
        rvUserList.setHasFixedSize(false);
        rvUserListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false);
        rvUserList.setLayoutManager(rvUserListLayoutManager);
        rvUserListAdapter = new UserListAdapter(userList);
        rvUserList.setAdapter(rvUserListAdapter);
    }

}
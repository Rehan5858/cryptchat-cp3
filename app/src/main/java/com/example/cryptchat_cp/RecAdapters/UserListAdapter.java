package com.example.cryptchat_cp.RecAdapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptchat_cp.MainPageActivity;
import com.example.cryptchat_cp.Objects.UserObject;
import com.example.cryptchat_cp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> implements Serializable {
    ArrayList<UserObject> userList;
    public UserListAdapter(){

    }
    public UserListAdapter(ArrayList<UserObject> userList){
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        UserListViewHolder rcv = new UserListViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, int position) {

            holder.mName.setText(userList.get(position).getName());
            holder.mPhone.setText(userList.get(position).getPhone());


            String uName = userList.get(position).getName().trim();
            String[] uNameArr = uName.split("\\s");
            String avatarOne = String.valueOf(uNameArr[0].charAt(0));
            String avatarTwo;
            if (uNameArr.length > 1) {
                avatarTwo = String.valueOf(uNameArr[1].charAt(0));
            } else {
                avatarTwo = String.valueOf(uNameArr[0].charAt(uNameArr[0].length() - 1));
            }

            holder.c1.setText(avatarOne.toUpperCase() + avatarTwo.toUpperCase());
            holder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Map mobMap = new HashMap();
                    Map mobMap2 = new HashMap();
                    String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();
                    mobMap.put("mob", userList.get(position).getPhone());
                    mobMap.put("uNotificationKey", userList.get(position).getuNotificationKey());
                    mobMap2.put("mob", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                    mobMap2.put("uNotificationKey", OneSignal.getDeviceState().getUserId());

                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).updateChildren(mobMap);
                    FirebaseDatabase.getInstance().getReference().child("user").child(userList.get(position).getUid()).child("chat").child(key).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("user").child(userList.get(position).getUid()).child("chat").child(key).updateChildren(mobMap2);
                    Intent i = new Intent(view.getContext(), MainPageActivity.class);
                    view.getContext().startActivity(i);
//                    ((Activity) view.getContext()).finish();
                }
            });

    }
    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class UserListViewHolder extends RecyclerView.ViewHolder{
        public TextView mName, mPhone, c1;
        LinearLayout mLayout;

        public UserListViewHolder(@NonNull View view) {
            super(view);
            mName = view.findViewById(R.id.name);
            mPhone = view.findViewById(R.id.phone);
            c1 = view.findViewById(R.id.c1);
            mLayout = view.findViewById(R.id.layout);
        }
    }

}

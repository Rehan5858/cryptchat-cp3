package com.example.cryptchat_cp.RecAdapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptchat_cp.ChatActivity;
import com.example.cryptchat_cp.Objects.ChatObject;
import com.example.cryptchat_cp.R;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {
    private chatListInterface chatListInterfaceListner;

    public interface chatListInterface{
        void getChatListPos(int pos);
    }


    ArrayList<ChatObject> chatList;
    public ChatListAdapter(ArrayList<ChatObject> chatList, Context context) {
        this.chatList =  chatList;
        if(context instanceof chatListInterface){
            chatListInterfaceListner = (chatListInterface) context;
        }else{
            throw new RuntimeException("context must implement chatlistInterface");
        }
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatListViewHolder rcv = new ChatListViewHolder(layoutView,chatListInterfaceListner);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListViewHolder holder, final int position) {

            holder.mTitle.setText(chatList.get(position).getName());

            String uName = chatList.get(position).getName().trim();
            String[] uNameArr = uName.split("\\s");
            String avatarOne = String.valueOf(uNameArr[0].charAt(0));
            String avatarTwo;
            if (uNameArr.length > 1) {
                avatarTwo = String.valueOf(uNameArr[1].charAt(0));
            } else {
                avatarTwo = String.valueOf(uNameArr[0].charAt(uNameArr[0].length() - 1));
            }
            holder.mChatString.setText(avatarOne.toUpperCase() + avatarTwo.toUpperCase());

            holder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ChatActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("chatID", chatList.get(holder.getAdapterPosition()).getChatId());
                    bundle.putString("uName", chatList.get(holder.getAdapterPosition()).getName());
                    bundle.putString("uNotificationKey", chatList.get(holder.getAdapterPosition()).getNotificationKey());
                    intent.putExtras(bundle);
                    v.getContext().startActivity(intent);
                }
            });

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder{
        public TextView mTitle, mChatString;
        public View emptyView;
        public LinearLayout mLayout;
        public ChatListViewHolder(View view, chatListInterface chatListInterfaceListner){
            super(view);

            mTitle = view.findViewById(R.id.title);
            mChatString = view.findViewById(R.id.chatString);
            mLayout = view.findViewById(R.id.layout);
            mLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(chatListInterfaceListner != null && getAdapterPosition() != RecyclerView.NO_POSITION){
                        chatListInterfaceListner.getChatListPos(getAdapterPosition());
                    }
                    return true;
                }
            });

        }
    }
}

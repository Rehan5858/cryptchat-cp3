package com.example.cryptchat_cp.RecAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.cryptchat_cp.Objects.MessageObject;
import com.example.cryptchat_cp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    ArrayList<MessageObject> messageList;

    private encDataInterface listener;
    private bioMetrics bioListener;

    public interface encDataInterface{
        void getData(int pos);
        void longPress(int pos);
    }
    public interface bioMetrics{
        void decryptData(int pos);
    }

    public  void setEncDataInterface(encDataInterface listener){
        this.listener = listener;
    }
    public void setBioListenerInterface(bioMetrics bioListener){
        this.bioListener = bioListener;
    }

    public MessageAdapter(ArrayList<MessageObject> messageList, Context mContext, Context mContextBio){
        this.messageList = messageList;

        if(mContext instanceof encDataInterface){
            listener = (encDataInterface) mContext;
        }else{
            throw new RuntimeException("MContext must implement encDataInterface");}
        if(mContextBio instanceof bioMetrics){
            bioListener = (bioMetrics) mContextBio;
        }else{
            throw new RuntimeException("MContext must implement encDataInterface");}
    }



    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        MessageViewHolder rcv = new MessageViewHolder(layoutView, listener ,bioListener);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {
        if(messageList == null){
            return;
        }
        if (!messageList.get(position).getEncData().trim().isEmpty()) {
            holder.unlocked.setVisibility(View.GONE);
            holder.locked.setVisibility(View.VISIBLE);
            holder.enc_dataTextView.setText("Tap on a bio button to decrypt data");
        }else{
            holder.unlocked.setVisibility(View.VISIBLE);
            holder.locked.setVisibility(View.GONE);
        }
        try {
            holder.mMessage.setText(messageList.get(position).getMessage().trim());
            if (!messageList.get(position).getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                holder.mSender.setText(messageList.get(position).getuName().trim());
            } else {
                holder.mSender.setText("You");
            }
            if (messageList.get(position).getColorPickerCode().equals("0")) {
                holder.colorPicker.setImageResource(R.drawable.color_picker_blue);
            } else if (messageList.get(position).getColorPickerCode().equals("1")) {
                holder.colorPicker.setImageResource(R.drawable.color_picker_green);

            } else if (messageList.get(position).getColorPickerCode().equals("2")) {
                holder.colorPicker.setImageResource(R.drawable.color_picker_orange);
            } else if (messageList.get(position).getColorPickerCode().equals("3")) {
                holder.colorPicker.setImageResource(R.drawable.color_picker_yellow);
            }

        }catch (Exception e){
            e.printStackTrace();
            holder.emptyView.setVisibility(View.VISIBLE);
        }
        }
    @Override
    public int getItemCount() {
        return messageList.size();
    }

   public class MessageViewHolder extends RecyclerView.ViewHolder{
       public TextView mMessage, mSender, enc_dataTextView;
       public ImageView colorPicker;
       public LottieAnimationView locked, unlocked;
        public LinearLayout mLayout;
        public Button btn_enc_data;
       public View emptyView;

       MessageViewHolder(View view, encDataInterface listener, bioMetrics bioMetricsListner){
            super(view);
            mLayout = view.findViewById(R.id.layoutF);
             mMessage = view.findViewById(R.id.message);
            mSender = view.findViewById(R.id.sender);
            enc_dataTextView = view.findViewById(R.id.enc_dataTextView);
            colorPicker = view.findViewById(R.id.colorI);
            btn_enc_data = view.findViewById(R.id.btn_enc_data);
            locked = view.findViewById(R.id.lockL);
            unlocked = view.findViewById(R.id.unlockL);
            mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null && getAdapterPosition() != RecyclerView.NO_POSITION){
                        listener.getData(getAdapterPosition());
                    }
                }
            });
           mLayout.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View view) {
                   if(listener != null && getAdapterPosition() != RecyclerView.NO_POSITION){
                        listener.longPress(getAdapterPosition());
                    }
                   return true;
               }
           });
            locked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(bioMetricsListner != null && getAdapterPosition() != RecyclerView.NO_POSITION){
                        bioMetricsListner.decryptData(getAdapterPosition());
                    }
                }
            });
        }
    }

}
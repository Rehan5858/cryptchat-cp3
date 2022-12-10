package com.example.cryptchat_cp.ExtClassFiles;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.cryptchat_cp.R;

public class DeviceInfoBox extends AppCompatDialogFragment {
    String info;
    TextView infoView,head,encMsg;

    public DeviceInfoBox(String info){
        this.info = info;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.popup);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.desc_data_dialog, null);
        builder.setView(view).setTitle("Device Info").setIcon(R.drawable.ic_baseline_perm_device_information_24)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        infoView = view.findViewById(R.id.descDataView);
        head = view.findViewById(R.id.wHead);
        encMsg = view.findViewById(R.id.encMsg);
        infoView.setTextSize(15);
        head.setVisibility(View.GONE);
        encMsg.setVisibility(View.GONE);
        infoView.setGravity(View.NO_ID);
        infoView.setText(info);
        return builder.create();
    }
}
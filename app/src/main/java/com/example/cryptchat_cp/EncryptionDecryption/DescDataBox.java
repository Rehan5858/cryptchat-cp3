package com.example.cryptchat_cp.EncryptionDecryption;

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

public class DescDataBox extends AppCompatDialogFragment {
    String descString, meessgae;
    TextView descDataView;

    public DescDataBox(String descString, String message){
        this.descString = descString;
        this.meessgae = message;
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
            builder.setView(view).setTitle(meessgae).setIcon(R.drawable.ic_baseline_lock_24)
                    .setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            descDataView = view.findViewById(R.id.descDataView);
            descDataView.setText(descString);
            return builder.create();
    }
}
package com.example.cryptchat_cp.EncryptionDecryption;

import android.net.Uri;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageEncDsc {
    static Uri path;
// ******NOT WORKINGGGGGGGGGGGGGGGGGGGGG/****
    public ImageEncDsc(Uri path){
        this.path = path;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {


        int key = 1234;

        FileInputStream fis = new FileInputStream(String.valueOf(path));


        byte data[] = new byte[fis.available()];

        fis.read(data);
        int i = 0;

        for (byte b : data) {
            data[i] = (byte) (b ^ key);
            i++;
        }

        FileOutputStream fos = new FileOutputStream(String.valueOf(path));

        fos.write(data);

        fos.close();
        fis.close();
        Log.e("JOIIIII LEEE:","thai gyu ho");
    }
}

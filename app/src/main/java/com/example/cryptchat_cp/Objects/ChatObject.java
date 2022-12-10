package com.example.cryptchat_cp.Objects;

import java.io.Serializable;

public class ChatObject implements Serializable {
    public String chatId, name, phone, notificationKey;
    public ChatObject(String chatId, String name, String phone, String notificationKey){
        this.chatId = chatId;
        this.name = name;
        this.phone = phone;
        this.notificationKey = notificationKey;
    }
    public ChatObject(String chatId){
        this.chatId = chatId;
    }
    public String getChatId(){
        return chatId;
    }
    public String getPhone(){
        return phone;
    }
    public String getName(){
        return name;
    }
    public String getNotificationKey(){
        return notificationKey;
    }
    public String setName(String name){
        return this.name = name;
    }
}

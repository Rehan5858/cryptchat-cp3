package com.example.cryptchat_cp.Objects;

public class UserObject {
    private String name, phone, uid, uNotificationKey;

    public UserObject(String uid, String name, String phone, String uNotificationKey){
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.uNotificationKey = uNotificationKey;
    }
    public String getUid(){return uid;}
    public String getPhone(){return phone;}
    public String getName(){return name;}
    public String getuNotificationKey(){return uNotificationKey;}
    public void setName(String name){
        this.name = name;
    }
}

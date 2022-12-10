package com.example.cryptchat_cp.Objects;

public class MessageObject {
    String messageId,senderId, message, uName, colorPickerCode, encData, imgUri, algoNumber;

    public MessageObject(String messageId, String senderId, String message, String uName, String colorPickerCode, String encData, String imgUri, String algoNumber){
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
        this.uName = uName;
        this.colorPickerCode = colorPickerCode;
        this.encData = encData;
        this.imgUri = imgUri;
        this.algoNumber = algoNumber;
    }
    public String getAlgoNumber() {
        return algoNumber;
    }

    public void setAlgoNumber(String algoNumber) {
        this.algoNumber = algoNumber;
    }
    public String getMessageId() {
        return messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }

    public String getuName(){return uName;}

    public String getColorPickerCode(){return colorPickerCode;}

    public String getEncData(){return encData;}

    public String getImgUri(){return  imgUri;}
}

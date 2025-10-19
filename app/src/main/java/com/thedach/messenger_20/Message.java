package com.thedach.messenger_20;

public class Message {

    private String text;
    private String senderId; // id отправителя
    private String receiverId; // id получателя

    public Message(String receiverId, String senderId, String text) {
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.text = text;
    }

    public Message() {
    }


    public String getReceiverId() {
        return receiverId;
    }
    public String getSenderId() {
        return senderId;
    }
    public String getText() {
        return text;
    }
}

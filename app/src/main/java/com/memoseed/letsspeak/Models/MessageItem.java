package com.memoseed.letsspeak.Models;

import java.util.Date;

/**
 * Created by Mohamed Sayed on 10/31/2016.
 */

public class MessageItem {
    String message,senderId,name;
    Date date;

    public MessageItem(Date date, String message, String senderId,String name) {
        this.date = date;
        this.message = message;
        this.senderId = senderId;
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

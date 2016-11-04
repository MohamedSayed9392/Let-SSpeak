package com.memoseed.letsspeak.Models;

import java.util.Date;

/**
 * Created by Mohamed Sayed on 10/31/2016.
 */

public class ChatItem {
   String name,lastMessage,image,senderId,objectId;

    public ChatItem(String image, String lastMessage, String name,String senderId,String objectId) {
        this.image = image;
        this.lastMessage = lastMessage;
        this.name = name;
        this.senderId=senderId;
        this.objectId = objectId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}

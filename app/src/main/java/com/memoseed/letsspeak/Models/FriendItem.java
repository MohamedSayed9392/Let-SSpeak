package com.memoseed.letsspeak.Models;

/**
 * Created by Mohamed Sayed on 10/31/2016.
 */

public class FriendItem {
    String name,image,objectId;

    public FriendItem(String image, String name, String objectId) {
        this.image = image;
        this.name = name;
        this.objectId = objectId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}

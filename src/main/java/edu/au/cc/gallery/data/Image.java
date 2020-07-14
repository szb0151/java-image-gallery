package edu.au.cc.gallery.data;

public class Image {
    private User user;
    private String imageData;
    private String uuid;


    public Image(User user, String uuid, String imageData) {
        this.user = user;
        this.uuid = uuid;
        this.imageData = imageData;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getId() {
        return uuid;
    }

    public void setId(String uuid) {
        this.uuid = uuid;
    }
}

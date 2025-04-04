package vn.iotstar.bt04_04_25.UploadImage;

public class ImageUpload {
    private int id;
    private String username;
    private String avatar;

    public ImageUpload(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ImageUpload(int id, String avatar) {
        this.id = id;
        this.avatar = avatar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}

package whatsappclone.proyecto_javier_juan_uceda.whatsappclone.User;

import java.io.Serializable;

public class UserObject implements Serializable {

    private String uid, name,
            phone, notificationKey;
    private Boolean selected;


    public UserObject(String uid) {
        this();
        this.uid = uid;

    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public UserObject() {
        selected = false;
    }

    public String getNotificationKey() {
        return notificationKey;
    }

    public void setNotificationKey(String notificationKey) {
        this.notificationKey = notificationKey;
    }

    public UserObject(String uid, String name, String phone) {
        this();
        this.name = name;
        this.phone = phone;
        this.uid = uid;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
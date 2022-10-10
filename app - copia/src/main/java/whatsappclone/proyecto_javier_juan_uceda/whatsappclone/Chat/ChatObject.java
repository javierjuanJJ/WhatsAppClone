package whatsappclone.proyecto_javier_juan_uceda.whatsappclone.Chat;

import java.io.Serializable;
import java.util.ArrayList;

import whatsappclone.proyecto_javier_juan_uceda.whatsappclone.User.UserObject;

public class ChatObject implements Serializable {
    private String chatId;

    private ArrayList<UserObject> userObjectArrayList;

    public ChatObject(String chatId) {
        this.chatId = chatId;
        userObjectArrayList = new ArrayList<>();
    }

    public ArrayList<UserObject> getUserObjectArrayList() {
        return userObjectArrayList;
    }

    public void addUserToArrayList(UserObject mUser) {
        userObjectArrayList.add(mUser);
    }


    public String getChatId() {
        return chatId;
    }
}
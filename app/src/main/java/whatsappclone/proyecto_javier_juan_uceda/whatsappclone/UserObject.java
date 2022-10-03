package whatsappclone.proyecto_javier_juan_uceda.whatsappclone;

public class UserObject {

    private String name,
            phone;

    public UserObject(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }
}
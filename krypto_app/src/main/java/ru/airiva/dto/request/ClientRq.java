package ru.airiva.dto.request;

/**
 * @author Ivan
 */
public class ClientRq extends RqDto{

    private String phone;
    private String username;

    public ClientRq() {
    }

    public ClientRq(String phone, String username) {
        this.phone = phone;
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

package com.mycompany.demojdpcmaven;

public class User {
    private int id;
    private String fullName;
    private String email;
    private String phone;
    private int accountId;

    public User() {
    }

    public User(int accountId, String phone, String email, String fullName, int id) {
        this.accountId = accountId;
        this.phone = phone;
        this.email = email;
        this.fullName = fullName;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
}

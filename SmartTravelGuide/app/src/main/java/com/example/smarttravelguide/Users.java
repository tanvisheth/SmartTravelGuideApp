package com.example.smarttravelguide;

public class Users {
    private String Name;
    private String Address;
    private String Email;
    private String Password;
    private Long Phone;

    public Users() {
    }

    public String getName() {
        return Name;
    }

    public Users(String name, String address, String email, String password, Long phone) {
        Name = name;
        Address = address;
        Email = email;
        Password = password;
        Phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public String getEmail() {
        return Email;
    }

    public String getPassword() {
        return Password;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setPhone(Long phone) {
        Phone = phone;
    }

    public Long getPhone() {
        return Phone;
    }
}

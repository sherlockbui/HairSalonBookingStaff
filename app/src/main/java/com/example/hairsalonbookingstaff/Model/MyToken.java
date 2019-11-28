package com.example.hairsalonbookingstaff.Model;

public class MyToken {
    private String token, phoneCustomber, idbarber;


    public MyToken() {
    }

    public MyToken(String token, String phoneCustomber) {
        this.token = token;
        this.phoneCustomber = phoneCustomber;
    }

    public String getPhoneCustomber() {
        return phoneCustomber;
    }

    public void setPhoneCustomber(String phoneCustomber) {
        this.phoneCustomber = phoneCustomber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIdbarber() {
        return idbarber;
    }

    public void setIdbarber(String idbarber) {
        this.idbarber = idbarber;
    }
}

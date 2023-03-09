package com.example.myday;

public class Users {
    public String userid;
    public Users() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public Users(String userid) {
        this.userid = userid;
    }
}

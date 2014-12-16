package com.vnguyen.liveokeremote;

public class User {
    private String name;
    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object usr) {
        if (!(usr instanceof User)) {
            return false;
        }
        User that = (User) usr;

        return this.getName().equalsIgnoreCase(that.getName());
    }
}

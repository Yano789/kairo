package com.example.infosys_1d;

import java.math.BigInteger;

public abstract class User {

    public BigInteger id;
    public String email;
    private String password;

    public String name;

    public BigInteger getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}

package com.example.infosys_1d;

import android.os.Parcel;

import java.math.BigInteger;

public class FacultyAdmin extends Admin {
    public FacultyAdmin(String email, String name, BigInteger id, String password) {
        this.email = email;
        this.name = name;
        this.id = id;
        this.password = password;
    }

    protected FacultyAdmin(Parcel in) {
        super(in);
    }

    public static final Creator<FacultyAdmin> CREATOR = new Creator<FacultyAdmin>() {
        @Override
        public FacultyAdmin createFromParcel(Parcel in) {
            return new FacultyAdmin(in);
        }

        @Override
        public FacultyAdmin[] newArray(int size) {
            return new FacultyAdmin[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}


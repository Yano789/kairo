package com.example.infosys_1d;

import android.os.Parcel;

import java.math.BigInteger;

public class FifthRowAdmin extends Admin {

    public FifthRowAdmin(String email, String name, BigInteger id) {
        this.email = email;
        this.name = name;
        this.id = id;
    }

    protected FifthRowAdmin(Parcel in) {
        super(in);
    }

    public static final Creator<FifthRowAdmin> CREATOR = new Creator<FifthRowAdmin>() {
        @Override
        public FifthRowAdmin createFromParcel(Parcel in) {
            return new FifthRowAdmin(in);
        }

        @Override
        public FifthRowAdmin[] newArray(int size) {
            return new FifthRowAdmin[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
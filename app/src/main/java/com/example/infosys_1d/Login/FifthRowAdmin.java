package com.example.infosys_1d.Login;

import android.os.Parcel;

public class FifthRowAdmin extends Admin {

    public FifthRowAdmin(String email, String name, String adminId, String password) {
        this.email = email;
        this.name = name;
        this.adminId = adminId;
        this.password = password;
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

    public String getPillar() {
        return "Fifthrow Student Organization";
    }
}
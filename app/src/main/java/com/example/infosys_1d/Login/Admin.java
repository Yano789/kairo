package com.example.infosys_1d.Login;

import android.os.Parcel;
import android.os.Parcelable;
import java.math.BigInteger;

public abstract class Admin extends User implements Parcelable {

    protected String adminId;

    protected Admin() {}

    protected Admin(Parcel in) {
        super.id = in.readString() != null ? new BigInteger(in.readString()) : null;
        this.adminId = in.readString();
        this.email = in.readString();
        this.name = in.readString();
        this.password = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id != null ? id.toString() : null);
        dest.writeString(adminId);
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(password);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public BigInteger getId() {
        return BigInteger.valueOf(0); // Admins use adminId instead
    }

    public String getAdminId() {
        return adminId;
    }

    public boolean isFifthrowAdmin() {
        return adminId != null && adminId.matches("F\\d+");
    }
}
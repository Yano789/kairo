package com.example.infosys_1d.Login;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigInteger;
import java.util.Calendar;

public abstract class Admin extends User implements Parcelable {


    protected Admin() {}

    protected Admin(Parcel in) {
        this.id = new BigInteger(in.readString());
        this.email = in.readString();
        this.name = in.readString();
        this.password = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id != null ? id.toString() : null);
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(password);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}

package com.example.infosys_1d;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigInteger;

public abstract class Admin extends User implements Parcelable {
    public Calendar generalCalendar = new Calendar();

    protected Admin() {}

    protected Admin(Parcel in) {
        this.id = new BigInteger(in.readString());
        this.email = in.readString();
        this.name = in.readString();
        this.generalCalendar = in.readParcelable(Calendar.class.getClassLoader());
        this.password = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id != null ? id.toString() : null);
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(password);
        dest.writeParcelable(generalCalendar, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}

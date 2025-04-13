package com.example.infosys_1d.Login;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.math.BigInteger;

public interface Identification {
    BigInteger getID();
    String getAffiliation(); // Renamed description to fit the standards
}

class FifthRowID implements Identification, Parcelable {
    public BigInteger id;
    public String fifthRowName;

    public FifthRowID(BigInteger id, String fifthRowName) {
        this.id = id;
        this.fifthRowName = fifthRowName;
    }

    protected FifthRowID(Parcel in) {
        String idString = in.readString();
        this.id = idString != null ? new BigInteger(idString) : null;
        this.fifthRowName = in.readString();
    }

    public static final Creator<FifthRowID> CREATOR = new Creator<FifthRowID>() {
        @Override
        public FifthRowID createFromParcel(Parcel in) {
            return new FifthRowID(in);
        }

        @Override
        public FifthRowID[] newArray(int size) {
            return new FifthRowID[size];
        }
    };

    @Override
    public BigInteger getID() {
        return id;
    }

    @Override
    public String getAffiliation() {
        return fifthRowName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id != null ? id.toString() : null);
        dest.writeString(fifthRowName);
    }
}

class FacultyID implements Identification, Parcelable {
    public BigInteger id;
    public String facultyName;

    public FacultyID(BigInteger id, String facultyName) {
        this.id = id;
        this.facultyName = facultyName;
    }

    protected FacultyID(Parcel in) {
        String idString = in.readString();
        this.id = idString != null ? new BigInteger(idString) : null;
        this.facultyName = in.readString();
    }

    public static final Creator<FacultyID> CREATOR = new Creator<FacultyID>() {
        @Override
        public FacultyID createFromParcel(Parcel in) {
            return new FacultyID(in);
        }

        @Override
        public FacultyID[] newArray(int size) {
            return new FacultyID[size];
        }
    };

    @Override
    public BigInteger getID() {
        return id;
    }

    @Override
    public String getAffiliation() {
        return facultyName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id != null ? id.toString() : null);
        dest.writeString(facultyName);
    }
}

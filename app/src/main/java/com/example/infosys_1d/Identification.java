package com.example.infosys_1d;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public interface Identification {
    public int getID();
    public String getAffiliation(); //Renamed description to fit the standards
}

class FifthRowID implements Identification, Parcelable {
    public int id;
    public String fifthRowName;

    public FifthRowID(int id, String fifthRowName){
        this.id = id;
        this.fifthRowName = fifthRowName;
    }

    protected FifthRowID(Parcel in) {
        id = in.readInt();
        fifthRowName = in.readString();
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
    public int getID() {
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
        dest.writeInt(id);
        dest.writeString(fifthRowName);
    }
};

class FacultyID implements Identification, Parcelable{
    public int id;
    public String facultyName;

    public FacultyID(int id, String facultyName){
        this.id = id;
        this.facultyName = facultyName;
    }

    protected FacultyID(Parcel in) {
        id = in.readInt();
        facultyName = in.readString();
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
    public int getID() {
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
        dest.writeInt(this.id);
        dest.writeString(this.facultyName);
    }
}
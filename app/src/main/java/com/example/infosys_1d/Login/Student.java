package com.example.infosys_1d.Login;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;

public class Student extends User implements Parcelable {
    public ArrayList<FifthRowID> fifthRows = new ArrayList<>();
    public String facultyName;
    private FacultyID facultyID;

    Student(BigInteger id, String email, String name, String facultyName, String password){
        this.facultyName = facultyName;
        this.password = password;
        this.id = id;
        this.facultyID = new FacultyID(id, facultyName);
        this.email = email;
        this.name = name;
    }

    public Student(String facultyName, ArrayList<FifthRowID> fifthRows, String password, BigInteger id, String email, String name){ //if they even have fifthrows
        this.facultyName = facultyName;
        this.id = id;
        this.facultyID = new FacultyID(id, facultyName);
        this.fifthRows = fifthRows;
        this.password= password;
        this.email = email;
        this.name = name;
    }

    protected Student(Parcel in) {
        this.id = new BigInteger(in.readString());
        this.email = in.readString();
        this.name = in.readString();
        this.password = in.readString();
        this.facultyName = in.readString();
        this.facultyID = in.readParcelable(FacultyID.class.getClassLoader());
        in.readTypedList(fifthRows, FifthRowID.CREATOR);
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id != null ? id.toString() : null); // BigInteger -> String
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(facultyName);
        dest.writeString(password);
        dest.writeParcelable(facultyID, flags);
        dest.writeTypedList(fifthRows);
    }


}

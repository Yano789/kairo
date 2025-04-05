package com.example.infosys_1d;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;

public class Student extends User implements Parcelable {
    public ArrayList<FifthRowID> fifthRows = new ArrayList<>();
    public String facultyName;
    private FacultyID facultyID = new FacultyID(id, facultyName);
    public Calendar generalCalendar = new Calendar();
    public Calendar personalCalendar = new Calendar();

    Student(String facultyName, FacultyID facultyID){
        this.facultyName = facultyName;
        this.facultyID = facultyID;
    }

    public Student(String facultyName, FacultyID facultyID, ArrayList<FifthRowID> fifthRows){ //if they even have fifthrows
        this.facultyName = facultyName;
        this.facultyID = facultyID;
        this.fifthRows = fifthRows;
    }

    protected Student(Parcel in) {
        this.id = new BigInteger(in.readString());
        this.email = in.readString();
        this.name = in.readString();
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
        dest.writeParcelable(facultyID, flags);
        dest.writeTypedList(fifthRows);
        //dest.writeParcelable(generalCalendar, flags); need to update calendar for this to work
        //dest.writeParcelable(personalCalendar, flags);
    }


}

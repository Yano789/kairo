package com.example.infosys_1d;

import java.math.BigInteger;
import java.util.ArrayList;

public class UserRepository {

    public static ArrayList<Student> getSampleStudents() {
        ArrayList<Student> students = new ArrayList<>();
        ArrayList<FifthRowID> fifthRows = new ArrayList<>();
        ArrayList<FifthRowID> fifthRows1 = new ArrayList<>();
        ArrayList<FifthRowID> fifthRows2 = new ArrayList<>();
        fifthRows.add(new FifthRowID(new BigInteger("50"), "MindSports"));
        fifthRows2.add(new FifthRowID(new BigInteger("51"), "CAT"));

        Student s1 = new Student(
                "Computer Science and Design",
                fifthRows,
                "password123",
                new BigInteger("1007916"),
                "jafira@gmail.com",
                "Jafira Nassar");

        Student s2 = new Student(
                new BigInteger("1007915"),
                "Sharon@gmail.com",
                "Sharon Ashok",
                "Computer Science and Design",
                "password456");

        students.add(s1);
        students.add(s2);

        return students;
    }

    public static ArrayList<Admin> getSampleAdmins() {
        ArrayList<Admin> admins = new ArrayList<>();

        Admin fAdmin = new FacultyAdmin("admin1@school.edu", "Dennis Wasabi", new BigInteger("9001"), "adminpass");
        Admin frAdmin = new FifthRowAdmin("admin2@fifthrow.edu", "Shreya", new BigInteger("9002"), "fifthrowpass");

        admins.add(fAdmin);
        admins.add(frAdmin);

        return admins;
    }
}


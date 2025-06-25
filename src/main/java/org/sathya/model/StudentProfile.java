package org.sathya.model;

import jakarta.persistence.*;

@Entity
public class StudentProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String address;
    private String phoneNo;
    private String dob;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    @Override
    public String toString() {
        return "StudentProfile{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", dob='" + dob + '\'' +
                '}';
    }
// Getters, setters, constructor, toString
}

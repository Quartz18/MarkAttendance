package com.example1.markattendance;

public class Model_Member {

    String members_name, members_device;
    String members_number,document_name;

    public Model_Member(String members_number, String members_name, String members_device,String document_name) {
        this.members_number = members_number;
        this.members_name = members_name;
        this.members_device = members_device;
        this.document_name = document_name;
    }

    public String getMembers_name() {
        return members_name;
    }

    public void setMembers_name(String members_name) {
        this.members_name = members_name;
    }

    public String getMembers_device() {
        return members_device;
    }

    public void setMembers_device(String members_device) {
        this.members_device = members_device;
    }

    public String getDocument_name() {
        return document_name;
    }

    public void setDocument_name(String document_name) {
        this.document_name = document_name;
    }

    public String getMembers_number() {
        return members_number;
    }

    public void setMembers_number(String members_number) {
        this.members_number = members_number;
    }
}

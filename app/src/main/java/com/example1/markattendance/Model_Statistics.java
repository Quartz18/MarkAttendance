package com.example1.markattendance;

public class Model_Statistics {
    String member_id, member_name,member_ratio, document_name, class_name, subject_name;

    public Model_Statistics(String member_id, String member_name, String member_ratio, String document_name, String class_name, String subject_name) {
        this.member_id = member_id;
        this.member_name = member_name;
        this.member_ratio = member_ratio;
        this.document_name = document_name;
        this.class_name = class_name;
        this.subject_name = subject_name;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public String getMember_ratio() {
        return member_ratio;
    }

    public void setMember_ratio(String member_ratio) {
        this.member_ratio = member_ratio;
    }

    public String getDocument_name() {
        return document_name;
    }

    public void setDocument_name(String document_name) {
        this.document_name = document_name;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }
}

package com.example1.markattendance;

public class Model_Past_Records {
    String past_record_name, member_id, member_name, member_attended;

    public Model_Past_Records(String past_record_name, String member_id, String member_name, String member_attended) {
        this.past_record_name = past_record_name;
        this.member_id = member_id;
        this.member_name = member_name;
        this.member_attended = member_attended;
    }

    public String getPast_record_name() {
        return past_record_name;
    }

    public void setPast_record_name(String past_record_name) {
        this.past_record_name = past_record_name;
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

    public String getMember_attended() {
        return member_attended;
    }

    public void setMember_attended(String member_attended) {
        this.member_attended = member_attended;
    }
}

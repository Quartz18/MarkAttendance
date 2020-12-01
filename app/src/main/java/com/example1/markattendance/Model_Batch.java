package com.example1.markattendance;

public class Model_Batch {

    String batch_name;
    String total_member;
    String count_of_subjects;
    int found;

    public Model_Batch(String batch_name, String total_member, String count_of_subjects, int found) {
        this.batch_name = batch_name;
        this.total_member = total_member;
        this.count_of_subjects = count_of_subjects;
        this.found = found;
    }

    public int getFound() {
        return found;
    }

    public void setFound(int found) {
        this.found = found;
    }

    public String getBatch_name() {
        return batch_name;
    }

    public void setBatch_name(String batch_name) {
        this.batch_name = batch_name;
    }

    public String getTotal_member() {
        return total_member;
    }

    public void setTotal_member(String total_member) {
        this.total_member = total_member;
    }

    public String getCount_of_subjects() {
        return count_of_subjects;
    }

    public void setCount_of_subjects(String count_of_subjects) {
        this.count_of_subjects = count_of_subjects;
    }
}

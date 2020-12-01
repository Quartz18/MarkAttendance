package com.example1.markattendance;

import java.util.ArrayList;

public class Model_Attendance {
    String attendee_id, attendee_name;
    ArrayList<String> attendee_details;
    Boolean attendance_select_all;
    int found;

    public Model_Attendance(String attendee_id, String attendee_name, ArrayList<String> attendee_details, Boolean attendance_select_all,int found) {
        this.attendee_id = attendee_id;
        this.attendee_name = attendee_name;
        this.attendee_details = attendee_details;
        this.attendance_select_all = attendance_select_all;
        this.found = found;
    }

    public int getFound() {
        return found;
    }

    public void setFound(int found) {
        this.found = found;
    }

    public String getAttendee_id() {
        return attendee_id;
    }

    public void setAttendee_id(String attendee_id) {
        this.attendee_id = attendee_id;
    }

    public String getAttendee_name() {
        return attendee_name;
    }

    public void setAttendee_name(String attendee_name) {
        this.attendee_name = attendee_name;
    }

    public ArrayList<String> getAttendee_details() {
        return attendee_details;
    }

    public void setAttendee_details(ArrayList<String> attendee_details) {
        this.attendee_details = attendee_details;
    }

    public Boolean getAttendance_select_all() {
        return attendance_select_all;
    }

    public void setAttendance_select_all(Boolean attendance_select_all) {
        this.attendance_select_all = attendance_select_all;
    }
}

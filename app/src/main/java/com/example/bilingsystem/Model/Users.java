package com.example.bilingsystem.Model;

public class Users {

    private String accno;
    private String fname;
    private String mname;
    private String lname;
    private String address;
    private String email;
    private String password;
    private String recent_total_amt_due;
    private String recent_total_amt_aftr_due;
    private String recent_due_date;


    public Users(){
    }

    public Users(String accno, String fname, String mname, String lname, String addresss,
                 String email, String password, String recent_total_amt_due, String recent_total_amt_aftr_due, String recent_due_date) {
        this.accno = accno;
        this.fname = fname;
        this.mname = mname;
        this.address = addresss;
        this.lname = lname;
        this.email = email;
        this.password = password;
        this.recent_total_amt_due = recent_total_amt_due;
        this.recent_total_amt_aftr_due = recent_total_amt_aftr_due;
        this.recent_due_date = recent_due_date;
    }

    public String getAccno() {
        return accno;
    }

    public void setAccno(String accno) {
        this.accno = accno;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRecent_total_amt_due() {   return recent_total_amt_due;  }

    public void setRecent_total_amt_due(String recent_total_amt_due) {  this.recent_total_amt_due = recent_total_amt_due;
    }

    public String getRecent_total_amt_aftr_due() {  return recent_total_amt_aftr_due;  }

    public void setRecent_total_amt_aftr_due(String recent_total_amt_aftr_due) {   this.recent_total_amt_aftr_due = recent_total_amt_aftr_due;
    }

    public String getRecent_due_date() {  return recent_due_date;    }

    public void setRecent_due_date(String recent_due_date) {   this.recent_due_date = recent_due_date;    }


}

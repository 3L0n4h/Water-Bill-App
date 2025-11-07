package com.example.bilingsystem.Model;

public class Bill {

    private int bill_no;
    private String period_from;
    private String period_to;
    private String due_date;
    private int meter_no;
    private int current_reading;
    private int prev_reading;
    private int consumption;
    private int other_consumption;
    private int total_consumption;
    private double maintenance_fee;
    private double curr_due;
    private double total_amount_due;
    private double penalty;
    private double total_after_due;

    public Bill(){

    }

    public Bill(int bill_no, String period_from, String period_to, String due_date,int meter_no, int current_reading,
                int prev_reading, int consumption, int other_consumption, int total_consumption, double maintenance_fee,
                double curr_due,double total_amount_due, double penalty, double total_after_due) {
        this.bill_no = bill_no;
        this.period_from = period_from;
        this.period_to = period_to;
        this.due_date = due_date;
        this.meter_no = meter_no;
        this.current_reading = current_reading;
        this.prev_reading = prev_reading;
        this.consumption = consumption;
        this.other_consumption = other_consumption;
        this.total_consumption = total_consumption;
        this.maintenance_fee = maintenance_fee;
        this.total_amount_due = total_amount_due;
        this.curr_due = curr_due;
        this.penalty = penalty;
        this.total_after_due = total_after_due;
    }

    public int getBill_no() {
        return bill_no;
    }

    public void setBill_no(int bill_no) {
        this.bill_no = bill_no;
    }

    public String getPeriod_from() {
        return period_from;
    }

    public void setPeriod_from(String period_from) {
        this.period_from = period_from;
    }

    public String getPeriod_to() {
        return period_to;
    }

    public void setPeriod_to(String period_to) {
        this.period_to = period_to;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public int getCurrent_reading() {
        return current_reading;
    }

    public void setCurrent_reading(int current_reading) {
        this.current_reading = current_reading;
    }

    public int getConsumption() {
        return consumption;
    }

    public void setConsumption(int consumption) {
        this.consumption = consumption;
    }

    public int getOther_consumption() {
        return other_consumption;
    }

    public void setOther_consumption(int other_consumption) {
        this.other_consumption = other_consumption;
    }

    public int getTotal_consumption() {
        return total_consumption;
    }

    public void setTotal_consumption(int total_consumption) {
        this.total_consumption = total_consumption;
    }

    public double getMaintenance_fee() {
        return maintenance_fee;
    }

    public void setMaintenance_fee(double maintenance_fee) {
        this.maintenance_fee = maintenance_fee;
    }

    public double getTotal_amount_due() {
        return total_amount_due;
    }

    public void setTotal_amount_due(double total_amount_due) {
        this.total_amount_due = total_amount_due;
    }

    public double getPenalty() {
        return penalty;
    }

    public void setPenalty(double penalty) {
        this.penalty = penalty;
    }

    public double getTotal_after_due() {
        return total_after_due;
    }

    public void setTotal_after_due(double total_after_due) { this.total_after_due = total_after_due; }

    public double getCurr_due() {
        return curr_due;
    }

    public void setCurr_due(double curr_due) {
        this.curr_due = curr_due;
    }

    public int getMeter_no() {    return meter_no;   }

    public void setMeter_no(int meter_no) {    this.meter_no = meter_no;   }

    public int getPrev_reading() {    return prev_reading;   }

    public void setPrev_reading(int prev_reading) {     this.prev_reading = prev_reading;    }

}

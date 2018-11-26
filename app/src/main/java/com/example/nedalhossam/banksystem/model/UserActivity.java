package com.example.nedalhossam.banksystem.model;

/**
 * Created by Nedal Hossam on 10/04/2018.
 */

public class UserActivity {
    private int id;
    private int accountNumber;
    private String type;
    private String date;
    private String value;
    private String to;
    private String from;
    private String timeInMilliseconds;
    public UserActivity(int id, int accountNumber, String type, String date, String value, String to, String from , String timeInMilliseconds) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.type = type;
        this.date = date;
        this.value = value;
        this.to = to;
        this.from = from;
        this.timeInMilliseconds = timeInMilliseconds ;
    }

    public String getTimeInMilliseconds() {
        return timeInMilliseconds;
    }

    public void setTimeInMilliseconds(String timeInMilliseconds) {
        this.timeInMilliseconds = timeInMilliseconds;
    }

    public int getId() {
        return id;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getValue() {
        return value;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}

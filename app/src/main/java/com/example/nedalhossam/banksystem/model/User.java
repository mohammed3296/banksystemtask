package com.example.nedalhossam.banksystem.model;

/**
 * Created by Nedal Hossam on 09/04/2018.
 */

public class User {
    private String firstName;
    private String secondName;
    private String email;
    private int accountNumber;

    public User(int accountNumber, String firstName, String secondName, String email) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        this.accountNumber = accountNumber;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getEmail() {
        return email;
    }

    public int getAccountNumber() {
        return accountNumber;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

}

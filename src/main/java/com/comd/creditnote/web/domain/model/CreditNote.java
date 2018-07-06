/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.domain.model;

/**
 *
 * @author maliska
 */
public class CreditNote {

    private String number;
    private double value;

    public CreditNote() {
    }

    public CreditNote(String number, double value) {
        this.number = number;
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.domain.model;

import java.math.BigDecimal;

/**
 *
 * @author maliska
 */
public class CreditNote {

    private String number;
    private Amount amount;

    public CreditNote() {
    }

    public CreditNote(String number, double value) {
        this.number = number;
        this.amount = new Amount(new BigDecimal(value));
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

}

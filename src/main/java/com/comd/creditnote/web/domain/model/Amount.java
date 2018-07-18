/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.domain.model;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author maliska
 */
public class Amount {

    private BigDecimal value;

    public Amount(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigInteger getIntValue() {
        return value.abs().toBigInteger();
    }

    public BigInteger getDecimalValue() {
        int decimals = 2;
        BigInteger INTEGER = getIntValue();
        BigInteger DECIMAL = ((value.subtract(new BigDecimal(INTEGER))).multiply(new BigDecimal(10).pow(decimals))).toBigInteger();
        return DECIMAL;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.domain.model;

import java.util.Date;

/**
 *
 * @author maliska
 */
public class Delivery {

    private Date blDate;
    private String vesselName;
    private String invoiceNumber;
    private double cargoValue;

    public Delivery(Date blDate, String vesselName, String invoiceNumber, double cargoValue) {
        this.blDate = blDate;
        this.vesselName = vesselName;
        this.invoiceNumber = invoiceNumber;
        this.cargoValue = cargoValue;
    }

    public Delivery() {
    }

    public Date getBlDate() {
        return blDate;
    }

    public void setBlDate(Date blDate) {
        this.blDate = blDate;
    }

    public String getVesselName() {
        return vesselName;
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public double getCargoValue() {
        return cargoValue;
    }

    public void setCargoValue(double cargoValue) {
        this.cargoValue = cargoValue;
    }

}

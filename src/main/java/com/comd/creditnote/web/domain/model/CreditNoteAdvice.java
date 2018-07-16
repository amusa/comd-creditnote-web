/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.domain.model;

import com.comd.customer.lib.v1.response.Address;

/**
 *
 * @author maliska
 */
public class CreditNoteAdvice {

    private String customer;
    private Address address;
    private String crudeName;
    private String producer;
    private Delivery delivery;
    private CreditNote creditNote;

    public CreditNoteAdvice() {
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getCrudeName() {
        return crudeName;
    }

    public void setCrudeName(String crudeName) {
        this.crudeName = crudeName;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public CreditNote getCreditNote() {
        return creditNote;
    }

    public void setCreditNote(CreditNote creditNote) {
        this.creditNote = creditNote;
    }

}

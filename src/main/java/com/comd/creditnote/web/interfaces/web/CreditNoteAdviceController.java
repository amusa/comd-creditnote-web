/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.interfaces.web;

import com.comd.creditnote.web.domain.model.Address;
import com.comd.creditnote.web.domain.model.CreditNote;
import com.comd.creditnote.web.domain.model.CreditNoteAdvice;
import com.comd.creditnote.web.domain.model.Delivery;
import com.comd.creditnote.web.util.CreditNoteLogger;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author maliska
 */
@Named(value = "cnAdviceController")
@SessionScoped
public class CreditNoteAdviceController implements Serializable {

    @CreditNoteLogger
    @Inject
    Logger logger;

    @Inject
    private DeliveryClient deliveryClient;

    @Inject
    private CreditNoteClient creditNoteClient;

    private String blDate;
    private String customerId;

    private CreditNoteAdvice creditNoteAdvice;

    @PostConstruct
    public void init() {
        logger.log(Level.INFO, "Initialization... B/L date={0}, customerId={1}", new Object[]{blDate, customerId});
    }

    public void viewParamListener() {
        logger.log(Level.INFO, "B/L date={0}, customerId={1}", new Object[]{blDate, customerId});
        List<com.comd.delivery.lib.v1.Delivery> deliveries = new ArrayList<>();
                
        try {
            deliveries = deliveryClient.delivery(blDate, "123456", customerId);
        } catch (Exception ex) {
            Logger.getLogger(CreditNoteAdviceController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //TODO: call creditnote service
        //TODO: call customer service

        com.comd.delivery.lib.v1.Delivery delivery = deliveries.get(1);
        creditNoteAdvice = new CreditNoteAdvice();
        creditNoteAdvice.setAddress(new Address("street 1", "city", "state", "country"));
        creditNoteAdvice.setCreditNote(new CreditNote("S/12345", 20000.00));
        creditNoteAdvice.setDelivery(
                new Delivery(
                        delivery.getBlDate(), delivery.getVesselName(), delivery.getInvoiceNumber(), delivery.getNetValue())
        );
        creditNoteAdvice.setCustomer(delivery.getCustomer());
        creditNoteAdvice.setProducer(delivery.getProducer());
        creditNoteAdvice.setCrudeName(delivery.getCrudeName());
    }

    public CreditNoteAdvice getCreditNoteAdvice() {
        return creditNoteAdvice;
    }

    public void setCreditNoteAdvice(CreditNoteAdvice creditNoteAdvice) {
        this.creditNoteAdvice = creditNoteAdvice;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getBlDate() {
        return blDate;
    }

    public void setBlDate(String blDate) {
        this.blDate = blDate;
    }

   

}

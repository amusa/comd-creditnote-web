/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.application.internal;

import com.comd.creditnote.web.application.CreditNoteService;
import com.comd.creditnote.web.domain.model.CreditNote;
import com.comd.creditnote.web.domain.model.CreditNoteAdvice;
import com.comd.creditnote.web.domain.model.Delivery;
import com.comd.creditnote.web.interfaces.rest.exceptions.CustomerException;
import com.comd.creditnote.web.interfaces.web.CreditNoteAdviceController;
import com.comd.creditnote.web.interfaces.web.CreditNoteClient;
import com.comd.creditnote.web.interfaces.web.CustomerClient;
import com.comd.creditnote.web.interfaces.web.DeliveryClient;
import com.comd.customer.lib.v1.response.Address;
import com.comd.customer.lib.v1.response.Customer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 *
 * @author maliska
 */
@ApplicationScoped
public class DefaultCreditNoteService implements CreditNoteService {

    @Inject
    private DeliveryClient deliveryClient;

    @Inject
    private CreditNoteClient creditNoteClient;

    @Inject
    private CustomerClient customerClient;

    @Override
    public CreditNoteAdvice generateCreditNoteAdvice(String customerId, String blDate) throws CustomerException {
        List<com.comd.delivery.lib.v1.Delivery> deliveries = new ArrayList<>();
        CreditNoteAdvice creditNoteAdvice;

        try {
            deliveries = deliveryClient.delivery(blDate, "123456", customerId);//TODO:vessel id hardcoded
        } catch (Exception ex) {
            Logger.getLogger(CreditNoteAdviceController.class.getName()).log(Level.SEVERE, null, ex);
        }

        //TODO: call creditnote service
        //TODO: call customer service
        Customer customer = customerClient.customer(customerId);

        com.comd.delivery.lib.v1.Delivery delivery = deliveries.get(0);
        creditNoteAdvice = new CreditNoteAdvice();
        creditNoteAdvice.setAddress(customer.getAddress());
        creditNoteAdvice.setCreditNote(new CreditNote("S/12345", 20000.00));
        creditNoteAdvice.setDelivery(
                new Delivery(
                        delivery.getBlDate(), delivery.getVesselName(), delivery.getInvoiceNumber(), delivery.getNetValue())
        );
        creditNoteAdvice.setCustomer(customer.getName());
        creditNoteAdvice.setProducer(delivery.getProducer());
        creditNoteAdvice.setCrudeName(delivery.getCrudeName());

        return creditNoteAdvice;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.application.internal;

import com.comd.creditnote.lib.v1.CreditNote;
import com.comd.creditnote.web.application.CreditNoteService;
import com.comd.creditnote.web.domain.model.CreditNoteAdvice;
import com.comd.creditnote.web.domain.model.Delivery;
import com.comd.creditnote.web.interfaces.web.CreditNoteClient;
import com.comd.creditnote.web.interfaces.web.CustomerClient;
import com.comd.creditnote.web.interfaces.web.DeliveryClient;
import com.comd.customer.lib.v1.response.Customer;
import java.util.ArrayList;
import java.util.List;
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
    public CreditNoteAdvice generateCreditNoteAdvice(String customerId, String blDate) throws Exception {
        List<com.comd.delivery.lib.v1.Delivery> deliveries = new ArrayList<>();
        CreditNoteAdvice creditNoteAdvice;

        deliveries = deliveryClient.delivery(blDate, "123456", customerId);//TODO:vessel id hardcoded
    
        if (deliveries.isEmpty()) {
            throw new Exception("No deliveries found!");
        }
        
        CreditNote creditNote = creditNoteClient.creditNoteOfDelivery(blDate, customerId);

        Customer customer = customerClient.customer(customerId);

        com.comd.delivery.lib.v1.Delivery delivery = deliveries.get(0);
        creditNoteAdvice = new CreditNoteAdvice();
        creditNoteAdvice.setAddress(customer.getAddress());
        creditNoteAdvice.setCreditNote(new com.comd.creditnote.web.domain.model.CreditNote(
                creditNote.getCreditNoteNo(),
                creditNote.getAmount()));
        creditNoteAdvice.setDelivery(
                new Delivery(
                        delivery.getBlDate(), delivery.getVesselName(), delivery.getInvoiceNumber(), delivery.getNetValue())
        );
        creditNoteAdvice.setCustomer(customer.getName());
        creditNoteAdvice.setProducer(delivery.getProducer());
        creditNoteAdvice.setCrudeName(delivery.getCrudeName());

        return creditNoteAdvice;
    }

    @Override
    public String postCreditNote(String dateStr, String vesselId, String customerId, String invoice, String creditNoteAmount) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

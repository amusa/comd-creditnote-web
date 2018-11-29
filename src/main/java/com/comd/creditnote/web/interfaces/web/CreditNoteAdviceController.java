/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.interfaces.web;

import com.comd.creditnote.web.application.CreditNoteService;
import com.comd.creditnote.web.domain.model.CreditNoteAdvice;
import com.comd.creditnote.web.interfaces.rest.exceptions.CustomerException;
import com.comd.creditnote.web.util.CreditNoteLogger;
import com.comd.creditnote.web.util.JsfUtil;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
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
    private transient CreditNoteService creditNoteService;

    private String blDate;
    private String customerId;
    private String invoiceNo;

    private CreditNoteAdvice creditNoteAdvice;

    @PostConstruct
    public void init() {

    }

    public void viewParamListener() {
        logger.log(Level.INFO, "B/L date={0}, customerId={1}", new Object[]{blDate, customerId});
        try {
            creditNoteAdvice = creditNoteService.generateCreditNoteAdvice(customerId, blDate, invoiceNo);
        } catch (Exception cex) {
            JsfUtil.addErrorMessage(cex.getMessage());
            logger.log(Level.SEVERE, null, cex);
        }
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

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

}

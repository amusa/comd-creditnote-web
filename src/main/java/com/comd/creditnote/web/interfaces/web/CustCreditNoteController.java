/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.interfaces.web;

import com.comd.creditnote.lib.v1.CreditNote;
import com.comd.creditnote.web.util.CreditNoteLogger;
import com.comd.creditnote.web.util.JsfUtil;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author maliska
 */
@Named(value = "custCNController")
@SessionScoped
public class CustCreditNoteController implements Serializable {

    @CreditNoteLogger
    @Inject
    Logger logger;

    @Inject
    private CreditNoteClient creditNoteClient;

    private String customerId;
    private List<CreditNote> creditNotes;
    private CreditNote selectedCreditNote;

    public void customerCreditNoteListener() {
        if (customerId == null) {
            JsfUtil.addErrorMessage("Customer not selected!");
            return;
        }

        try {
            creditNotes = creditNoteClient.creditNotesOfCustomer(customerId);
        } catch (Exception ex) {
            JsfUtil.addErrorMessage(ex.getMessage());
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<CreditNote> getCreditNotes() {
        return creditNotes;
    }

    public void setCreditNotes(List<CreditNote> creditNotes) {
        this.creditNotes = creditNotes;
    }

    public CreditNote getSelectedCreditNote() {
        return selectedCreditNote;
    }

    public void setSelectedCreditNote(CreditNote selectedCreditNote) {
        this.selectedCreditNote = selectedCreditNote;
    }

}

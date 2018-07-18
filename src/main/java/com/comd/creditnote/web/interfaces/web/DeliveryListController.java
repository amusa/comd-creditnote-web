/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.interfaces.web;

import com.comd.creditnote.lib.v1.CreditNote;
import com.comd.delivery.lib.v1.Delivery;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Named;
import com.comd.creditnote.web.util.CreditNoteLogger;
import com.comd.creditnote.web.util.JsfUtil;
import java.io.IOException;
import java.text.ParseException;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author maliska
 */
@Named(value = "listController")
@SessionScoped
public class DeliveryListController implements Serializable {

    @CreditNoteLogger
    @Inject
    Logger logger;

    @Inject
    private DeliveryClient deliveryClient;

    @Inject
    private CreditNoteClient creditNoteClient;

    private Date blDate;
    private String customerId;
    private String vesselId;
    private List<Delivery> deliveries;
    private Delivery selectedDelivery;
    private Double creditNoteAmount;
    private CreditNote creditNote;

    public void findDeliveries() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = blDate != null ? format.format(blDate) : null;
        logger.log(Level.INFO, "about to execute find...{0} {1} {2}", new Object[]{dateStr, vesselId, customerId});
        try {
            deliveries = getDeliveryClient().delivery(dateStr, vesselId, customerId);
            JsfUtil.addErrorMessage("Delivery fetched from SAP successfully!");
        } catch (Exception ex) {
            deliveries = null;
            JsfUtil.addErrorMessage(ex.getMessage());
            Logger.getLogger(DeliveryListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void viewCreditNoteListener(Delivery delv) {
        creditNote = null;

        if (delv == null) {
            JsfUtil.addErrorMessage("Delivery not selected");
            logger.log(Level.INFO, "VDelivery not selected");
            return;
        }

        selectedDelivery = delv;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = format.format(selectedDelivery.getBlDate());
        String custId = selectedDelivery.getCustomer();

        logger.log(Level.INFO, "Viewing Credit Note of blDate={0}, customer={1}", new Object[]{dateStr, custId});
        
        try {
            creditNote = creditNoteClient.creditNoteOfDelivery(dateStr, custId);
        } catch (Exception ex) {
            creditNote = null;
            JsfUtil.addErrorMessage(ex.getMessage());
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void postCreditNote() throws Exception {
        if (blDate == null) {
            JsfUtil.addErrorMessage("Error: B/L Date not selected");
            return;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = blDate != null ? format.format(blDate) : null;
        String invoice;

        if (selectedDelivery == null) {
            JsfUtil.addErrorMessage("Error: Delivery not selected!");
            return;
        }

        if (creditNoteAmount == null) {
            JsfUtil.addErrorMessage("Error: Credit note amount not set!");
            return;
        }

        invoice = selectedDelivery.getInvoiceNumber();
        logger.log(Level.INFO, "about to post...{0} {1} {2} {3} {4}",
                new Object[]{dateStr, vesselId, customerId, invoice, creditNoteAmount});
        try {
            String message = getCreditNoteClient().post(dateStr, vesselId, customerId, invoice, creditNoteAmount);
            JsfUtil.addErrorMessage(message);
        } catch (Exception ex) {
            JsfUtil.addErrorMessage(ex.getMessage());
            Logger.getLogger(DeliveryListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void creditNoteAdvice() throws IOException {
        logger.log(Level.INFO, "navigating to creditnoteadvice.xhtml page...");

        FacesContext.getCurrentInstance().getExternalContext()
                .redirect(String.format("invoice?customer=%s&bldate=%s", customerId, getDateAsString()));

        // return "creditnoteadvice?faces-redirect=true&includeViewParams=true";
    }

    private DeliveryClient getDeliveryClient() {
        return deliveryClient;
    }

    private CreditNoteClient getCreditNoteClient() {
        return creditNoteClient;
    }

    public Date getBlDate() {
        return blDate;
    }

    public void setBlDate(Date blDate) {
        logger.log(Level.INFO, "setting bldate {0}...", blDate);
        this.blDate = blDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        logger.log(Level.INFO, "setting customerId {0}...", customerId);
        this.customerId = customerId;
    }

    public String getVesselId() {
        return vesselId;
    }

    public void setVesselId(String vesselId) {
        logger.log(Level.INFO, "setting vesselId {0}...", vesselId);
        this.vesselId = vesselId;
    }

    public List<Delivery> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(List<Delivery> deliveries) {
        this.deliveries = deliveries;
    }

    public Delivery getSelectedDelivery() {
        return selectedDelivery;
    }

    public void setSelectedDelivery(Delivery selectedDelivery) {
        logger.log(Level.INFO, "setting selected delivery {0}", selectedDelivery);
        this.selectedDelivery = selectedDelivery;
    }

    public Double getCreditNoteAmount() {
        return creditNoteAmount;
    }

    public void setCreditNoteAmount(Double creditNoteAmount) {
        logger.log(Level.INFO, "setting credit note amount {0}", creditNoteAmount);
        this.creditNoteAmount = creditNoteAmount;
    }

    public String getDateAsString() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return blDate != null ? format.format(blDate) : null;
    }

    public void setDateAsString(String dateStr) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        this.blDate = (dateStr != null ? format.parse(dateStr) : null);
    }

    public CreditNote getCreditNote() {
        return creditNote;
    }

    public void setCreditNote(CreditNote creditNote) {
        this.creditNote = creditNote;
    }

}

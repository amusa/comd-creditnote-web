/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.interfaces.web;

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
import javax.enterprise.context.SessionScoped;

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

    private Date blDate;
    private String customerId;
    private String vesselId;
    private List<Delivery> deliveries;

    public void findDeliveries() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = blDate != null ? format.format(blDate) : null;
        logger.log(Level.INFO, "about to execute find...{0} {1} {2}", new Object[]{dateStr, vesselId, customerId});
        deliveries = getClient().delivery(dateStr, vesselId, customerId);
    }

    private DeliveryClient getClient() {
        return deliveryClient;
    }

    public Date getBlDate() {
        return blDate;
    }

    public void setBlDate(Date blDate) {
        this.blDate = blDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getVesselId() {
        return vesselId;
    }

    public void setVesselId(String vesselId) {
        this.vesselId = vesselId;
    }

    public List<Delivery> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(List<Delivery> deliveries) {
        this.deliveries = deliveries;
    }

}

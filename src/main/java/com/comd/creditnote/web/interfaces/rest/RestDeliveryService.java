/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.interfaces.rest;

import com.comd.creditnote.web.interfaces.web.DeliveryClient;
import com.comd.delivery.lib.v1.Delivery;
import com.comd.delivery.lib.v1.response.DeliveryResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.comd.creditnote.web.util.CreditNoteLogger;
import java.util.logging.Level;
import javax.ws.rs.core.GenericType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author maliska
 */
@Stateless
public class RestDeliveryService implements DeliveryClient {

    private Client client;
    private WebTarget target;

    @CreditNoteLogger
    @Inject
    Logger logger;

    @Inject
    @ConfigProperty(name = "DELIVERY_SERVICE_URL")
    private String deliveryUrl;
//    private final static String DELIVERY_SERVICE_URL = "http://localhost:8090/comd-delivery-api/v1";

    @Override
    public List<Delivery> delivery(String blDate, String vesselId, String customerId) throws Exception {
        client = ClientBuilder.newClient();
        target = client.target(deliveryUrl)
                .path("/delivery")
                .queryParam("bldate", blDate)
                .queryParam("customer", customerId)
                .queryParam("vessel", vesselId);

        Response response = target.request(MediaType.APPLICATION_JSON).get();
        List<Delivery> deliveries = new ArrayList<>();
        try {
            if (response.getStatus() == 200) {
                deliveries = response.readEntity(new GenericType<List<Delivery>>() {
                });
            } else if (response.getStatus() == 400) {
                throw new Exception("No data returned for the given selection");
            } else if (response.getStatus() == 404) {
                throw new Exception("Resource not found");
            } else if (response.getStatus() == 500) {
                throw new Exception("Internal server error!");
            } else {
                logger.log(Level.SEVERE, "could not connect to service. return code {0}", response.getStatus());
                throw new Exception("Unexpected error occured!");
            }
        } finally {
            response.close();
        }

        return deliveries;
    }

}

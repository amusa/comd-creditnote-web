/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.interfaces.rest;

import com.comd.creditnote.lib.v1.request.PostCreditNoteRequest;
import com.comd.creditnote.web.interfaces.web.CreditNoteClient;
import com.comd.creditnote.web.util.CreditNoteLogger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author maliska
 */
@Stateless
public class RestCreditNoteService implements CreditNoteClient {

    private Client client;
    private WebTarget target;

    @CreditNoteLogger
    @Inject
    Logger logger;

    // @Inject
    //ConfigProperty(name = "CREDITNOTE_SERVICE_URL")
    private final static String CREDITNOTE_SERVICE_URL = "http://localhost:8085/comd-creditnote-api/v1";

    @Override
    public String post(String blDate, String vesselId, String customerId, String invoice, double amount) throws Exception {
        client = ClientBuilder.newClient();

        PostCreditNoteRequest request = new PostCreditNoteRequest();
        request.setBlDate(blDate);
        request.setCustomerId(customerId);
        request.setVesselId(vesselId);
        request.setInvoice(invoice);
        request.setAmount(amount);

        target = client.target(CREDITNOTE_SERVICE_URL)
                .path("/creditnote");

        logger.log(Level.INFO, "Calling external webservice @ {0}", CREDITNOTE_SERVICE_URL);
        Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(request, MediaType.APPLICATION_JSON), Response.class);

        String responseMessage;
        try {
            if (response.getStatus() == 200) {
                responseMessage = response.readEntity(String.class);
                logger.log(Level.INFO, "Response received {0}", responseMessage);
            } else if (response.getStatus() == 400) {
                logger.log(Level.SEVERE, "No data returned for the given selection. return code {0}", response.getStatus());
                throw new Exception("No data returned for the given selection");
            } else if (response.getStatus() == 404) {
                logger.log(Level.SEVERE, "esource not found. return code {0}", response.getStatus());
                throw new Exception("Resource not found");
            } else if (response.getStatus() == 500) {
                logger.log(Level.SEVERE, "Internal server error!. return code {0}", response.getStatus());
                throw new Exception("Internal server error!");
            } else {
                logger.log(Level.SEVERE, "could not connect to service. return code {0}", response.getStatus());
                throw new Exception("Unexpected error occured!");
            }
        } finally {
            response.close();
        }

        return responseMessage;
    }

}

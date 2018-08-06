/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.interfaces.rest;

import com.comd.creditnote.lib.v1.CreditNote;
import com.comd.creditnote.lib.v1.request.PostCreditNoteRequest;
import com.comd.creditnote.web.interfaces.rest.exceptions.CustomerException;
import com.comd.creditnote.web.interfaces.web.CreditNoteClient;
import com.comd.creditnote.web.util.CreditNoteLogger;
import com.comd.customer.lib.v1.response.Customer;
import com.comd.customer.lib.v1.response.CustomerResponse;
import com.comd.delivery.lib.v1.Delivery;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

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

     @Inject
    @ConfigProperty(name = "CREDITNOTE_SERVICE_URL")
     private String creditNoteServiceUrl;
//    private final static String CREDITNOTE_SERVICE_URL = "http://localhost:8085/comd-creditnote-api/v1";

    @Override
    public String post(String blDate, String vesselId, String customerId, String invoice, double amount) throws Exception {
        client = ClientBuilder.newClient();

        PostCreditNoteRequest request = new PostCreditNoteRequest();
        request.setBlDate(blDate);
        request.setCustomerId(customerId);
        request.setVesselId(vesselId);
        request.setInvoice(invoice);
        request.setAmount(amount);

        target = client.target(creditNoteServiceUrl)
                .path("/creditnote");

        logger.log(Level.INFO, "Calling external webservice @ {0}", creditNoteServiceUrl);
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

    @Override
    public CreditNote creditNoteOfDelivery(String blDate, String customerId) throws Exception {
        client = ClientBuilder.newClient();
        target = client.target(creditNoteServiceUrl)
                .path("/creditnote")
                .queryParam("bldate", blDate)
                .queryParam("customer", customerId);

        logger.log(Level.INFO, "Invoking credit note service endpoint: {0}", target.getUri());

        Response response = target.request(MediaType.APPLICATION_JSON).get();
        CreditNote creditNote;
        try {
            if (response.getStatus() == 200) {
                creditNote = response.readEntity(CreditNote.class);
            } else if (response.getStatus() == 400) {
                logger.log(Level.SEVERE, "No data returned for the given selection. Return code: {0}", response.getStatus());
                throw new Exception("No data returned for the given selection");
            } else if (response.getStatus() == 404) {
                logger.log(Level.SEVERE, "Resource not found. Return code: {0}", response.getStatus());
                throw new Exception("Resource not found");
            } else if (response.getStatus() == 500) {
                logger.log(Level.SEVERE, "Internal server error! Return code: {0}", response.getStatus());
                throw new Exception("Internal server error!");
            } else {
                logger.log(Level.SEVERE, "Unexpected error occured! return code: {0}", response.getStatus());
                throw new Exception("Unexpected error occured!");
            }
        } finally {
            response.close();
        }

        return creditNote;
    }

    @Override
    public List<CreditNote> creditNotesOfCustomer(String customerId) throws Exception {
        client = ClientBuilder.newClient();
        target = client.target(creditNoteServiceUrl)
                .path("/creditnote/customer")
                .path(customerId);

        logger.log(Level.INFO, "Invoking credit note service endpoint: {0}", target.getUri());

        Response response = target.request(MediaType.APPLICATION_JSON).get();
        List<CreditNote> creditNotes;
        try {
            if (response.getStatus() == 200) {            
                creditNotes = response.readEntity(new GenericType<List<CreditNote>>() {
                });
            } else if (response.getStatus() == 400) {
                logger.log(Level.SEVERE, "No data returned for the given selection. Return code: {0}", response.getStatus());
                throw new Exception("No data returned for the given selection");
            } else if (response.getStatus() == 404) {
                logger.log(Level.SEVERE, "Resource not found. Return code: {0}", response.getStatus());
                throw new Exception("Resource not found");
            } else if (response.getStatus() == 500) {
                logger.log(Level.SEVERE, "Internal server error! Return code: {0}", response.getStatus());
                throw new Exception("Internal server error!");
            } else {
                logger.log(Level.SEVERE, "Unexpected error occured! return code: {0}", response.getStatus());
                throw new Exception("Unexpected error occured!");
            }
        } finally {
            response.close();
        }

        return creditNotes;
    }

}

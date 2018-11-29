/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.interfaces.rest;

import com.comd.creditnote.web.interfaces.rest.exceptions.CustomerException;
import com.comd.creditnote.web.interfaces.web.CustomerClient;
import com.comd.delivery.lib.v1.Delivery;
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
import com.comd.customer.lib.v1.response.Customer;
import com.comd.customer.lib.v1.response.CustomerListResponse;
import com.comd.customer.lib.v1.response.CustomerResponse;
import java.util.logging.Level;
import javax.ws.rs.core.GenericType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author maliska
 */
@Stateless
public class RestCustomerService implements CustomerClient {

    private Client client;
    private WebTarget target;

    @CreditNoteLogger
    @Inject
    Logger logger;

    @Inject
    @ConfigProperty(name = "CUSTOMER_SERVICE_URL")
    private String customerServiceUrl;
//    private final static String CUSTOMER_SERVICE_URL = "http://localhost:9090/comd-customer-api/v1";

    @Override
    public Customer customer(String customerNumber) throws CustomerException {
        client = ClientBuilder.newClient();
        target = client.target(customerServiceUrl)
                .path("/customer/")
                .path(customerNumber);

        logger.log(Level.INFO, "Invoking customer service endpoint: {0}", target.getUri());

        Response response = target.request(MediaType.APPLICATION_JSON).get();
        Customer customer;
        try {
            if (response.getStatus() == 200) {
                CustomerResponse customerResponse = response.readEntity(CustomerResponse.class);
                customer = customerResponse.getCustomer();
            } else if (response.getStatus() == 400) {
                logger.log(Level.SEVERE, "No data returned for the given selection. Return code: {0}", response.getStatus());
                throw new CustomerException("No data returned for the given selection");
            } else if (response.getStatus() == 404) {
                logger.log(Level.SEVERE, "Resource not found. Return code: {0}", response.getStatus());
                throw new CustomerException("Resource not found");
            } else if (response.getStatus() == 500) {
                logger.log(Level.SEVERE, "Internal server error! Return code: {0}", response.getStatus());
                throw new CustomerException("Internal server error!");
            } else {
                logger.log(Level.SEVERE, "Unexpected error occured! return code: {0}", response.getStatus());
                throw new CustomerException("Unexpected error occured!");
            }
        } finally {
            response.close();
        }

        return customer;
    }

    @Override
    public List<Customer> customers() throws CustomerException {
        client = ClientBuilder.newClient();
        target = client.target(customerServiceUrl)
                .path("/customer/");

        logger.log(Level.INFO, "Invoking customer service endpoint: {0}", target.getUri());

        Response response = target.request(MediaType.APPLICATION_JSON).get();
        List<Customer> customers = new ArrayList<>();
        try {
            if (response.getStatus() == 200) {
                CustomerListResponse customerResponse = response.readEntity(CustomerListResponse.class);
                customers = customerResponse.getCustomerList();
            } else if (response.getStatus() == 400) {
                logger.log(Level.SEVERE, "No data returned for the given selection. Return code: {0}", response.getStatus());
                throw new CustomerException("No data returned for the given selection");
            } else if (response.getStatus() == 404) {
                logger.log(Level.SEVERE, "Resource not found. Return code: {0}", response.getStatus());
                throw new CustomerException("Resource not found");
            } else if (response.getStatus() == 500) {
                logger.log(Level.SEVERE, "Internal server error! Return code: {0}", response.getStatus());
                throw new CustomerException("Internal server error!");
            } else {
                logger.log(Level.SEVERE, "Unexpected error occured!. return code {0}", response.getStatus());
                throw new CustomerException("Unexpected error occured!");
            }
        } finally {
            response.close();
        }

        return customers;
    }

}

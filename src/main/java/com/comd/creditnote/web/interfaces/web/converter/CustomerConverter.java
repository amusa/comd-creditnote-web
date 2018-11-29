/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.interfaces.web.converter;

import com.comd.creditnote.web.interfaces.rest.exceptions.CustomerException;
import com.comd.creditnote.web.interfaces.web.CustomerClient;
import com.comd.customer.lib.v1.response.Customer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author maliska
 */
@Named
@ApplicationScoped
public class CustomerConverter implements Converter {

    private static final Logger LOG = Logger.getLogger(CustomerConverter.class.getName());

    @Inject
    private CustomerClient customerClient;

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
        if (value == null || value.length() == 0) {
            return null;
        }

        try {
            LOG.log(Level.INFO, "--- retrieving customer of id:{0} ---", value);
            return customerClient.customer(value);
        } catch (CustomerException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        return null;
    }

    java.lang.Long getKey(String value) {
        java.lang.Long key;
        key = Long.valueOf(value);
        return key;
    }

    String getStringKey(java.lang.Long value) {
        StringBuilder sb = new StringBuilder();
        sb.append(value);
        return sb.toString();
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Customer) {
            Customer o = (Customer) object;
            return o.getNumber();
        } else {
            throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Customer.class.getName());
        }
    }

}

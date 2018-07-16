/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.interfaces.web;

import com.comd.creditnote.web.interfaces.rest.exceptions.CustomerException;
import com.comd.customer.lib.v1.response.Customer;
import java.util.List;

/**
 *
 * @author maliska
 */
public interface CustomerClient {

    Customer customer(String customerNumber) throws CustomerException;

    List<Customer> customers() throws CustomerException;
}

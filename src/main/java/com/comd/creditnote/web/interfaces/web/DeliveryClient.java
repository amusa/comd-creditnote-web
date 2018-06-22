/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.interfaces.web;

import com.comd.delivery.lib.v1.Delivery;
import java.util.List;

/**
 *
 * @author maliska
 */
public interface DeliveryClient {
    List<Delivery> delivery(String blDate, String vesselId, String customerId) throws Exception;
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.interfaces.web;

/**
 *
 * @author maliska
 */
public interface CreditNoteClient {

    String post(String blDate, String vesselId, String customerId, String invoice, double amount) throws Exception;

}

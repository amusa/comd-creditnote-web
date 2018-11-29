/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.application;

import com.comd.creditnote.web.domain.model.CreditNoteAdvice;

/**
 *
 * @author maliska
 */
public interface CreditNoteService {
    CreditNoteAdvice generateCreditNoteAdvice(String customerId, String blDate, String invoiceNo) throws Exception;
    String postCreditNote(String dateStr, String vesselId, String customerId, String invoice, String creditNoteAmount);
}

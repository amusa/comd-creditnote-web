/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.interfaces.web;

import com.comd.creditnote.web.util.CreditNoteLogger;
import com.comd.creditnote.web.util.JsfUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author maliska
 */
@Named
@SessionScoped
public class AppController implements Serializable {

    @CreditNoteLogger
    @Inject
    Logger logger;

    public void logout() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();

        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();

        try {
            request.logout();
            logger.log(Level.INFO, "--- Logout successfully ---");
        } catch (ServletException ex) {
            JsfUtil.addErrorMessage("Logout Error: " + ex.getMessage());
            logger.log(Level.SEVERE, null, ex);
        }

        logger.log(Level.INFO, "--- redirecting to {0} ---", externalContext.getRequestContextPath());
        externalContext.redirect(externalContext.getRequestContextPath());
    }
}

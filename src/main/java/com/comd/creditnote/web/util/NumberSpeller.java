/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.util;

import com.ibm.icu.text.RuleBasedNumberFormat;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;

/**
 *
 * @author maliska
 */
public class NumberSpeller {
    
    public String toWords(BigDecimal num){
        String output = "";
        BigDecimal dec = num.subtract(num.setScale(0, RoundingMode.FLOOR)).setScale(2, RoundingMode.HALF_DOWN);
        Locale locale = Locale.ENGLISH;

        RuleBasedNumberFormat formatter = new RuleBasedNumberFormat(locale, RuleBasedNumberFormat.SPELLOUT);
        BigDecimal it =num.setScale(0, RoundingMode.FLOOR);
        String integerPart = formatter.format(it);
        Currency theCurrency = Currency.getInstance("USD");
       

        dec = new BigDecimal(dec.toString().replace(".", ""));
        if (dec.compareTo(BigDecimal.ZERO) == 1) {
            String fractionalPart = formatter.format(dec);

            output = integerPart + " " + theCurrency.getDisplayName() + " " + fractionalPart + " Cents";
        } else {
            output = integerPart;
        }
        return output;
    }
    
}

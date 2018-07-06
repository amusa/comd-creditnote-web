/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.interfaces.web;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ayemi
 */
public class PdfWatermarkHelper extends PdfPageEventHelper {

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        //super.onEndPage(writer, document);
        try {
            Image img = Image.getInstance(getResource("/nnpc-logo-bg.jpg"));

            img.scaleAbsolute(400.0f, 400.0f);

            float width = document.getPageSize().getWidth();
            float height = document.getPageSize().getHeight();
            float w = img.getScaledWidth();
            float h = img.getScaledHeight();

            //            img.setAbsolutePosition(95, 230);
            img.setAbsolutePosition((width - w) / 2, (height - h) / 2); //centralize watermark logo

            // transparency
            PdfGState gs1 = new PdfGState();
            gs1.setFillOpacity(0.1f);
            gs1.setBlendMode(PdfGState.BM_SCREEN);
            writer.setRgbTransparencyBlending(true);
            writer.getDirectContentUnder().setGState(gs1);
            writer.getDirectContentUnder().addImage(img, false);
        } catch (DocumentException ex) {

        } catch (MalformedURLException ex) {

        } catch (IOException ex) {

        }
    }

    private static URL getResource(String name) {
        return PdfWatermarkHelper.class.getResource(name);
    }
}

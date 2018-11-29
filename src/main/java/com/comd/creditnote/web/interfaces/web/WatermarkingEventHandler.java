/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.interfaces.web;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.element.Image;
import java.net.URL;

/**
 *
 * @author Ayemi
 */
public class WatermarkingEventHandler implements IEventHandler {

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdfDoc = docEvent.getDocument();
        PdfPage page = docEvent.getPage();

        // image watermark
        ImageData imageData = ImageDataFactory.create(getResource("/nnpc-logo-bg.jpg"));
        Image img = new Image(imageData);
        img.scaleAbsolute(400.0f, 400.0f);
        //  Implement transformation matrix usage in order to scale image
        float width = page.getPageSize().getWidth();
        float height = page.getPageSize().getHeight();

        float w = img.getImageScaledWidth();
        float h = img.getImageScaledHeight();

        img.setFixedPosition((width - w) / 2, (height - h) / 2); //centralize watermark logo

        // transparency
        PdfExtGState gs1 = new PdfExtGState();
        gs1.setFillOpacity(0.1f);
        gs1.setBlendMode(PdfExtGState.BM_SCREEN);
        // properties
        PdfCanvas over;
        Rectangle pagesize;
        float x, y;
        // loop over every page

        pagesize = page.getPageSizeWithRotation();
        page.setIgnorePageRotationForContent(true);

        x = (pagesize.getLeft() + pagesize.getRight()) / 2;
        y = (pagesize.getTop() + pagesize.getBottom()) / 2;
        over = new PdfCanvas(page);
        over.saveState();
        over.setExtGState(gs1);

        over.addImage(imageData, w, 0, 0, h, x - (w / 2), y - (h / 2), false);

        over.restoreState();

    }

    private static URL getResource(String name) {
        return WatermarkingEventHandler.class
                .getResource(name);
    }

}

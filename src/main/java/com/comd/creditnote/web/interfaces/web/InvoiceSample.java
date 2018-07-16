/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.interfaces.web;

import com.comd.creditnote.web.application.CreditNoteService;
import com.comd.creditnote.web.domain.model.CreditNoteAdvice;
import com.comd.creditnote.web.interfaces.rest.exceptions.CustomerException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Ayemi
 */
@WebServlet(name = "Invoice", urlPatterns = {"/faces/invoice"})
public class InvoiceSample extends HttpServlet {

    public static final String ARIAL_BLACK = "fonts/arial_black.ttf";
    private static Font midBold = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
    private static Font subBold = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private static Font smallReg = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
    private static Font smallBold2 = new Font(Font.FontFamily.COURIER, 12, Font.BOLD);

    @Inject
    private CreditNoteService creditNoteService;

    private static CreditNoteAdvice creditNoteAdvice;

    ByteArrayOutputStream baos;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, DocumentException, CustomerException {
        response.setContentType("text/html;charset=UTF-8");

        String customerId = request.getParameter("customer");
        String blDateAsString = request.getParameter("bldate");

        loadCreditNoteAdvice(customerId, blDateAsString);

        try {

            Document layoutDocument = new Document(PageSize.A4);
            baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(layoutDocument, baos);
            writer.setPageEvent(new PdfWatermarkHelper());

            layoutDocument.open();
            addMetaData(layoutDocument);

            addEmptyLine(layoutDocument, 5);

            addCustomerReference(layoutDocument);

            addEmptyLine(layoutDocument, 8);

            // title
            addTitle(layoutDocument);

            addEmptyLine(layoutDocument, 2);

            addBody(layoutDocument);

            addEmptyLine(layoutDocument, 2);

            addTable(layoutDocument);

            addEmptyLine(layoutDocument, 4);

            addFooter(layoutDocument);

            addEmptyLine(layoutDocument, 4);

            addSignature(layoutDocument);

            layoutDocument.close();

            setResponseHeaders(response);

            OutputStream os = response.getOutputStream();
            baos.writeTo(os);
            os.flush();
            os.close();
        } catch (DocumentException e) {
            throw new IOException(e.getMessage());
        }
    }

    private void addMetaData(Document document) {
        document.addTitle("Credit Note Advice");
        document.addSubject("Credit Note Advice");
        document.addKeywords("Credit Note, Demurrage, Claim");
        document.addAuthor("COMD");
        document.addCreator("COMD");
        document.addCreationDate();
    }

    private void setResponseHeaders(HttpServletResponse response) {
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control",
                "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setContentType("application/pdf");
        response.setContentLength(baos.size());
    }

    public void addTable(Document layoutDocument) throws DocumentException {

        PdfPTable table = new PdfPTable(5);
        table.setWidths(new float[]{110f, 110f, 110f, 110f, 50f});
        table.setWidthPercentage(100);
        // headers

//        PdfPCell c1 = new PdfPCell(new Phrase("JV COMPANY", midBold));
//        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
//        table.addCell(c1);
        table.addCell(new Paragraph("BL Date", smallBold));
        table.addCell(new Paragraph("Producer", smallBold));
        table.addCell(new Paragraph("Crude Type", smallBold));
        table.addCell(new Paragraph("Amount", smallBold));
        table.addCell(new Paragraph(""));

        // items        
        table.addCell(new Paragraph("11/27/2014", smallReg));
        table.addCell(new Paragraph("SPDC", smallReg));
        table.addCell(new Paragraph("FB", smallReg));
        table.addCell(new Paragraph("6,570", smallReg));
        table.addCell(new Paragraph("90", smallReg));

        layoutDocument.add(table);
    }

    public static void addCustomerReference(Document layoutDocument) throws DocumentException, IOException {
        Paragraph reference = new Paragraph();

        BaseFont bf = BaseFont.createFont(ARIAL_BLACK, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font f2 = new Font(bf, 14, Font.BOLD);

        Paragraph line1 = new Paragraph(creditNoteAdvice.getCustomer(), f2);
        line1.setAlignment(Element.ALIGN_LEFT);
        line1.setMultipliedLeading(0.2f);
        reference.add(line1);

        addEmptyLine(reference, 1);

        Paragraph line2 = new Paragraph(creditNoteAdvice.getAddress().getStreet(), f2);
        line1.setMultipliedLeading(0.2f);
        reference.add(line2);

        addEmptyLine(reference, 1);

        Paragraph line3 = new Paragraph(String.format("%s, %s",
                new Object[]{
                    creditNoteAdvice.getAddress().getCity(),
                    creditNoteAdvice.getAddress().getState()
                }), f2);
        line1.setMultipliedLeading(0.2f);
        reference.add(line3);

        layoutDocument.add(reference);
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private void addEmptyLine(Document layoutDocument, int number) {
        for (int i = 0; i < number; i++) {
            try {
                layoutDocument.add(new Paragraph(" "));
            } catch (DocumentException ex) {
                Logger.getLogger(InvoiceSample.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void addTitle(Document layoutDocument) throws DocumentException, IOException {
        // layoutDocument.add(new Paragraph("RETAIL INVOICE").setFont(Font.BOLD).setUnderline().setTextAlignment(TextAlignment.CENTER));

        BaseFont bf = BaseFont.createFont(ARIAL_BLACK, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font f1 = new Font(bf, 16, Font.BOLD);

        Paragraph title = new Paragraph("CREDIT NOTE NO: S-94/2018", f1);
        title.setAlignment(Element.ALIGN_CENTER);
        layoutDocument.add(title);
    }

    public static void addBody(Document layoutDocument) throws DocumentException {
        Paragraph body = new Paragraph();
        body.setAlignment(Element.ALIGN_JUSTIFIED);
        body.add(new Chunk("To ", smallReg));
        body.add(new Chunk("CREDIT ", smallBold));
        body.add(new Chunk("you with the sum of ", smallReg));
        body.add(new Chunk("(US$6,570.90) Six Thousand, Five Hundred and Seventy Dollars, Ninety Cents ", smallBold));
        body.add(new Chunk("being the amount due to you as ", smallReg));
        body.add(new Chunk("demurrage claims ", smallBold));
        body.add(new Chunk("from Vessel RIDGEBURY ASTARI with our Invoice Ref: COS/11/113/2014", smallReg));
        layoutDocument.add(body);
    }

    private void loadCreditNoteAdvice(String customerId, String blDateAsString) throws CustomerException {
        creditNoteAdvice = creditNoteService.generateCreditNoteAdvice(customerId, blDateAsString);
    }

    private void addFooter(Document layoutDocument) throws DocumentException {
        Paragraph footer = new Paragraph();
        Paragraph line1 = new Paragraph("Yours faithfully,", smallReg);
        footer.add(line1);

        Paragraph line2 = new Paragraph("For: NIGERIAN NATIONAL PETROLEUM CORPERATION", smallBold);
        footer.add(line2);

        layoutDocument.add(footer);
    }

    private void addSignature(Document layoutDocument) throws DocumentException, IOException {
        BaseFont bf = BaseFont.createFont(ARIAL_BLACK, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font f3 = new Font(bf, 12, Font.BOLD);

        Paragraph signature = new Paragraph();
        Paragraph sign1 = new Paragraph("ABAH, A. L. (MRS)", f3);
        signature.add(sign1);
        Paragraph sign2 = new Paragraph("FOR: GGM, COMD", f3);
        signature.add(sign2);

        layoutDocument.add(signature);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (CustomerException ex) {
            Logger.getLogger(InvoiceSample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (CustomerException ex) {
            Logger.getLogger(InvoiceSample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

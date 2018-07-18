/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comd.creditnote.web.interfaces.web;

import com.comd.creditnote.web.application.CreditNoteService;
import com.comd.creditnote.web.domain.model.CreditNoteAdvice;
import com.comd.creditnote.web.interfaces.rest.exceptions.CustomerException;
import com.comd.creditnote.web.util.NumberSpeller;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.text.WordUtils;

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
    private static Font smallBoldUnderline = FontFactory.getFont(
            BaseFont.TIMES_BOLD, 12, Font.UNDERLINE);

    @Inject
    private CreditNoteService creditNoteService;

    private static CreditNoteAdvice creditNoteAdvice;

    ByteArrayOutputStream baos;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, DocumentException, CustomerException, Exception {
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
        table.setWidths(new float[]{110f, 110f, 100f, 120f, 50f});

        table.setWidthPercentage(95);
        table.getDefaultCell().setBorderWidth(0f);

        table.addCell(getCell("BL Date", smallBoldUnderline));
        table.addCell(getCell("Producer", smallBoldUnderline));
        table.addCell(getCell("Crude Type", smallBoldUnderline, PdfPCell.ALIGN_CENTER));
        table.addCell(getCell("Amount (US $)", smallBold, PdfPCell.ALIGN_CENTER));
        table.addCell(getEmptyCell());

        // items        
        table.addCell(getCell(creditNoteAdvice.getDelivery().dateAsString(), smallReg));
        table.addCell(getCell(creditNoteAdvice.getProducer(), smallReg));
        table.addCell(getCell(creditNoteAdvice.getCrudeName(), smallReg, PdfPCell.ALIGN_CENTER));
        table.addCell(getCell(String.format("%,d", creditNoteAdvice.getCreditNote().getAmount().getIntValue().intValue()), smallReg, PdfPCell.ALIGN_CENTER));
        table.addCell(getCell(String.format("%02d", creditNoteAdvice.getCreditNote().getAmount().getDecimalValue().intValue()), smallReg, PdfPCell.ALIGN_RIGHT));

        layoutDocument.add(table);
    }

    public static void addCustomerReference(Document layoutDocument) throws DocumentException, IOException {

        BaseFont bf = BaseFont.createFont(ARIAL_BLACK, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font f2 = new Font(bf, 14, Font.BOLD);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100f);

        table.addCell(getCell(creditNoteAdvice.getCustomer(), f2, PdfPCell.ALIGN_LEFT));
        table.addCell(getCell(String.format("CREDIT NOTE NO: %s", creditNoteAdvice.getCreditNote().getNumber().substring(0, 10)), f2, PdfPCell.ALIGN_RIGHT));

        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());

        table.addCell(getCell(creditNoteAdvice.getAddress().getStreet(), f2, PdfPCell.ALIGN_LEFT));
        table.addCell(getEmptyCell());

        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());

        table.addCell(getCell(String.format("%s, %s",
                new Object[]{
                    creditNoteAdvice.getAddress().getCity(),
                    creditNoteAdvice.getAddress().getState()
                }), f2, PdfPCell.ALIGN_LEFT));
        table.addCell(getCell(String.format("%tD", LocalDate.now()),
                f2, PdfPCell.ALIGN_RIGHT));

        layoutDocument.add(table);

    }

    private static PdfPCell getCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    private static PdfPCell getCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(0);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    private static PdfPCell getEmptyCell() {
        PdfPCell cell = new PdfPCell(new Phrase(" "));
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
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
        BaseFont bf = BaseFont.createFont(ARIAL_BLACK, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font f1 = new Font(bf, 16, Font.BOLD);
        String pTitle = String.format("CREDIT NOTE NO: %s", creditNoteAdvice.getCreditNote().getNumber());
        Paragraph title = new Paragraph(pTitle, f1);
        title.setAlignment(Element.ALIGN_CENTER);
        layoutDocument.add(title);
    }

    public static void addBody(Document layoutDocument) throws DocumentException {
        NumberSpeller converter = new NumberSpeller();
        //MoneyConverters converter = MoneyConverters.ENGLISH_BANKING_MONEY_VALUE;

        BigDecimal amount = creditNoteAdvice.getCreditNote().getAmount().getValue();

        String moneyInWords = converter.toWords(new BigDecimal(amount.doubleValue()));
        String moneyChunk = String.format("US$(%,.2f) %s ", amount.doubleValue(), WordUtils.capitalizeFully(moneyInWords));

        Paragraph body = new Paragraph();
        body.setAlignment(Element.ALIGN_LEFT);
        body.setIndentationRight(70);
        body.add(new Chunk("To ", smallReg));
        body.add(new Chunk("CREDIT ", smallBold));
        body.add(new Chunk("you with the sum of ", smallReg));
        body.add(new Chunk(moneyChunk, smallBold));
        body.add(new Chunk("being the amount due to you as ", smallReg));
        body.add(new Chunk("demurrage claims ", smallBold));

        String lastChunk = String.format("from Vessel %s with our Invoice Ref: %s",
                creditNoteAdvice.getDelivery().getVesselName(),
                creditNoteAdvice.getDelivery().getInvoiceNumber());

        body.add(new Chunk(lastChunk, smallReg));

        layoutDocument.add(body);
    }

    private void loadCreditNoteAdvice(String customerId, String blDateAsString) throws Exception {
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

        PdfPTable signTable = new PdfPTable(2);
        signTable.setWidthPercentage(100);

        signTable.addCell(getCell("ABAH, A. L. (MRS)", f3));
        signTable.addCell(getCell("AMINA M. MOHAMMED", f3));

        signTable.addCell(getCell("FOR: GGM, COMD", f3));
        signTable.addCell(getCell("SUPVR. C&P RECON. COMD", f3));

        layoutDocument.add(signTable);
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
        } catch (Exception ex) {
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
        } catch (Exception ex) {
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

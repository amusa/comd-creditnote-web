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
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
@WebServlet(name = "CreditNoteAdvice", urlPatterns = {"/faces/creditNoteAdvice"})
public class CreditNoteAdviceForm extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(CreditNoteAdviceForm.class.getName());

    public static final String ARIAL_BLACK = "fonts/arial_black.ttf";

    @Inject
    private CreditNoteService creditNoteService;

    private static CreditNoteAdvice creditNoteAdvice;

    ByteArrayOutputStream baos;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, CustomerException, Exception {
        response.setContentType("text/html;charset=UTF-8");

        String customerId = request.getParameter("customer");
        String blDateAsString = request.getParameter("bldate");
        String invoiceNo = request.getParameter("invoice");

        loadCreditNoteAdvice(customerId, blDateAsString, invoiceNo);

        LOG.log(Level.INFO, "--- Setting up document. Page size {0} x {1} ---",
                new Object[]{PageSize.A4.getWidth(), PageSize.A4.getHeight()});

        OutputStream os = response.getOutputStream();

        PdfWriter writer = new PdfWriter(os);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document layoutDocument = new Document(pdfDoc, new PageSize(PageSize.A4));
        layoutDocument.setMargins(0, 15, 0, 80);

        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new WatermarkingEventHandler());

        addMetaData(pdfDoc);

        addEmptyLine(layoutDocument, 11);

        addCustomerReference(layoutDocument);

        addEmptyLine(layoutDocument, 11);

        // title
        addTitle(layoutDocument);

        addEmptyLine(layoutDocument, 2);

        addBody(layoutDocument);

        addEmptyLine(layoutDocument, 3);

        addTable(layoutDocument);

        addEmptyLine(layoutDocument, 3);

        addFooter(layoutDocument);

        addEmptyLine(layoutDocument, 7);

        addSignature(layoutDocument);

        setResponseHeaders(response);

        layoutDocument.close();

        pdfDoc.close();
    }

    private void addMetaData(PdfDocument document) {
        LOG.log(Level.FINE, "--- adding document metadata ---");
        document.getDocumentInfo().setTitle("Credit Note Advice");
        document.getDocumentInfo().setSubject("Credit Note Advice");
        document.getDocumentInfo().setKeywords("Credit Note, Demurrage, Claim");
        document.getDocumentInfo().setAuthor("COMD");
        document.getDocumentInfo().setCreator("COMD");
        document.getDocumentInfo().addCreationDate();
    }

    private void setResponseHeaders(HttpServletResponse response) {
        LOG.log(Level.FINE, "--- setting response headers ---");
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control",
                "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setContentType("application/pdf");
        //response.setContentLength(baos.size());
    }

    public void addTable(Document layoutDocument) {
//        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{5,5,5}));
//headerTable.setWidth(UnitValue.createPercentValue(100));

        LOG.log(Level.FINE, "--- adding credit note table ---");
        float[] columnWidths = {1.5f, 2f, 2.5f, 2.5f, 1f};
        Table table = new Table(columnWidths);

        //table.setWidthPercent(100);
        table.useAllAvailableWidth();
        //table.getDefaultCell().setBorderWidth(0f);
        table.addCell(getCell("BL Date", getSmallBoldUnderlineStyle()));
        table.addCell(getCell("Producer", getSmallBoldUnderlineStyle()));
        table.addCell(getCell("Crude Type", getSmallBoldUnderlineStyle(), TextAlignment.CENTER));
        //table.addCell(getCell("Amount (US $)", smallBold, PdfPCell.ALIGN_CENTER));
        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());

        // items        
        table.addCell(getCell(creditNoteAdvice.getDelivery().dateAsString(), getSmallRegularStyle()));
        table.addCell(getCell(creditNoteAdvice.getProducer(), getSmallRegularStyle()));
        table.addCell(getCell(creditNoteAdvice.getCrudeName(), getSmallRegularStyle(), TextAlignment.CENTER));
        table.addCell(getCell(String.format("%,d", creditNoteAdvice.getCreditNote().getAmount().getIntValue().intValue()), getSmallRegularStyle(), TextAlignment.RIGHT));
        table.addCell(getCell(String.format("%02d", creditNoteAdvice.getCreditNote().getAmount().getDecimalValue().intValue()), getSmallRegularStyle(), TextAlignment.RIGHT));

        layoutDocument.add(table);
    }

    public void addCustomerReference(Document layoutDocument) throws IOException {
        LOG.log(Level.FINE, "--- adding customer reference ---");
        //PdfFont font = PdfFontFactory.createFont(ARIAL_BLACK, PdfEncodings.IDENTITY_H, true);
        Style normalBoldStyle = getSmallBoldStyle();

        //normalBoldStyle.setFont(font).setFontSize(12);
        //Font f2 = new Font(bf, 12, Font.BOLD);
        Table table = new Table(2);
        table.useAllAvailableWidth();

        table.addCell(getCell(creditNoteAdvice.getCustomer(), normalBoldStyle, TextAlignment.LEFT));
        table.addCell(getCell(String.format("CREDIT NOTE NO: %s", creditNoteAdvice.getCreditNote().getNumber().substring(0, 10)), normalBoldStyle, TextAlignment.RIGHT));

        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());

        table.addCell(getCell(creditNoteAdvice.getAddress().getStreet(), normalBoldStyle, TextAlignment.LEFT));
        table.addCell(getEmptyCell());

        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());

        table.addCell(getCell(String.format("%s, %s",
                new Object[]{
                    creditNoteAdvice.getAddress().getCity(),
                    creditNoteAdvice.getAddress().getState()
                }), normalBoldStyle, TextAlignment.LEFT));
        table.addCell(getCell(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                normalBoldStyle, TextAlignment.RIGHT));

        layoutDocument.add(table);

    }

    private static Cell getCell(String text, Style style, TextAlignment alignment) {
        Cell cell = new Cell()
                .add(new Paragraph(text))
                .addStyle(style)
                .setPadding(0)
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(alignment);

        return cell;
    }

    private static Cell getCell(String text, Style style) {
        Cell cell = new Cell()
                .add(new Paragraph(text))
                .addStyle(style)
                .setPadding(0)
                .setBorder(Border.NO_BORDER)
                .setHorizontalAlignment(HorizontalAlignment.LEFT);

        return cell;
    }

    private static Cell getEmptyCell() {
        Cell cell = new Cell()
                .setBorder(Border.NO_BORDER);
        return cell;
    }

    private void addEmptyLine(Document layoutDocument, int number) {
        for (int i = 0; i < number; i++) {
            layoutDocument.add(new Paragraph(" "));
        }
    }

    public void addTitle(Document layoutDocument) throws IOException {
        LOG.log(Level.FINE, "--- adding document title ---");

        //PdfFont font = PdfFontFactory.createFont(ARIAL_BLACK, PdfEncodings.IDENTITY_H, true);
        Style style = getMidBoldStyle();
        //style.setFont(font).setFontSize(14);

        String pTitle = String.format("CREDIT NOTE NO: %s", creditNoteAdvice.getCreditNote().getNumber());
        Paragraph title = new Paragraph(pTitle)
                .addStyle(style)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setWidth(360);
        layoutDocument.add(title);
    }

    public void addBody(Document layoutDocument) {
        LOG.log(Level.FINE, "--- adding main body content ---");
        NumberSpeller converter = new NumberSpeller();
        //MoneyConverters converter = MoneyConverters.ENGLISH_BANKING_MONEY_VALUE;

        BigDecimal amount = creditNoteAdvice.getCreditNote().getAmount().getValue();

        String moneyInWords = converter.toWords(new BigDecimal(amount.doubleValue()));
//        String moneyText = String.format("US$(%,.2f) %s ", amount.doubleValue(), WordUtils.capitalizeFully(moneyInWords));
        String moneyText = String.format("US$(%,.2f) %s ", amount.doubleValue(), moneyInWords);
        Paragraph body = new Paragraph();
        body.setWidth(360);

        //body.setLeading(18, 2);
        body.setHorizontalAlignment(HorizontalAlignment.LEFT);
        //body.setIndentationRight(70);
        body.add(new Text("To ").addStyle(getSmallRegularStyle()));
        body.add(new Text("CREDIT ").addStyle(getSmallBoldStyle()));
        body.add(new Text("you with the sum of ").addStyle(getSmallRegularStyle()));
        body.add(new Text(moneyText).addStyle(getSmallBoldStyle()));
        body.add(new Text("being the amount due to you as ").addStyle(getSmallRegularStyle()));
        body.add(new Text("demurrage claims ").addStyle(getSmallBoldStyle()));

        String lastText = String.format("from Vessel %s with our Invoice Ref: %s",
                creditNoteAdvice.getDelivery().getVesselName(),
                creditNoteAdvice.getDelivery().getInvoiceRef());

        body.add(new Text(lastText).addStyle(getSmallRegularStyle()));

//        PdfPTable table = new PdfPTable(2);
//        table.setWidths(new float[]{200f, 50f});
//
//        table.setWidthPercentage(100);
//        table.getDefaultCell().setBorderWidth(0f);
//
//        table.addCell(body);
//        table.addCell(getEmptyCell());
        layoutDocument.add(body);
    }

    private void loadCreditNoteAdvice(String customerId, String blDateAsString, String invoiceNo) throws Exception {
        LOG.log(Level.FINE, "--- loading creditnote advice for customer: {0} invoice: {1} B/L date: {2} ---",
                new Object[]{customerId, invoiceNo, blDateAsString});
        creditNoteAdvice = creditNoteService.generateCreditNoteAdvice(customerId, blDateAsString, invoiceNo);
    }

    private void addFooter(Document layoutDocument) {
        LOG.log(Level.FINE, "--- adding footer ---");

        Paragraph footer = new Paragraph();
        Text line1 = new Text("Yours faithfully,")
                .addStyle(getSmallRegularStyle());

        footer.add(line1);

        Text line2 = new Text("\nFor: NIGERIAN NATIONAL PETROLEUM CORPORATION")
                .addStyle(getSmallBoldStyle());

        footer.add(line2);

        layoutDocument.add(footer);
    }

    private void addSignature(Document layoutDocument) throws IOException {
        LOG.log(Level.FINE, "--- adding signature line ---");

        //PdfFont font = PdfFontFactory.createFont(ARIAL_BLACK, PdfEncodings.IDENTITY_H, true);
        Style style = getSmallBoldStyle();
        //style.setFont(font).setFontSize(12);

        float[] columnWidths = {1.0f, 1.5f};
        Table signTable = new Table(columnWidths);
        signTable.setWidth(360);

        Paragraph pLeft = new Paragraph()
                .setHorizontalAlignment(HorizontalAlignment.LEFT)
                .setMultipliedLeading(1);

        Text line11 = new Text("ABAH, A. L. (MRS)")
                .addStyle(style);
        pLeft.add(line11);
        Text line21 = new Text("\nFOR: GGM, COMD")
                .addStyle(style);
        pLeft.add(line21);

        signTable.addCell(new Cell()
                .add(pLeft)
                .setPadding(0)
                .setBorder(Border.NO_BORDER));

        Paragraph pRight = new Paragraph()
                .setHorizontalAlignment(HorizontalAlignment.LEFT)
                .setMultipliedLeading(1);

        Text line12 = new Text("AMINA M. MOHAMMED")
                .addStyle(style);
        pRight.add(line12);
        Text line22 = new Text("\nFOR: GGM, COMD")
                .addStyle(style);
        pRight.add(line22);

        signTable.addCell(new Cell()
                .add(pRight)
                .setPadding(0)
                .setBorder(Border.NO_BORDER));

//        signTable.addCell(getCell("ABAH, A. L. (MRS)", style, TextAlignment.LEFT));
//        signTable.addCell(getCell("AMINA M. MOHAMMED", style, TextAlignment.LEFT));
//        signTable.addCell(getCell("FOR: GGM, COMD", style, TextAlignment.LEFT));       
//        signTable.addCell(getCell("SUPVR. C&P RECON. COMD", style, TextAlignment.LEFT));
        layoutDocument.add(signTable);
    }

    private Style getMidBoldStyle() {
        Style style = new Style();

        PdfFont midBold;
        try {
            midBold = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
            style.setFont(midBold)
                    .setFontSize(14);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return style;
    }

    private Style getSubBoldStyle() {
        Style style = new Style();
        PdfFont subBold;
        try {
            subBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            style.setFont(subBold)
                    .setFontSize(12);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return style;
    }

    private Style getSmallRegularStyle() {
        Style style = new Style();
        PdfFont smallReg;
        try {
            smallReg = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        style.setFont(smallReg)
                .setFontSize(12);

        return style;
    }

    private Style getSmallBoldStyle() {
        Style style = new Style();
        PdfFont smallBold;
        try {
            smallBold = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        style.setFont(smallBold)
                .setFontSize(12);

        return style;
    }

    private Style getSmallBoldStyle2() {
        Style style = new Style();
        PdfFont smallBold2;
        try {
            smallBold2 = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        style.setFont(smallBold2)
                .setFontSize(12);

        return style;
    }

    private Style getSmallBoldUnderlineStyle() {
        Style style = new Style();
        PdfFont smallBoldUnderline;
        try {
            smallBoldUnderline = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        style.setFont(smallBoldUnderline)
                .setFontSize(12)
                .setUnderline();

        return style;
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
        } catch (CustomerException ex) {
            Logger.getLogger(CreditNoteAdvice.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (Exception ex) {
            Logger.getLogger(CreditNoteAdvice.class
                    .getName()).log(Level.SEVERE, null, ex);
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
        } catch (CustomerException ex) {
            Logger.getLogger(CreditNoteAdvice.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (Exception ex) {
            Logger.getLogger(CreditNoteAdvice.class
                    .getName()).log(Level.SEVERE, null, ex);
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

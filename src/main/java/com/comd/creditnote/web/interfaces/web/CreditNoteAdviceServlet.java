///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.comd.creditnote.web.interfaces.web;
//
//import com.comd.creditnote.web.application.CreditNoteService;
//import com.comd.creditnote.web.domain.model.CreditNoteAdvice;
//import com.comd.creditnote.web.interfaces.rest.exceptions.CustomerException;
//import com.itextpdf.text.*;
//import com.itextpdf.text.pdf.PdfPCell;
//import com.itextpdf.text.pdf.PdfPTable;
//import com.itextpdf.text.pdf.PdfWriter;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.inject.Inject;
//
///**
// * @author Ayemi
// */
//@WebServlet(name = "CreditNoteAdvice", urlPatterns = {"/faces/creditnote/advice"})
//public class CreditNoteAdviceServlet extends HttpServlet {
//
//    @Inject
//    private CreditNoteService creditNoteService;
//
//    private CreditNoteAdvice creditNoteAdvice;
//
//    ByteArrayOutputStream baos;
//
//    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
//    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.RED);
//    private static Font midBold = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
//    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
//    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
//
//    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException, DocumentException, CustomerException, Exception {
//        response.setContentType("text/html;charset=UTF-8");
//
//        String customerId = request.getParameter("customer");
//        String blDateAsString = request.getParameter("bldate");
//        String invoiceNo = request.getParameter("bldate");
//
//        loadCreditNoteAdvice(customerId, blDateAsString, invoiceNo);
//
//        try {
//
//            Document document = new Document(PageSize.A4);
//            baos = new ByteArrayOutputStream();
//            //PdfWriter.getInstance(document, baos);
//            PdfWriter writer = PdfWriter.getInstance(document, baos);
//            writer.setPageEvent(new PdfWatermarkHelper());
//
//            document.open();
//            addMetaData(document);
//            addTitlePage(document);
//            addContent(document);
//
//            document.close();
//
//            setResponseHeaders(response);
//
//            OutputStream os = response.getOutputStream();
//            baos.writeTo(os);
//            os.flush();
//            os.close();
//        } catch (DocumentException e) {
//            throw new IOException(e.getMessage());
//        }
//    }
//
//    private void setResponseHeaders(HttpServletResponse response) {
//        response.setHeader("Expires", "0");
//        response.setHeader("Cache-Control",
//                "must-revalidate, post-check=0, pre-check=0");
//        response.setHeader("Pragma", "public");
//        response.setContentType("application/pdf");
//        response.setContentLength(baos.size());
//    }
//
//    private void addContent(Document document) throws DocumentException {
//
//        PdfPTable table = new PdfPTable(5);
//        table.setWidths(new float[]{3.5f, 3.0f, 3.0f, 3.0f, 4.0f});
//        table.setWidthPercentage(100);
//
//        PdfPCell c1 = new PdfPCell(new Phrase("JV COMPANY", midBold));
//        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
//        table.addCell(c1);
//
//        c1 = new PdfPCell(new Phrase("CRUDE TYPE", midBold));
//        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
//        table.addCell(c1);
//
//        c1 = new PdfPCell(new Phrase("NNPC (BBLS)", midBold));
//        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
//        table.addCell(c1);
//
//        c1 = new PdfPCell(new Phrase("COMPANY (BBLS)", midBold));
//        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
//        table.addCell(c1);
//
//        c1 = new PdfPCell(new Phrase("REMARKS", midBold));
//        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
//        table.addCell(c1);
//
//        table.setHeaderRows(1);
//
//        addTableContent(table);
//
//        document.add(table);
//
//    }
//
//    private void addTableContent(PdfPTable table) {
//        PdfPCell cell;
//
//    }
//
//    private void addMetaData(Document document) {
//        document.addTitle("Credit Note Advice");
//        document.addSubject("Credit Note Advice");
//        document.addKeywords("Credit Note, Demurrage, Claim");
//        document.addAuthor("COMD");
//        document.addCreator("COMD");
//        document.addCreationDate();
//    }
//
//    private void addTitlePage(Document document) throws DocumentException {
//        Paragraph preface = new Paragraph();
//        preface.add(new Paragraph("CREDIT NOTE ADVICE", catFont));
//        addEmptyLine(preface, 1);
//        preface.add(new Paragraph(String.format("CREDIT NOTE ADVICE"), subFont));
//        addEmptyLine(preface, 3);
//        preface.setAlignment(Element.ALIGN_CENTER);
//        document.add(preface);
//    }
//
//    private void addEmptyLine(Paragraph paragraph, int number) {
//        for (int i = 0; i < number; i++) {
//            paragraph.add(new Paragraph(" "));
//        }
//    }
//
//    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
//    /**
//     * Handles the HTTP <code>GET</code> method.
//     *
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        try {
//            processRequest(request, response);
//                } catch (CustomerException ex) {
//            Logger.getLogger(CreditNoteAdviceServlet.class.getName()).log(Level.SEVERE, null, ex);
//} catch (DocumentException e) {
//            e.printStackTrace();
//        } catch (Exception ex) {
//            Logger.getLogger(CreditNoteAdviceServlet.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    /**
//     * Handles the HTTP <code>POST</code> method.
//     *
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        try {
//            processRequest(request, response);
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        } catch (CustomerException ex) {
//            Logger.getLogger(CreditNoteAdviceServlet.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            Logger.getLogger(CreditNoteAdviceServlet.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    /**
//     * Returns a short description of the servlet.
//     *
//     * @return a String containing servlet description
//     */
//    @Override
//    public String getServletInfo() {
//        return "Short description";
//    }// </editor-fold>
//
//     private void loadCreditNoteAdvice(String customerId, String blDateAsString, String invoiceNo) throws CustomerException, Exception{
//        creditNoteAdvice = creditNoteService.generateCreditNoteAdvice(customerId, blDateAsString, invoiceNo);
//    }
//
//}

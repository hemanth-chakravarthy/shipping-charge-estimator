package com.shipping.estimator.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;

@Service
public class InvoiceService {

    public ByteArrayInputStream generateBulkInvoice(String customerName, String warehouseName, java.util.Map<String, Integer> items, com.shipping.estimator.dto.ShippingChargeResponse response) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            Paragraph title = new Paragraph("Enterprise Logistics Invoice", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Date: " + new Date().toString(), normalFont));
            document.add(new Paragraph("To: " + customerName, boldFont));
            document.add(new Paragraph("From: " + warehouseName, normalFont));
            document.add(new Paragraph("Distance: " + response.getDistanceKm() + " KM", normalFont));
            document.add(new Paragraph("\nItems in Shipment:", boldFont));
            
            for (java.util.Map.Entry<String, Integer> entry : items.entrySet()) {
                document.add(new Paragraph(" - " + entry.getKey() + " x " + entry.getValue(), normalFont));
            }
            
            document.add(new Paragraph("\nCost Breakdown:", boldFont));
            document.add(new Paragraph("Product Subtotal: INR " + response.getProductSubtotal(), normalFont));
            document.add(new Paragraph("Base Shipping: INR " + response.getBaseCharge(), normalFont));
            document.add(new Paragraph("Fuel Surcharge (5%): INR " + response.getFuelSurcharge(), normalFont));
            document.add(new Paragraph("Handling & Tech Fee: INR " + response.getHandlingFee(), normalFont));
            
            document.add(new Paragraph("\nService: Multi-Item Enterprise Logistics V3.0", smallFont));
            document.add(new Paragraph("\n"));

            Paragraph total = new Paragraph("GRAND TOTAL: INR " + response.getGrandTotal(), titleFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.close();
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }
}

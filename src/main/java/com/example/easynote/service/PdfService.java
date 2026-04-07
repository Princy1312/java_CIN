package com.example.easynote.service;

import com.example.easynote.entity.CinRequest;
import com.example.easynote.entity.Citizen;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.Hashtable;

@Service
public class PdfService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public byte[] generateCinPdf(CinRequest request) throws Exception {
        Citizen c = request.getCitizen();

        float cardW = 243f;
        float cardH = 153f;

        Document document = new Document(new Rectangle(cardW, cardH));
        document.setMargins(0, 0, 0, 0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, out);
        document.open();

        PdfContentByte cb = writer.getDirectContent();

        // ── COULEURS ──
        BaseColor bleuFonce = new BaseColor(26, 60, 94);
        BaseColor bleuClair = new BaseColor(45, 106, 159);
        BaseColor or        = new BaseColor(232, 160, 32);
        BaseColor blanc     = BaseColor.WHITE;

        // ── FOND ──
        cb.setColorFill(bleuFonce);
        cb.rectangle(0, 0, cardW, cardH);
        cb.fill();

        // ── BANDE DORÉE HAUT ──
        cb.setColorFill(or);
        cb.rectangle(0, cardH - 20, cardW, 20);
        cb.fill();

        // ── BANDE DORÉE BAS ──
        cb.setColorFill(or);
        cb.rectangle(0, 0, cardW, 7);
        cb.fill();

        // ── COLONNE GAUCHE ──
        cb.setColorFill(bleuClair);
        cb.rectangle(0, 7, 62, cardH - 27);
        cb.fill();

        // ── TITRE ──
        Font titreFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 5.5f, blanc);
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
            new Phrase("REPOBLIKAN'I MADAGASIKARA  —  CARTE NATIONALE D'IDENTITÉ", titreFont),
            cardW / 2, cardH - 13, 0);

        // ── NUMÉRO NATIONAL VERTICAL ──
        Font numFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 5.5f, or);
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
            new Phrase(c.getNumeroNational(), numFont),
            12, cardH / 2, 90);

        // ══════════════════════════════════════
        // ── PHOTO — utiliser cb.addImage() pas document.add() ──
        // ══════════════════════════════════════
        boolean photoOk = false;
        if (c.getPhotoPath() != null && !c.getPhotoPath().isBlank()) {
            try {
                File photoFile = new File(System.getProperty("user.dir")
                    + File.separator + "uploads"
                    + File.separator + "photos"
                    + File.separator + c.getPhotoPath());

                if (photoFile.exists()) {
                    BufferedImage buffImg = ImageIO.read(photoFile);
                    if (buffImg != null) {
                        // Cadre blanc
                        cb.setColorFill(blanc);
                        cb.rectangle(8, cardH - 88, 46, 58);
                        cb.fill();

                        ByteArrayOutputStream imgOut = new ByteArrayOutputStream();
                        ImageIO.write(buffImg, "PNG", imgOut);

                        Image photo = Image.getInstance(imgOut.toByteArray());
                        photo.scaleToFit(44, 54);

                        float px = 8 + (46 - photo.getScaledWidth()) / 2;
                        float py = cardH - 88 + (58 - photo.getScaledHeight()) / 2;

                        // ✅ CORRECTION CLÉE : cb.addImage() pas document.add()
                        photo.setAbsolutePosition(px, py);
                        cb.addImage(photo);
                        photoOk = true;
                    }
                }
            } catch (Exception e) {
                System.out.println("Erreur photo: " + e.getMessage());
            }
        }

        // Silhouette si pas de photo
        if (!photoOk) {
            cb.setColorFill(new BaseColor(160, 195, 225));
            cb.rectangle(8, cardH - 88, 46, 58);
            cb.fill();
            cb.setColorFill(new BaseColor(210, 228, 242));
            cb.circle(31, cardH - 50, 12);
            cb.fill();
            cb.setColorFill(new BaseColor(210, 228, 242));
            cb.roundRectangle(14, cardH - 88, 34, 26, 6);
            cb.fill();
        }

        // ══════════════════════════════════════
        // ── QR CODE — utiliser cb.addImage() pas document.add() ──
        // ══════════════════════════════════════
        try {
            QRCodeWriter qrWriter = new QRCodeWriter();
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.MARGIN, 1);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix bitMatrix = qrWriter.encode(
                "CIN-VERIFY:" + c.getQrCodeToken(),
                BarcodeFormat.QR_CODE, 150, 150, hints
            );

            BufferedImage qrBuffered = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream qrOut = new ByteArrayOutputStream();
            ImageIO.write(qrBuffered, "PNG", qrOut);

            // Fond blanc derrière QR
            cb.setColorFill(blanc);
            cb.rectangle(cardW - 47, 8, 44, 44);
            cb.fill();

            Image qrImg = Image.getInstance(qrOut.toByteArray());
            qrImg.scaleToFit(42, 42);
            qrImg.setAbsolutePosition(cardW - 46, 9);

            // ✅ CORRECTION CLÉE : cb.addImage() pas document.add()
            cb.addImage(qrImg);

            Font qrLabel = FontFactory.getFont(FontFactory.HELVETICA, 4f, new BaseColor(180, 200, 220));
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                new Phrase("Scan pour vérifier", qrLabel),
                cardW - 25, 7, 0);

        } catch (Exception e) {
            System.out.println("Erreur QR: " + e.getMessage());
        }

        // ── INFORMATIONS ──
        float xInfo = 68f;
        float yInfo = cardH - 28f;
        float lineH = 11f;

        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 4.5f, or);
        Font valFont   = FontFactory.getFont(FontFactory.HELVETICA, 7f, blanc);
        Font nomFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f, blanc);

        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("NOM", labelFont), xInfo, yInfo, 0);
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(c.getNom().toUpperCase(), nomFont), xInfo, yInfo - 9, 0);

        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("PRÉNOM", labelFont), xInfo, yInfo - lineH * 2, 0);
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(c.getPrenom(), valFont), xInfo, yInfo - lineH * 2 - 7, 0);

        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("DATE DE NAISSANCE", labelFont), xInfo, yInfo - lineH * 4, 0);
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
            new Phrase(c.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), valFont),
            xInfo, yInfo - lineH * 4 - 7, 0);

        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("LIEU DE NAISSANCE", labelFont), xInfo, yInfo - lineH * 6, 0);
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(c.getLieuNaissance(), valFont), xInfo, yInfo - lineH * 6 - 7, 0);

        String adresse = c.getAdresse().length() > 38 ? c.getAdresse().substring(0, 38) + "..." : c.getAdresse();
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("ADRESSE", labelFont), xInfo, yInfo - lineH * 8, 0);
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(adresse, valFont), xInfo, yInfo - lineH * 8 - 7, 0);

        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("SEXE", labelFont), xInfo, yInfo - lineH * 10, 0);
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
            new Phrase(c.getSexe().equals("M") ? "Masculin" : "Féminin", valFont),
            xInfo, yInfo - lineH * 10 - 7, 0);

        Font dosFont = FontFactory.getFont(FontFactory.COURIER, 4.5f, new BaseColor(160, 185, 210));
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
            new Phrase("N° Dossier: " + request.getNumeroDossier(), dosFont),
            68, 10, 0);

        document.close();
        return out.toByteArray();
    }
}
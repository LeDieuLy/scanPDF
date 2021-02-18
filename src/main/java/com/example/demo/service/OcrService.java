package com.example.demo.service;


import com.example.demo.conStants.Constants;
import com.example.demo.model.DocCreaditAplicationFModel;
import com.example.demo.model.OcrResult;
import com.example.demo.model.DocCreaditChangeFModel;
import com.example.demo.model.RemitAplicationFModel;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OcrService {
    @Autowired
    private Tesseract tesseract;

    public static PDDocument convertDoc(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        PDDocument document = PDDocument.load(convFile);
        return document;
    }

    public String checkFormPDF(PDDocument document) throws IOException, TesseractException {
        String checkForm = extractTextFromScannedDocument(document, tesseract, Constants.checkFLocationX, Constants.checkFLocationY,Constants.checkFLocationW,Constants.checkFLocationH);
        System.out.println("check Form: " + checkForm);
        String[] checkFormCode = checkForm.split("\n");
        System.out.println("checkFormCode: " + checkFormCode[0]);
        return checkFormCode[0];
    }

    public DocCreaditChangeFModel getDocCreChange(PDDocument document) throws IOException, TesseractException {
        DocCreaditChangeFModel docCreaditChangeFModel = new DocCreaditChangeFModel();
        String text = extractTextFromScannedDocument(document, tesseract, Constants.dcchangeFLocationX, Constants.dcchangeFLocationY,Constants.dcchangeFLocationW,Constants.dcchangeFLocationH);
        List<String> value = cutDocCreditChange(text);
        if (value.size()>0){
            docCreaditChangeFModel.setLcNo(value.get(0));
            docCreaditChangeFModel.setIssueDate(value.get(1));
            docCreaditChangeFModel.setLcAmount(value.get(2));
        }
        return docCreaditChangeFModel;
    }

    public DocCreaditAplicationFModel getDocCreApli(PDDocument document) throws IOException, TesseractException {
        DocCreaditAplicationFModel DocCreaditAplicationFModel = new DocCreaditAplicationFModel();
        String text = extractTextFromScannedDocument(document, tesseract, Constants.dcapliFLocationX, Constants.dcapliFLocationY,Constants.dcapliFLocationW,Constants.dcapliFLocationH);
        List<String> value = cutDocCreditApli(text);
        if (value.size()>0){
            DocCreaditAplicationFModel.setCommodity(value.get(0));
            DocCreaditAplicationFModel.setQuantity(value.get(1));
            DocCreaditAplicationFModel.setUniprice(value.get(2));
        }
        return DocCreaditAplicationFModel;
    }

    public RemitAplicationFModel getRemitApli(PDDocument document) throws IOException, TesseractException {
        RemitAplicationFModel RemitAplicationFModel = new RemitAplicationFModel();
        String text = extractTextFromScannedDocument(document, tesseract, Constants.remitLocationX, Constants.remitLocationY,Constants.remitLocationW,Constants.remitLocationH);
        List<String> value = cutRemitAplication(text);
        if (value.size()>0){
            RemitAplicationFModel.setMoneyTransfer(value.get(0));
        }
        return RemitAplicationFModel;
    }



    public static List<String> cutRemitAplication(String text){
        List<String> value = new ArrayList<>();
        if(text != null){
            String[] textSub = text.split("\n");
            value.add(textSub[0]);
        }
        return value;
    }

    public static List<String> cutDocCreditApli(String text){
        List<String> value = new ArrayList<>();
        if(text != null){
            String[] textPageSub = text.split("\n\n");
            String[] textSub = textPageSub[0].split("\n");
            for (String i : textSub) {
                System.out.println("value: " + i);
                if(i.contains(":")){
                    String[] valueSub = i.split(":");
                    if(valueSub.length == 2){
                        System.out.println("value cut: " + valueSub[1]);
                        value.add(valueSub[1]);
                    }
                }
            }
        }
        return value;
    }

    public static List<String> cutDocCreditChange(String text){
        List<String> value = new ArrayList<>();
        if(text != null){
            String[] textSub = text.split("\n\n");
            for (String i : textSub) {
                System.out.println("value: " + i);
                if(i.contains(":")){
                    String[] valueSub = i.split(":");
                    if(valueSub.length == 2){
                        System.out.println("value cut: " + valueSub[1]);
                        value.add(valueSub[1]);
                    }
                }
            }
        }
        return value;
    }

    public static String extractTextFromScannedDocument(PDDocument document, Tesseract tesseract, int x, int y, int with, int height) throws IOException, TesseractException {
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        StringBuilder out = new StringBuilder();
        for (int page = 0; page < document.getNumberOfPages(); page++) {
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, 300, ImageType.ARGB);
            BufferedImage crop = cropImage(bufferedImage, x, y, with, height);
            File tempFile = File.createTempFile("tempfile_" + page, ".png");
            ImageIO.write(crop, "png", tempFile);
            String result = tesseract.doOCR(tempFile);
            out.append(result);
            tempFile.delete();
        }
        return out.toString();
    }

    public static BufferedImage cropImage(BufferedImage bufferedImage, int x, int y, int width, int height) {
        BufferedImage croppedImage = bufferedImage.getSubimage(x, y, width, height);
        return croppedImage;
    }


}

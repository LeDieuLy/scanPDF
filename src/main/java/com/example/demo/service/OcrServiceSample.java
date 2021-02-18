package com.example.demo.service;

import com.example.demo.conStants.Constants;
import com.example.demo.model.DocCreaditChangeFModel;
import com.example.demo.model.OcrResult;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.example.demo.service.OcrService.*;

@Service
public class OcrServiceSample {

    @Autowired
    private Tesseract tesseract;

    public OcrResult ocr(MultipartFile file) throws IOException, TesseractException {
        File convFile = convertFile(file);
        tesseract.setLanguage("vie");
        OcrResult ocrResult = new OcrResult();
        String result = tesseract.doOCR(convFile);
        ocrResult.setResult(result);
        System.out.println("---Result: " + ocrResult.getResult());
        return ocrResult;
    }

    public static File convertFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    public DocCreaditChangeFModel setOcrData(MultipartFile file) throws IOException, TesseractException {
        File convFile = convertFile(file);
        tesseract.setLanguage("vie");
        PDDocument document = PDDocument.load(convFile);
        DocCreaditChangeFModel docCreaditChangeFModel = new DocCreaditChangeFModel();
        String checkForm = extractTextFromScannedDocument(document, tesseract, Constants.checkFLocationX, Constants.checkFLocationY,Constants.checkFLocationW,Constants.checkFLocationH);
        System.out.println("check Form: " + checkForm);
        String[] checkFormCode = checkForm.split("\n");
        System.out.println("checkFormCode: " + checkFormCode[0]);
        String text;
        List<String> value;
        if(checkFormCode[0] != null){
            switch (checkFormCode[0]){
                case Constants.docCreaditChangeForm:
                    text = extractTextFromScannedDocument(document, tesseract, Constants.dcchangeFLocationX, Constants.dcchangeFLocationY,Constants.dcchangeFLocationW,Constants.dcchangeFLocationH);
                    System.out.println(text);
                    value = cutDocCreditChange(text);
                    if (value.size()>0){
                        docCreaditChangeFModel.setLcNo(value.get(0));
                        docCreaditChangeFModel.setIssueDate(value.get(1));
                        docCreaditChangeFModel.setLcAmount(value.get(2));
                    }
                    break;
                case Constants.remitAplicationForm:
                    text = extractTextFromScannedDocument(document, tesseract, Constants.remitLocationX, Constants.remitLocationY,Constants.remitLocationW,Constants.remitLocationH);
                    System.out.println(text);
                    value = cutRemitAplication(text);
                    if (value.size()>0){
                        docCreaditChangeFModel.setLcNo(value.get(0));
                        docCreaditChangeFModel.setIssueDate("000");
                        docCreaditChangeFModel.setLcAmount("000");
                    }
                    break;
                case Constants.docCreaditAplicationForm:
                    text = extractTextFromScannedDocument(document, tesseract, Constants.dcapliFLocationX, Constants.dcapliFLocationY,Constants.dcapliFLocationW,Constants.dcapliFLocationH);
                    System.out.println(text);
                    value = cutDocCreditApli(text);
                    if (value.size()>0){
                        docCreaditChangeFModel.setLcNo(value.get(0));
                        docCreaditChangeFModel.setIssueDate(value.get(1));
                        docCreaditChangeFModel.setLcAmount(value.get(2));
                    }
                    break;
                default:
                    docCreaditChangeFModel.setLcNo("000");
                    docCreaditChangeFModel.setIssueDate("000");
                    docCreaditChangeFModel.setLcAmount("000");
                    break;
            }
        }
//        String[] textSub = text.split("\n\n");
//        List<String> value = new ArrayList<String>();

//        for (String i : textSub) {
//            System.out.println("value: " + i);
//            if(i.contains(":")){
//                String[] valueSub = i.split(":");
//                if(valueSub.length == 2){
//                    System.out.println("value cut: " + valueSub[1]);
//                    value.add(valueSub[1]);
//                }
//            }
//        }
        return docCreaditChangeFModel;
    }

}

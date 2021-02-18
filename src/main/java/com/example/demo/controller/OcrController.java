package com.example.demo.controller;

import com.example.demo.conStants.Constants;
import com.example.demo.model.DocCreaditAplicationFModel;
import com.example.demo.model.OcrResult;
import com.example.demo.model.DocCreaditChangeFModel;
import com.example.demo.model.RemitAplicationFModel;
import com.example.demo.service.OcrService;
import com.example.demo.service.OcrServiceSample;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;

@Controller
public class OcrController {
    @Autowired
    private OcrService ocrService;

    private OcrServiceSample ocrServiceSample;

    @PostMapping("/upload")
    public ResponseEntity<OcrResult> upload(@RequestParam("file") MultipartFile file) throws IOException, TesseractException {
        return ResponseEntity.ok(ocrServiceSample.ocr(file));
    }

    @GetMapping
    public ModelAndView showForm() {
        ModelAndView modelAndView = new ModelAndView("scanPdf");
        return modelAndView;
    }

    @PostMapping("/readPdf")
    public ModelAndView readPdf(@RequestParam("file") MultipartFile file) throws IOException, TesseractException {
        PDDocument document = ocrService.convertDoc(file);
        String checkForm = ocrService.checkFormPDF(document);
        ModelAndView modelAndView = new ModelAndView();
        if(checkForm != null){
            switch (checkForm) {
                case Constants.docCreaditChangeForm:
                    DocCreaditChangeFModel docCreaditChangeFModel = ocrService.getDocCreChange(document);
                    modelAndView.addObject("viewTB", "display: inline-table");
                    modelAndView.addObject("response", docCreaditChangeFModel);
                    modelAndView.addObject("type", "docCreaditChangeForm");
                    break;
                case Constants.docCreaditAplicationForm:
                    DocCreaditAplicationFModel DocCreaditAplicationFModel = ocrService.getDocCreApli(document);
                    modelAndView.addObject("viewTB", "display: inline-table");
                    modelAndView.addObject("response", DocCreaditAplicationFModel);
                    modelAndView.addObject("type", "docCreApliF");
                    break;
                case Constants.remitAplicationForm:
                    RemitAplicationFModel RemitAplicationFModel = ocrService.getRemitApli(document);
                    modelAndView.addObject("viewTB", "display: inline-table");
                    modelAndView.addObject("response", RemitAplicationFModel);
                    modelAndView.addObject("type", "remitApliResultF");
                    break;
                default:
                    modelAndView.addObject("type", "default");


            }
        }
        modelAndView.setViewName("scanPdf");
        return modelAndView;
    }

    @PostMapping("/upload2")
    public ResponseEntity<DocCreaditChangeFModel> upload2(@RequestParam("file") MultipartFile file) throws IOException, TesseractException {
        return ResponseEntity.ok(ocrServiceSample.setOcrData(file));
    }

}

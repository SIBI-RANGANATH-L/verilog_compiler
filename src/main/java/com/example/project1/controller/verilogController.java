package com.example.project1.controller;

import com.example.project1.service.verilogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class verilogController {

    private final verilogService verilogservice;

    @Autowired
    public verilogController(verilogService verilogservice) {
        this.verilogservice = verilogservice;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> fileUpload(
            @RequestParam("design") MultipartFile design,
            @RequestParam("tb") MultipartFile tb
    ) {
        try {
            String result = verilogservice.processFile(design, tb);
            System.out.println(result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}

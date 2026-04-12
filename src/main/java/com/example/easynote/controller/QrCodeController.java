package com.example.easynote.controller;

import com.example.easynote.entity.Citizen;
import com.example.easynote.service.CitizenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/qr")
public class QrCodeController {

    @Autowired
    private CitizenService citizenService;

    @GetMapping("/scan")
    public String scanPage() {
        return "qr/scan";
    }

    @GetMapping("/verify/{token}")
    public String verifyQrCode(@PathVariable String token, Model model) {
        try {
            Citizen citizen = citizenService.verifyByQrCode(token)
                    .orElseThrow(() -> new RuntimeException("QR Code invalide ou citoyen non trouvé"));
            
            model.addAttribute("citizen", citizen);
            return "qr/citizen-info";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "qr/error";
        }
    }

    @PostMapping("/verify")
    public String verifyQrCodePost(@RequestParam String token, Model model) {
        try {
            Citizen citizen = citizenService.verifyByQrCode(token)
                    .orElseThrow(() -> new RuntimeException("QR Code invalide ou citoyen non trouvé"));
            
            model.addAttribute("citizen", citizen);
            return "qr/citizen-info";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "qr/error";
        }
    }
}

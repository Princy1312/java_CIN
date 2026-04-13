package com.example.easynote.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        
        if (statusCode == null) {
            statusCode = 500;
        }
        
        // Ne traiter que les vraies erreurs (4xx et 5xx)
        if (statusCode >= 400) {
            model.addAttribute("status", statusCode);
            model.addAttribute("error", request.getAttribute("javax.servlet.error.message"));
            return "error";
        }
        
        // Pour les statuts 2xx et 3xx, rediriger vers le dashboard
        return "redirect:/dashboard";
    }
}

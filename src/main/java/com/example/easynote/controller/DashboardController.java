package com.example.easynote.controller;

import com.example.easynote.enums.RequestStatus;
import com.example.easynote.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    @Autowired private CitizenService citizenService;
    @Autowired private CinRequestService requestService;
    @Autowired private UserService userService;

    @GetMapping("/") public String index() { return "redirect:/dashboard"; }
    @GetMapping("/login") public String login() { return "auth/login"; }

    @GetMapping("/dashboard")
    public String dashboard(Model m, Authentication auth) {
        m.addAttribute("totalCitoyens", citizenService.count());
        m.addAttribute("totalUtilisateurs", userService.count());
        m.addAttribute("enAttente", requestService.countByStatus(RequestStatus.EN_ATTENTE));
        m.addAttribute("enCours", requestService.countByStatus(RequestStatus.EN_COURS));
        m.addAttribute("validees", requestService.countByStatus(RequestStatus.VALIDEE));
        m.addAttribute("imprimees", requestService.countByStatus(RequestStatus.IMPRIMEE));
        m.addAttribute("rejetees", requestService.countByStatus(RequestStatus.REJETEE));
        m.addAttribute("dossiersRetard", requestService.getRetarded().size());
        m.addAttribute("statsRegion", citizenService.countByRegion());
        m.addAttribute("statsMensuelles", requestService.getMonthlyStats());
        return "dashboard";
    }
}

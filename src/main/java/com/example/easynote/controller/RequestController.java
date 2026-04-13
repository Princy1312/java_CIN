package com.example.easynote.controller;

import com.example.easynote.entity.*;
import com.example.easynote.enums.*;
import com.example.easynote.repository.UserRepository;
import com.example.easynote.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/requests")
public class RequestController {

    @Autowired private CinRequestService requestService;
    @Autowired private CitizenService citizenService;
    @Autowired private UserRepository userRepo;
    @Autowired private PdfService pdfService;

    @GetMapping
    public String list(Model m, @RequestParam(required = false) String status) {
        try {
            List<CinRequest> list = (status != null && !status.isBlank())
                    ? requestService.getByStatus(RequestStatus.valueOf(status))
                    : requestService.getAll();
            m.addAttribute("requests", list);
            m.addAttribute("statuses", RequestStatus.values());
            m.addAttribute("filterStatus", status);
            return "requests/list";
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des demandes: " + e.getMessage());
            m.addAttribute("error", "Erreur lors du chargement des demandes: " + e.getMessage());
            return "requests/list";
        }
    }

    @GetMapping("/new")
    public String newForm(Model m) {
        try {
            m.addAttribute("citizens", citizenService.getAll());
            m.addAttribute("types", RequestType.values());
            return "requests/form";
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du formulaire de demande: " + e.getMessage());
            m.addAttribute("error", "Erreur lors du chargement du formulaire de demande: " + e.getMessage());
            return "requests/form"; // Return statement added
        }
    }

    @PostMapping("/new")
    public String create(@RequestParam Long citizenId, @RequestParam RequestType type,
                         Authentication auth, RedirectAttributes ra) {
        try {
            User agent = userRepo.findByEmail(auth.getName()).orElse(null);
            requestService.create(citizenId, type, agent);
            ra.addFlashAttribute("success", "Demande créée avec succès!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/requests";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model m) {
        m.addAttribute("request", requestService.getById(id).orElseThrow());
        m.addAttribute("statuses", RequestStatus.values());
        return "requests/view";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam RequestStatus newStatus,
                               @RequestParam(required = false) String motif,
                               Authentication auth, RedirectAttributes ra) {
        try {
            User agent = userRepo.findByEmail(auth.getName()).orElse(null);
            requestService.updateStatus(id, newStatus, motif, agent);
            ra.addFlashAttribute("success", "Statut mis à jour : " + newStatus.name().replace("_", " "));
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/requests/" + id;
    }

    // ── PDF — accessible à tous les utilisateurs connectés ──
    // Le contrôle du rôle est fait dans le template HTML (sec:authorize)
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id, Authentication auth) {
        try {
            // Vérifier manuellement que l'utilisateur a le bon rôle
            boolean autorise = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERVISEUR")
                               || a.getAuthority().equals("ROLE_ADMINISTRATEUR"));

            if (!autorise) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            CinRequest request = requestService.getById(id)
                    .orElseThrow(() -> new RuntimeException("Demande introuvable"));

            byte[] pdf = pdfService.generateCinPdf(request);

            String filename = "CIN_" + request.getCitizen().getNumeroNational() + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdf.length)
                    .body(pdf);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
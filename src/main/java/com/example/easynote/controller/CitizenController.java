package com.example.easynote.controller;

import com.example.easynote.entity.*;
import com.example.easynote.repository.UserRepository;
import com.example.easynote.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/citizens")
public class CitizenController {
    @Autowired private CitizenService citizenService;
    @Autowired private UserRepository userRepo;
    @Autowired private QrCodeService qrCodeService;

    @GetMapping
    public String list(Model m, @RequestParam(required=false) String search) {
        try {
            List<Citizen> list = (search != null && !search.isBlank())
                    ? citizenService.search(search) : citizenService.getAll();
            m.addAttribute("citizens", list);
            m.addAttribute("search", search);
            return "citizens/list";
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des citoyens: " + e.getMessage());
            m.addAttribute("error", "Erreur lors du chargement des citoyens: " + e.getMessage());
            return "citizens/list";
        }
    }

    @GetMapping("/new")
    public String newForm(Model m) { m.addAttribute("citizen", new Citizen()); return "citizens/form"; }

    @PostMapping("/new")
    public String create(@ModelAttribute Citizen c, @RequestParam(required=false) MultipartFile photo,
                         Authentication auth, RedirectAttributes ra) {
        try {
            User agent = userRepo.findByEmail(auth.getName()).orElse(null);
            citizenService.create(c, photo, agent);
            ra.addFlashAttribute("success", "Citoyen enregistré avec succès!");
        } catch (Exception e) { ra.addFlashAttribute("error", "Erreur: " + e.getMessage()); }
        return "redirect:/citizens";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model m) {
        Citizen c = citizenService.getById(id).orElseThrow();
        m.addAttribute("citizen", c);
        if (c.getQrCodeToken() != null) {
            try { m.addAttribute("qrCodeBase64", qrCodeService.getQrBase64(c.getQrCodeToken())); }
            catch (Exception ignored) {}
        }
        return "citizens/view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model m) {
        m.addAttribute("citizen", citizenService.getById(id).orElseThrow());
        return "citizens/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute Citizen c,
                         @RequestParam(required=false) MultipartFile photo, RedirectAttributes ra) {
        try { citizenService.update(id, c, photo); ra.addFlashAttribute("success", "Mis à jour!"); }
        catch (Exception e) { ra.addFlashAttribute("error", "Erreur: " + e.getMessage()); }
        return "redirect:/citizens/" + id;
    }

    @PostMapping("/{id}/archive")
    public String archive(@PathVariable Long id, RedirectAttributes ra) {
        citizenService.archive(id); ra.addFlashAttribute("success", "Citoyen archivé.");
        return "redirect:/citizens";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        citizenService.delete(id); ra.addFlashAttribute("success", "Citoyen supprimé.");
        return "redirect:/citizens";
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) {
        try {
            Citizen citizen = citizenService.getById(id)
                    .orElseThrow(() -> new RuntimeException("Citoyen introuvable"));
            
            if (citizen.getPhotoPath() == null || citizen.getPhotoPath().isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            // Construire le chemin complet du fichier
            Path filePath = Paths.get(System.getProperty("user.dir"), "uploads", "photos", citizen.getPhotoPath());
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] photoBytes = Files.readAllBytes(filePath);
            
            // Détecter le type de contenu
            String contentType = "image/jpeg";
            if (citizen.getPhotoPath().toLowerCase().endsWith(".png")) {
                contentType = "image/png";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(photoBytes);
                    
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

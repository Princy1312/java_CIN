package com.example.easynote.controller;

import com.example.easynote.entity.User;
import com.example.easynote.enums.Role;
import com.example.easynote.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMINISTRATEUR')")
public class AdminController {
    @Autowired private UserService userService;

    @GetMapping("/users")
    public String list(Model m) { m.addAttribute("users", userService.getAll()); m.addAttribute("roles", Role.values()); return "admin/users"; }

    @GetMapping("/users/new")
    public String newForm(Model m) { m.addAttribute("user", new User()); m.addAttribute("roles", Role.values()); return "admin/user-form"; }

    @PostMapping("/users/new")
    public String create(@ModelAttribute User u, RedirectAttributes ra) {
        try { userService.create(u); ra.addFlashAttribute("success", "Utilisateur créé!"); }
        catch (Exception e) { ra.addFlashAttribute("error", "Erreur: " + e.getMessage()); }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/{id}/edit")
    public String editForm(@PathVariable Long id, Model m) {
        m.addAttribute("user", userService.getById(id).orElseThrow()); m.addAttribute("roles", Role.values()); return "admin/user-form";
    }

    @PostMapping("/users/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute User u, RedirectAttributes ra) {
        try { userService.update(id, u); ra.addFlashAttribute("success", "Mis à jour!"); }
        catch (Exception e) { ra.addFlashAttribute("error", "Erreur: " + e.getMessage()); }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggle(@PathVariable Long id, RedirectAttributes ra) {
        userService.toggleStatus(id); ra.addFlashAttribute("success", "Statut modifié!"); return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        userService.delete(id); ra.addFlashAttribute("success", "Supprimé!"); return "redirect:/admin/users";
    }
}

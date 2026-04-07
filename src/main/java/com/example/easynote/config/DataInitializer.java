package com.example.easynote.config;

import com.example.easynote.entity.User;
import com.example.easynote.enums.Role;
import com.example.easynote.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail("admin@cin.gov")) return;

        createUser("Admin", "Système", "admin@cin.gov", "Admin@1234", Role.ADMINISTRATEUR);
        createUser("Dupont", "Jean", "agent@cin.gov", "Agent@1234", Role.AGENT_ENREGISTREMENT);
        createUser("Martin", "Sophie", "validateur@cin.gov", "Valid@1234", Role.AGENT_VALIDATION);
        createUser("Bernard", "Michel", "superviseur@cin.gov", "Super@1234", Role.SUPERVISEUR);

        System.out.println("=====================================================");
        System.out.println("  COMPTES PAR DÉFAUT:");
        System.out.println("  admin@cin.gov        / Admin@1234");
        System.out.println("  agent@cin.gov        / Agent@1234");
        System.out.println("  validateur@cin.gov   / Valid@1234");
        System.out.println("  superviseur@cin.gov  / Super@1234");
        System.out.println("=====================================================");
    }

    private void createUser(String nom, String prenom, String email, String pwd, Role role) {
        User u = new User();
        u.setNom(nom); u.setPrenom(prenom); u.setEmail(email);
        u.setMotDePasse(passwordEncoder.encode(pwd));
        u.setRole(role); u.setActif(true);
        userRepository.save(u);
    }
}

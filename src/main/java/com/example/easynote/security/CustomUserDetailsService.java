package com.example.easynote.security;

import com.example.easynote.repository.UserRepository;
import com.example.easynote.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Tentative de connexion pour: " + email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("Utilisateur non trouvé: " + email);
                    return new UsernameNotFoundException("Utilisateur non trouvé: " + email);
                });
        System.out.println("Utilisateur trouvé: " + user.getEmail() + ", rôle: " + user.getRole() + ", actif: " + user.isActif());
        if (!user.isActif()) {
            System.out.println("Compte désactivé pour: " + email);
            throw new UsernameNotFoundException("Compte désactivé");
        }
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getMotDePasse(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
        System.out.println("UserDetails créés avec succès pour: " + email);
        return userDetails;
    }
}

package com.example.easynote.controller;

import com.example.easynote.entity.*;
import com.example.easynote.enums.*;
import com.example.easynote.repository.UserRepository;
import com.example.easynote.security.*;
import com.example.easynote.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    @Autowired private CitizenService citizenService;
    @Autowired private CinRequestService requestService;
    @Autowired private UserService userService;
    @Autowired private UserRepository userRepo;
    @Autowired private AuthenticationManager authManager;
    @Autowired private CustomUserDetailsService uds;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(body.get("email"), body.get("password")));
            UserDetails ud = uds.loadUserByUsername(body.get("email"));
            return ResponseEntity.ok(Map.of("token", jwtUtil.generateToken(ud)));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Identifiants invalides"));
        }
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody User u) {
        try { User created = userService.create(u); return ResponseEntity.ok(Map.of("id", created.getId())); }
        catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @GetMapping("/citizens")
    public List<Citizen> getCitizens(@RequestParam(required=false) String search) {
        return search != null ? citizenService.search(search) : citizenService.getAll();
    }

    @GetMapping("/citizens/{id}")
    public ResponseEntity<?> getCitizen(@PathVariable Long id) {
        return citizenService.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/citizens/{id}")
    public ResponseEntity<?> deleteCitizen(@PathVariable Long id) {
        citizenService.delete(id); return ResponseEntity.ok(Map.of("message", "Supprimé"));
    }

    @GetMapping("/requests")
    public List<CinRequest> getRequests(@RequestParam(required=false) String status) {
        return status != null ? requestService.getByStatus(RequestStatus.valueOf(status)) : requestService.getAll();
    }

    @PostMapping("/requests")
    public ResponseEntity<?> createRequest(@RequestBody Map<String, Object> body) {
        try {
            Long cid = Long.valueOf(body.get("citizenId").toString());
            RequestType type = RequestType.valueOf(body.get("type").toString());
            return ResponseEntity.ok(requestService.create(cid, type, null));
        } catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @PutMapping("/requests/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            RequestStatus s = RequestStatus.valueOf(body.get("status"));
            return ResponseEntity.ok(requestService.updateStatus(id, s, body.get("motif"), null));
        } catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @GetMapping("/verify/{qrCode}")
    public ResponseEntity<?> verify(@PathVariable String qrCode) {
        return citizenService.verifyByQrCode(qrCode)
                .map(c -> ResponseEntity.ok(Map.of("valid", true, "nom", c.getNom(), "prenom", c.getPrenom(),
                        "numeroNational", c.getNumeroNational(), "dateNaissance", c.getDateNaissance().toString())))
                .orElse(ResponseEntity.ok(Map.of("valid", false)));
    }
}

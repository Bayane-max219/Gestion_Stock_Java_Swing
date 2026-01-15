package com.miguel.gestionstock.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.miguel.gestionstock.entity.UserAccount;
import com.miguel.gestionstock.repository.UserRepository;

public class UserService {

    private final UserRepository userRepository = new UserRepository();

    public UserAccount register(String username, String password, String fullName) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom d'utilisateur est obligatoire.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire.");
        }
        if (password.length() < 4) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 4 caractères.");
        }

        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà utilisé.");
        }

        String hash = hashPassword(password);
        UserAccount user = new UserAccount(username.trim(), hash, fullName);
        userRepository.save(user);
        return user;
    }

    public UserAccount authenticate(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Nom d'utilisateur et mot de passe sont obligatoires.");
        }

        UserAccount user = userRepository.findByUsername(username.trim());
        if (user == null) {
            throw new IllegalArgumentException("Identifiants invalides.");
        }

        String expectedHash = user.getPasswordHash();
        String actualHash = hashPassword(password);
        if (!expectedHash.equals(actualHash)) {
            throw new IllegalArgumentException("Identifiants invalides.");
        }

        return user;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithme de hachage non disponible.", e);
        }
    }
}

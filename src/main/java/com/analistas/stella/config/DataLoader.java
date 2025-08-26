package com.analistas.stella.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.analistas.stella.model.domain.Usuario;
import com.analistas.stella.model.repository.IUsuarioRepository;
import com.analistas.stella.model.service.IRolService;

import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    IUsuarioRepository usuarioRepository;

    @Autowired
    IRolService rolService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public DataLoader(IUsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Crea el usuario base para acceder
        if (usuarioRepository.count() == 0) {
            Usuario u = new Usuario();
            u.setNombrecompleto("user");
            u.setCorreo("santivalenti@gmail.com");
            u.setRol(rolService.buscarPorId(1L));
            u.setContrasena(passwordEncoder.encode("123456")); // 👈 hash automático
            u.setFechaAlta(LocalDate.now());
            usuarioRepository.save(u);
            // System.out.println("✅ Usuario inicial creado: user / 123456");
        }
    }
}

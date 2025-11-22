package com.analistas.stella.config;

import java.io.IOException;

// import javax.servlet.ServletException;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
// import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.analistas.stella.model.domain.Usuario;
// import com.analistas.stella.model.Caja;
import com.analistas.stella.model.service.ICajaService;
// import com.analistas.stella.service.CajaService;
import com.analistas.stella.model.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    ICajaService cajaService;

    @Autowired
    IUsuarioService usuarioService;

    @Override
    public void onAuthenticationSuccess(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response,
            Authentication authentication) throws IOException, jakarta.servlet.ServletException {

        // Buscar si tiene una caja abierta
        Usuario usuario = usuarioService.findByNombrecompleto(authentication.getName());
        String nombreCompleto = usuario.getNombrecompleto();
        // String username = authentication.getName();
        HttpSession session = request.getSession();

        cajaService.buscaarCajaAbiertaConFetch(nombreCompleto).ifPresent(caja -> {
            session.setAttribute("cajaId", caja.getId());
            session.setAttribute("cajaNombre", caja.getCajaFisica().getNombre());
        });

        response.sendRedirect("/dashboard"); // o donde quieras redirigir
    }
}

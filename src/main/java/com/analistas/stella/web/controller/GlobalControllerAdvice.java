package com.analistas.stella.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.analistas.stella.config.SecurityUtils;
import com.analistas.stella.model.domain.Usuario;
import com.analistas.stella.model.service.IUsuarioService;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    IUsuarioService usuarioService;

    @ModelAttribute
    public void addUserInfo(Model model){
        
        String username = SecurityUtils.getCurrentUsername();

        if (username != null){
            Usuario usuario = usuarioService.findByNombrecompleto(username);

            if(usuario != null) {
                model.addAttribute("nombreUsuario", usuario.getNombrecompleto());
            }
        }

    }

}

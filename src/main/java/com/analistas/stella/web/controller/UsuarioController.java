package com.analistas.stella.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analistas.stella.model.domain.Usuario;
import com.analistas.stella.model.service.IRolService;
import com.analistas.stella.model.service.IUsuarioService;

import jakarta.validation.Valid;


@RequestMapping("/usuarios")
@SessionAttributes({"usuario"}) //Esto hace que al editar un usuario, no se cree otro, sino que se edite 
@Controller
public class UsuarioController {

    @Autowired
    IUsuarioService usuarioService;

    @Autowired
    IRolService rolService;

    @GetMapping("/nuevo")
    public String crearFormulario(Model model) {

        model.addAttribute("titulo", "Nuevo Usuario");
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolService.buscarTodos());

        return "usuarios/usuarios-form";  // Asegurate de tener esta vista en templates
    }
    
    @PostMapping("/guardar")
    public String guardarUsuario(@Valid Usuario usuario, BindingResult result, RedirectAttributes flash,
            SessionStatus status, Model model) {

        // Verificar si hay errores de validación
        if (result.hasErrors()) {
            model.addAttribute("danger", "Corergir los errores del formulario");
        }

        String mensaje = (usuario.getId() == null || usuario.getId() == 0)
                ? "Producto " + usuario.getNombrecompleto() + " añadido"
                : "Producto " + usuario.getNombrecompleto() + " modificado";

        flash.addFlashAttribute((usuario.getId() == null || usuario.getId() == 0) ? "success" : "warning", mensaje);

        usuarioService.guardar(usuario);
        status.setComplete(); // Lo que hace es limpiar la variable "producto" definida en el SessionStatus de
                              // arriba

        return "redirect:/dashboard";
        // Al usar redirect, se usa el link de los GetMapping, no la dirección del archivo
    }

}

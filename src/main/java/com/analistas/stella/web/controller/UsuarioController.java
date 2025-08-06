package com.analistas.stella.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analistas.stella.model.domain.Usuario;
import com.analistas.stella.model.service.IUsuarioService;

import jakarta.validation.Valid;

@RequestMapping("/usuarios")
@SessionAttributes({"usuario"}) //Esto hace que al editar un usuario, no se cree otro, sino que se edite 

public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping("/nuevo")
    public String crearFormulario(Model model) {
        model.addAttribute("titulo", "nuevo usuario");
        model.addAttribute("usuario", new Usuario());
        //model.addAttribute(null, model);
        return "usuarios/form";  // Asegurate de tener esta vista en templates
    }
    
    // @PostMapping("/guardar")
    // public String guardarProducto(@Valid Producto producto, BindingResult result, RedirectAttributes flash,
    //         SessionStatus status, Model model) {

    //     // Verificar si hay errores de validación
    //     if (result.hasErrors()) {
    //         model.addAttribute("danger", "Corergir los errores del formulario");
    //     }

    //     String mensaje = (producto.getId() == null || producto.getId() == 0)
    //             ? "Producto " + producto.getNombre() + " añadido"
    //             : "Producto " + producto.getNombre() + " modificado";

    //     flash.addFlashAttribute((producto.getId() == null || producto.getId() == 0) ? "success" : "warning", mensaje);

    //     productoService.guardar(producto);
    //     status.setComplete(); // Lo que hace es limpiar la variable "producto" definida en el SessionStatus de
    //                           // arriba

    //     return "redirect:/productos/listadoAdmin";
        // Al usar redirect, se usa el link de los GetMapping, no la dirección del archivo
    //}

}

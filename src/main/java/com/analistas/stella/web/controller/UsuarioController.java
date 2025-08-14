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
                ? "Usuario " + usuario.getNombrecompleto() + " añadido"
                : "Usuario " + usuario.getNombrecompleto() + " modificado";

        flash.addFlashAttribute((usuario.getId() == null || usuario.getId() == 0) ? "success" : "warning", mensaje);


        usuarioService.guardar(usuario);
        status.setComplete(); // Lo que hace es limpiar la variable "usuario" definida en el SessionStatus de
                              // arriba

        return "redirect:/usuarios/listadoAdmin";
        // Al usar redirect, se usa el link de los GetMapping, no la dirección del archivo
    }

    @GetMapping("/listadoAdmin")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.buscarTodo());
        model.addAttribute("titulo", "Listado de Usuarios");
        return "usuarios/usuarios-list-admin";
}

@GetMapping("/editar/{id}")
    public String editarusuario(@PathVariable("id") Long id ,Model model, RedirectAttributes flash) {

        Usuario usuario = usuarioService.buscarPorId(id);

        model.addAttribute("titulo", "Editar usuario");
        model.addAttribute("listaUsuarios", usuarioService.buscarTodo());
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolService.buscarTodos());

        return "usuarios/usuarios-form";
    }

    @GetMapping("/borrar/{id}")
    public String borrarusuario(@PathVariable("id") Long id, RedirectAttributes flash) {

        Usuario usuario = usuarioService.buscarPorId(id);
        // Se obtiene el usuario antes de borrarlo para mostrar su descripción en el mensaje

        if (usuario != null) {
            usuarioService.borrarPorId(id);
            String mensaje = "Usuario " + usuario.getNombrecompleto() + " eliminado.";
            flash.addFlashAttribute("danger", mensaje);
        } else {
            String mensaje = "Usuario no encontrado";
            flash.addFlashAttribute("danger", mensaje);
        }

        return "redirect:/usuarios/listadoAdmin";
    }

}

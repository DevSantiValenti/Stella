package com.analistas.stella.web.controller;

import com.analistas.stella.model.domain.Proveedor;
import com.analistas.stella.model.service.IProveedorService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SessionAttributes({ "proveedor" }) // Esto hace que al editar un proveedor, no se cree otro, sino que se edite
@Controller
@RequestMapping("/proveedores")
public class ProveedorController {
    @Autowired
    IProveedorService proveedorService;

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("proveedores", proveedorService.listar());
        return "proveedores/proveedores-list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("titulo", "Nuevo Proveedor");
        model.addAttribute("proveedor", new Proveedor());
        return "proveedores/proveedores-form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Proveedor proveedor, RedirectAttributes flash, SessionStatus status) {

        String mensaje = (proveedor.getId() == null || proveedor.getId() == 0)
                ? "Proveedor " + proveedor.getNombre() + " creado."
                : "Proveedor " + proveedor.getNombre() + " modificado.";


        flash.addFlashAttribute((proveedor.getId() == null || proveedor.getId() == 0) ? "success" : "warning", mensaje);
        
        proveedorService.guardar(proveedor);
        status.setComplete();
        
        return "redirect:/proveedores/listar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {

        Proveedor proveedor = proveedorService.buscarPorId(id);

        String mensaje = "Proveedor " + proveedor.getNombre() + " eliminado.";
        flash.addFlashAttribute("danger", mensaje);

        proveedorService.eliminar(id);
        return "redirect:/proveedores/listar";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model) {
        Proveedor proveedor = proveedorService.buscarPorId(id);

        model.addAttribute("titulo", "Editar Proveedor");
        model.addAttribute("proveedor", proveedor);
        return "proveedores/proveedores-form";
    }
}

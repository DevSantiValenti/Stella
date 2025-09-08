package com.analistas.stella.web.controller;

import com.analistas.stella.model.domain.Proveedor;
import com.analistas.stella.model.service.IProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SessionAttributes({"proveedor"}) //Esto hace que al editar un proveedor, no se cree otro, sino que se edite 
@Controller
@RequestMapping("/proveedores")
public class ProveedorController {
    @Autowired
    private IProveedorService proveedorRepository;

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("proveedores", proveedorRepository.listar());
        return "proveedores/proveedores-list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        return "proveedores/proveedores-form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Proveedor proveedor) {
        proveedorRepository.guardar(proveedor);
        return "redirect:/proveedores/listar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        proveedorRepository.eliminar(id);
        return "redirect:/proveedores/listar";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor inválido: " + id));

        model.addAttribute("proveedor", proveedor);
        return "proveedores/proveedores-form";
    }
}

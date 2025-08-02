package com.analistas.stella.web.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analistas.stella.model.domain.Producto;
import com.analistas.stella.model.service.ICategoriaService;
import com.analistas.stella.model.service.IDepartamentoService;
import com.analistas.stella.model.service.IProductoService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;


@RequestMapping("/productos")
@Controller
public class ProductoControler {

    @Autowired
    IProductoService productoService;

    @Autowired
    ICategoriaService categoriaService;

    @Autowired
    IDepartamentoService departamentoService;

    @GetMapping("/listado")
    public String listado(Model model) {
        return "productos/productos-list";
    }

    @GetMapping("/listadoAdmin")
    public String listadoAdmin(Model model) {

        model.addAttribute("titulo", "Listado de Productos");

        return "/productos/productos-list-admin";
    }
    

    @GetMapping("/nuevo")
    public String nuevoProducto(Model model) {

        model.addAttribute("titulo", "Nuevo Producto");
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.buscarTodo());
        model.addAttribute("departamentos", departamentoService.buscarTodo());

        return "productos/productos-form";
    }

    @PostMapping("/guardar")
    public String guardarProducto(@Valid Producto producto, BindingResult result, RedirectAttributes flash,
            SessionStatus status, Model model) {

        // Verificar si hay errores de validación
        if (result.hasErrors()) {
            model.addAttribute("danger", "Corergir los errores del formulario");
        }

        String mensaje = (producto.getId() == null || producto.getId() == 0)
                ? "Producto " + producto.getNombre() + " añadido"
                : "Producto " + producto.getNombre() + " modificado";

        flash.addFlashAttribute((producto.getId() == null || producto.getId() == 0) ? "success" : "warning", mensaje);

        productoService.guardar(producto);
        status.setComplete(); // Lo que hace es limpiar la variable "producto" definida en el SessionStatus de
                              // arriba

        return "redirect:/productos/listado";
        // Al usar redirect, se usa el link de los GetMapping, no la dirección del archivo
    }

}

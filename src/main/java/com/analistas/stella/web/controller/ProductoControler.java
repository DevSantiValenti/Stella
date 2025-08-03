package com.analistas.stella.web.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analistas.stella.model.domain.Producto;
import com.analistas.stella.model.service.ICategoriaService;
import com.analistas.stella.model.service.IDepartamentoService;
import com.analistas.stella.model.service.IProductoService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RequestMapping("/productos")
@Controller
@SessionAttributes({"producto"}) //Esto hace que al editar un producto, no se cree otro, sino que se edite 
public class ProductoControler {

    @Autowired
    IProductoService productoService;

    @Autowired
    ICategoriaService categoriaService;

    @Autowired
    IDepartamentoService departamentoService;

    @GetMapping("/listado")
    public String listado(Model model) {

        model.addAttribute("productos", productoService.buscarTodo());

        return "productos/productos-list";
    }

    @GetMapping("/listadoAdmin")
    public String listadoAdmin(Model model) {

        model.addAttribute("titulo", "Listado de Productos");
        model.addAttribute("productos", productoService.buscarTodo());

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

        return "redirect:/productos/listadoAdmin";
        // Al usar redirect, se usa el link de los GetMapping, no la dirección del archivo
    }

    @GetMapping("/editar/{id}")
    public String editarProducto(@PathVariable("id") Long id ,Model model, RedirectAttributes flash) {

        Producto producto = productoService.buscarPorId(id);

        model.addAttribute("categorias", categoriaService.buscarTodo());
        model.addAttribute("titulo", "Editar Producto");
        model.addAttribute("producto", producto);

        return "productos/productos-form";
    }

    @GetMapping("/borrar/{id}")
    public String borrarProducto(@PathVariable("id") Long id, RedirectAttributes flash) {

        Producto producto = productoService.buscarPorId(id);
        // Se obtiene el producto antes de borrarlo para mostrar su descripción en el mensaje

        if (producto != null) {
            productoService.borrarPorId(id);
            String mensaje = "Producto " + producto.getNombre() + " eliminado.";
            flash.addFlashAttribute("danger", mensaje);
        } else {
            String mensaje = "Producto no encontrado";
            flash.addFlashAttribute("danger", mensaje);
        }

        return "redirect:/productos/listadoAdmin";
    }

    @GetMapping("/deshabilitar/{id}")
    public String getMethodName(@PathVariable("id") Long id, RedirectAttributes flash) {

        Producto producto = productoService.buscarPorId(id);
        producto.setActivo(!producto.isActivo());
        productoService.guardar(producto);

        String mensaje = producto.isActivo() ? "Producto " + producto.getNombre() + " deshabilitado."
                                            : "Producto " + producto.getNombre() + " habilitado.";
                                            
        flash.addFlashAttribute(producto.isActivo() ? "info" : "danger", mensaje);

        return "redirect:/productos/listadoAdmin";
    }
    
    

}

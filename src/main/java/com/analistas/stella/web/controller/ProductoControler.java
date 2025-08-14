package com.analistas.stella.web.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analistas.stella.model.domain.Categoria;
import com.analistas.stella.model.domain.Departamento;
import com.analistas.stella.model.domain.Oferta;
import com.analistas.stella.model.domain.Producto;
import com.analistas.stella.model.service.ICategoriaService;
import com.analistas.stella.model.service.IDepartamentoService;
import com.analistas.stella.model.service.IOfertaService;
import com.analistas.stella.model.service.IProductoService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;

@RequestMapping("/productos")
@Controller
@SessionAttributes({ "producto" }) // Esto hace que al editar un producto, no se cree otro, sino que se edite
public class ProductoControler {

    @Autowired
    IProductoService productoService;

    @Autowired
    ICategoriaService categoriaService;

    @Autowired
    IOfertaService ofertaService;

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
            SessionStatus status, Model model, @RequestParam("file") MultipartFile file) {

        // Verificar si hay errores de validación
        if (result.hasErrors()) {
            model.addAttribute("danger", "Corergir los errores del formulario");
        }

        String mensaje = (producto.getId() == null || producto.getId() == 0)
                ? "Producto " + producto.getNombre() + " añadido"
                : "Producto " + producto.getNombre() + " modificado";

        if (!file.isEmpty()) {
            String nombreUnico = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // Carpeta física fuera del JAR, en la raíz del proyecto
            Path directorioRecursos = Paths.get("uploads");
            Path rutaAbsoluta = directorioRecursos.toAbsolutePath();

            try {
                // Crea la carpeta si no existe
                Files.createDirectories(rutaAbsoluta);

                // Guarda el archivo
                Path rutaCompleta = rutaAbsoluta.resolve(nombreUnico);
                Files.write(rutaCompleta, file.getBytes());

                // Guarda solo la ruta pública
                producto.setLinkImagen("/uploads/" + nombreUnico);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        flash.addFlashAttribute((producto.getId() == null || producto.getId() == 0) ? "success" : "warning", mensaje);

        productoService.guardar(producto);
        status.setComplete(); // Lo que hace es limpiar la variable "producto" definida en el SessionStatus de
                              // arriba

        return "redirect:/productos/listadoAdmin";
        // Al usar redirect, se usa el link de los GetMapping, no la dirección del
        // archivo
    }

    @GetMapping("/editar/{id}")
    public String editarProducto(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {

        Producto producto = productoService.buscarPorId(id);


        // Obtenemos la categoría del producto
        Categoria categoriaDelProducto = producto.getCategoria();

        // Obtenemos el departamento de esa categoría
        Departamento departamentoDelProducto = categoriaDelProducto.getDepartamento();

        // Agregamos al modelo el producto y el departamento para preseleccionar en la vista
        model.addAttribute("producto", producto);
        model.addAttribute("departamento", departamentoDelProducto);

        // También necesitamos las listas completas para los desplegables
        List<Departamento> departamentos = departamentoService.buscarTodo();
        List<Categoria> categorias = categoriaService.buscarTodo();
        model.addAttribute("departamentos", departamentos);
        model.addAttribute("categorias", categorias);



        // model.addAttribute("categorias", categoriaService.buscarTodo());
        model.addAttribute("titulo", "Editar Producto");
        model.addAttribute("producto", producto);

        return "productos/productos-form";
    }

    @GetMapping("/borrar/{id}")
    public String borrarProducto(@PathVariable("id") Long id, RedirectAttributes flash) {

        Producto producto = productoService.buscarPorId(id);

        if (producto != null) {
            // 1. Buscar la oferta que contiene el producto
            List<Oferta> todasLasOfertas = ofertaService.buscarTodos(); // Asumiendo este método existe
            for (Oferta oferta : todasLasOfertas) {
                // Verificar si la oferta contiene el producto en su lista
                if (oferta.getProductos().contains(producto)) {
                    // 2. Si lo encuentra, remover el producto de la oferta y guardar la oferta
                    ofertaService.borrarPorId(oferta.getId());
                    // oferta.getProductos().remove(producto); esto es solo para que elimine el producto y no la oferta entera
                    // ofertaService.guardar(oferta); // Asumiendo un método para guardar la oferta
                    // break; // Romper el ciclo una vez que se encuentra y actualiza la oferta
                }
            }

            // 3. Eliminar el producto
            productoService.borrarPorId(id);
            String mensaje = "Producto '" + producto.getNombre()
                    + "' eliminado. Su oferta relacionada (si existía) también fue eliminada.";
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

        String mensaje = producto.isActivo() ? "Producto " + producto.getNombre() + " habilitado."
                : "Producto " + producto.getNombre() + " deshabilitado.";

        flash.addFlashAttribute(producto.isActivo() ? "info" : "danger", mensaje);

        return "redirect:/productos/listadoAdmin";
    }

}

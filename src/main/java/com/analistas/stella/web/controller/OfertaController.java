package com.analistas.stella.web.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analistas.stella.model.domain.Oferta;
import com.analistas.stella.model.domain.Producto;
import com.analistas.stella.model.service.IOfertaService;
import com.analistas.stella.model.service.IProductoService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/ofertas")
@SessionAttributes("oferta")
public class OfertaController {

    @Autowired
    IProductoService productoService;

    @Autowired
    IOfertaService ofertaService;

    @GetMapping("/nuevo")
    public String nuevaOferta(Model model) {

        model.addAttribute("oferta", new Oferta());
        model.addAttribute("listaProductos", productoService.listaProductosActivos());
        model.addAttribute("titulo", "Nueva Oferta");

        return "ofertas/ofertas-form";
    }

    @GetMapping("/listadoAdmin")
    public String listadoOfertasAdm(Model model) {

        model.addAttribute("tipo", "¿Estás seguro que quieres borrar esta oferta? Esta acción no se puede deshacer.");
        model.addAttribute("titulo", "Listado de Ofertas");
        model.addAttribute("ofertas", ofertaService.buscarTodos());

        return "ofertas/ofertas-list-admin";
    }

    @PostMapping("/guardar")
    public String guardarCategoria(@Valid Oferta oferta,
            @RequestParam(value = "productos", required = false) List<Long> productosIds, BindingResult result,
            Model model, RedirectAttributes flash, SessionStatus status, @RequestParam("file") MultipartFile file) {

        // Verificar si hay errores
        if (result.hasErrors()) {
            model.addAttribute("danger", "Corregir errores");
        }

        // Cargar los productos desde la base de datos usando los IDs recibidos
        List<Producto> productosSelec = new ArrayList<>();
        if (productosIds != null && !productosIds.isEmpty()) {
            productosSelec = productoService.buscarPorIds(productosIds);
        }

        // Setear la lista de productos en la oferta
        oferta.setProductos(productosSelec);

        String mensaje = (oferta.getId() == null || oferta.getId() == 0)
                ? "Oferta " + oferta.getDescripcion() + " añadida"
                : "Oferta " + oferta.getDescripcion() + " modificada";

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
                oferta.setImagenBanner("/uploads/" + nombreUnico);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        flash.addFlashAttribute((oferta.getId() == null || oferta.getId() == 0) ? "success" : "warning", mensaje);

        ofertaService.guardar(oferta);
        status.setComplete();

        return "redirect:/ofertas/listadoAdmin";
    }

    @GetMapping("/editar/{id}")
    public String editarProducto(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {

        Oferta oferta = ofertaService.buscarPorId(id);

        model.addAttribute("titulo", "Editar Oferta");
        model.addAttribute("listaProductos", productoService.listaProductosActivos());
        model.addAttribute("oferta", oferta);

        return "ofertas/ofertas-form";
    }

    @GetMapping("/borrar/{id}")
    public String borrarOferta(@PathVariable("id") Long id, RedirectAttributes flash) {

        Oferta oferta = ofertaService.buscarPorId(id);
        // Se obtiene el producto antes de borrarlo para mostrar su descripción en el
        // mensaje

        if (oferta != null) {
            ofertaService.borrarPorId(id);
            String mensaje = "Oferta " + oferta.getDescripcion() + " eliminada.";
            flash.addFlashAttribute("danger", mensaje);
        } else {
            String mensaje = "Oferta no encontrada";
            flash.addFlashAttribute("danger", mensaje);
        }

        return "redirect:/ofertas/listadoAdmin";
    }

    @GetMapping("/deshabilitar/{id}")
    public String deshabilitar(@PathVariable("id") Long id, RedirectAttributes flash) {

        Oferta oferta = ofertaService.buscarPorId(id);
        oferta.setActivo(!oferta.isActivo());
        ofertaService.guardar(oferta);

        String mensaje = oferta.isActivo() ? "Oferta " + oferta.getDescripcion() + " habilitada."
                : "Oferta " + oferta.getDescripcion() + " deshabilitada.";

        flash.addFlashAttribute(oferta.isActivo() ? "info" : "danger", mensaje);

        return "redirect:/ofertas/listadoAdmin";
    }

}

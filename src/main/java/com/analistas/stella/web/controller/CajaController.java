package com.analistas.stella.web.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.analistas.stella.model.domain.Caja;
import com.analistas.stella.model.service.ICajaFisicaService;
import com.analistas.stella.model.service.ICajaService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cajas")
public class CajaController {

    @Autowired
    private ICajaService cajaService;

    @Autowired
    ICajaFisicaService cajaFisicaService;

    @GetMapping("/listadoAdmin")
    public String listar(Model model) {
        model.addAttribute("titulo", "Listado de Cajas");
        model.addAttribute("cajas", cajaService.listarCajas());
        model.addAttribute("cajasFisicas", cajaFisicaService.listarCajasFisicas());

        return "cajas/cajas-list-admin";
    }

    @PostMapping("/abrir")
    public String abrirCaja(Model model, @RequestParam BigDecimal montoInicial, HttpSession session,
            @RequestParam Long cajaFisicaId) {

        Caja caja = cajaService.abrirCaja(montoInicial, cajaFisicaId);
        session.setAttribute("cajaId", caja.getId());
        session.setAttribute("cajaNombre", caja.getCajaFisica().getNombre());

        return "redirect:/ventas/nuevo";
    }

    @PostMapping("/cerrar")
    public String cerrarCaja(@RequestParam Long cajaId, HttpSession session) {
        // Cerrar caja por ID
        cajaService.cerrarCaja(cajaId);

        session.removeAttribute("cajaId");
        session.removeAttribute("cajaNombre");
        return "redirect:/cajas/listadoAdmin";
    }


    // CajaFisica
    @PostMapping("/crear")
    public String crearCaja(@RequestParam String nombre) {
        cajaFisicaService.crearCaja(nombre);
        return "redirect:/cajas/listadoAdmin";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarCaja(@PathVariable Long id) {
        cajaFisicaService.eliminarCaja(id);
        return "redirect:/cajas/listadoAdmin";
    }
}

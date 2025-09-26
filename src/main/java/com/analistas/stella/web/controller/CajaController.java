package com.analistas.stella.web.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.analistas.stella.model.domain.Caja;
import com.analistas.stella.model.service.ICajaFisicaService;
import com.analistas.stella.model.service.ICajaService;
import com.analistas.stella.model.service.IVentaService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cajas")
public class CajaController {

    @Autowired
    ICajaService cajaService;

    @Autowired
    ICajaFisicaService cajaFisicaService;

    @Autowired
    IVentaService ventaService;

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

    @GetMapping("/cerrarCaja/{id}")
    public String getMethodName(Model model, @PathVariable Long id) {
        Caja caja = cajaService.buscarPorId(id);

        BigDecimal totalEfectivo = cajaService.calcularTotalPorMetodo(caja, "EFECTIVO");
        BigDecimal totalTransferencia = cajaService.calcularTotalPorMetodo(caja, "TRANSFERENCIA");
        BigDecimal totalCredito1 = cajaService.calcularTotalPorMetodo(caja, "CREDITO1C");
        BigDecimal totalCredito3 = cajaService.calcularTotalPorMetodo(caja, "CREDITO3C");
        BigDecimal totalCredito6 = cajaService.calcularTotalPorMetodo(caja, "CREDITO6C");
        BigDecimal totalDebito = cajaService.calcularTotalPorMetodo(caja, "DEBITO");
        BigDecimal totalCuentaCorriente = cajaService.calcularTotalPorMetodo(caja, "CTA_CORRIENTE");

        // Calculo del total de ventas de todos los medios de pago:
        BigDecimal totalVentas = ventaService.calcularTotalVentasPorCaja(id);
        model.addAttribute("total", totalVentas);



        // Cantidades por metodo de pago
        model.addAttribute("totalEfectivo", totalEfectivo);
        model.addAttribute("totalTransferencia", totalTransferencia);
        model.addAttribute("totalCredito", totalCredito1.add(totalCredito3).add(totalCredito6));
        model.addAttribute("totalDebito", totalDebito);
        model.addAttribute("totalCuentaCorriente", totalCuentaCorriente);

        model.addAttribute("montoInicial", caja.getMontoInicial());

        model.addAttribute("caja", caja);
        model.addAttribute("titulo", "Cerrar Venta");
        return "cajas/cerrar-caja";
    }
    

    @PostMapping("/cerrar")
    public String cerrarCaja(@RequestParam Long cajaId, @RequestParam BigDecimal montoDeclarado,
            @RequestParam(required = false) String comentarioCierre, HttpSession session) {
        // Cerrar caja por ID
        cajaService.cerrarCaja(cajaId, montoDeclarado, comentarioCierre);

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

    // Probando lo de cerrar caja con cambio

}

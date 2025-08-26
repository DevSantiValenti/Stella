package com.analistas.stella.web.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.analistas.stella.model.domain.Venta;
import com.analistas.stella.model.service.IVentaService;

@Controller
public class DashboardController {

    @Autowired
    IVentaService ventaService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("titulo", "Dashboard");
        model.addAttribute("totalVentas", ventaService.listar().size());

        // Trae todas las ventas, suma los totales y resta el vuelto para obtener la
        // ganancia neta
        // List<Venta> ventas = ventaService.listar();
        // Double ganNeta = ventas.stream().mapToDouble(v -> v.getTotal()).sum();
        model.addAttribute("ganNeta", ventaService.gananciaNetaTotal());

        // Calculo para ticket Promedio:
        List<Venta> ventas = ventaService.listar();
        Double totalTickets = ventas.stream().mapToDouble(v -> v.getTotal()).sum();
        Double averTicket = totalTickets / ventas.size();
        model.addAttribute("averTicket", averTicket);

        // Calculo para la categoria mas vendida:
        // Nueva lógica: categoría más comprada
        String categoriaTop = ventaService.getCategoriaTop();
        model.addAttribute("categoriaTop", categoriaTop);

        // NUEVO: Ventas agrupadas por mes
        Map<Integer, Double> ventasPorMes = ventaService.obtenerVentasPorMes(LocalDate.now().getYear());
        // Armo un array de 12 posiciones para pasarlo al Chart.js
        Double[] datosMeses = new Double[12];
        for (int i = 1; i <= 12; i++) {
            datosMeses[i - 1] = ventasPorMes.getOrDefault(i, 0.0);
        }
        model.addAttribute("ventasMeses", Arrays.toString(datosMeses));

         // Obtener numero de ventas por mes:
        Map<Integer, Long> nVentasMes = ventaService.obtenerNumVentasPorMes(LocalDate.now().getYear());
        Double[] ventasXMeses = new Double[12];
        for (int i = 1; i <= 12; i++) {
            ventasXMeses[i - 1] = nVentasMes.getOrDefault(i, 0L).doubleValue();
        }
        model.addAttribute("nVentasMes", Arrays.toString(ventasXMeses));

        // Traer las ventas de hoy
        model.addAttribute("ventasHoy", ventaService.getCantidadVentasHoy());

        // Traer el total vendido hoy
        model.addAttribute("totalHoy", ventaService.getTotalVendidoHoy());

        // Traer el ticket promedio hoy
        model.addAttribute("ticketPromedioHoy", ventaService.getTicketPromedioHoy());

        return "dashboard";
    }

}

package com.analistas.stella.web.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.analistas.stella.model.domain.Venta;
import com.analistas.stella.model.repository.IVentaRepository;
import com.analistas.stella.model.service.IVentaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Controller
public class ReporteController {

    @Autowired
    IVentaService ventaService;

    @Autowired
    IVentaRepository ventaRepository;

    @GetMapping("/reportes")
    public String reportes(Model model) throws JsonProcessingException {

        // Ganancia Total:
        model.addAttribute("ganNeta", ventaService.gananciaNetaTotal());

        // Calculo para ticket Promedio:
        List<Venta> ventas = ventaService.listar();
        Double totalTickets = ventas.stream().mapToDouble(v -> v.getTotal()).sum();
        Double averTicket = totalTickets / ventas.size();
        model.addAttribute("averTicket", averTicket);

        // Total de ventas de todos los tiempos:
        model.addAttribute("totalVentas", ventaService.listar().size());

        model.addAttribute("titulo", "Reportes");

        // Ventas por dia en un rango de 10 dias:
        LocalDate hoy = LocalDate.now();
        LocalDate inicio = hoy.minusDays(4); // 4 dias antes
        LocalDate fin = hoy.plusDays(5);

        Map<LocalDate, Double> ventasPorDia = ventaService.obtenerTotalVentasPorDia(hoy.getYear());

        // Rellenar rango aunque no haya ventas en algún dia:
        List<String> labels = new ArrayList<>();
        List<String> data = new ArrayList<>();
        for (LocalDate fecha = inicio; !fecha.isAfter(fin); fecha = fecha.plusDays(1)) {
            labels.add(fecha.toString() + "T00:00:00");
            data.add(String.valueOf(ventasPorDia.getOrDefault(fecha, 0.0)));
        }
        // model.addAttribute("labels", labels);
        // model.addAttribute("data", data);
        model.addAttribute("labels", new ObjectMapper().writeValueAsString(labels));
        model.addAttribute("data", new ObjectMapper().writeValueAsString(data));

        return "reportes/reportes";
    }

}

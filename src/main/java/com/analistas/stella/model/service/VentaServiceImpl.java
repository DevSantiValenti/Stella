package com.analistas.stella.model.service;

import com.analistas.stella.model.domain.MetodoPagoDTO;
import com.analistas.stella.model.domain.Venta;
import com.analistas.stella.model.repository.IVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VentaServiceImpl implements IVentaService {

    @Autowired
    IVentaRepository ventaRepo;

    @Override
    public void guardar(Venta venta) {
        ventaRepo.save(venta);
    }

    @Override
    public List<Venta> listar() {
        return (List<Venta>) ventaRepo.findAll();
    }

    @Override
    public Venta buscarPorId(Long id) {
        return ventaRepo.findById(id).orElse(null);
    }

    @Override
    public String getCategoriaTop() {
        List<Object[]> result = ventaRepo.findTopCategoria();
        if (!result.isEmpty()) {
            return (String) result.get(0)[0]; // nombre de la categoría top
        }
        return "N/A";
    }

    // Para obtener el total en $ de todas las ventas por cada mes
    @Override
    public Map<Integer, Double> obtenerVentasPorMes(int anio) {
        List<Venta> ventas = (List<Venta>) ventaRepo.findAll();

        return ventas.stream()
                .filter(v -> v.getFechaVenta().getYear() == anio) // solo el año actual
                .collect(Collectors.groupingBy(
                        v -> v.getFechaVenta().getMonthValue(), // mes (1-12)
                        Collectors.summingDouble(Venta::getTotal) // suma total por mes
                ));
    }

    // Para obtener el total en numero de todas las ventas por cada mes
    @Override
    public Map<Integer, Long> obtenerNumVentasPorMes(int anio) {
        List<Venta> ventas = (List<Venta>) ventaRepo.findAll();

        return ventas.stream()
        .filter(v -> v.getFechaVenta().getYear() == anio)
        .collect(Collectors.groupingBy(
                v -> v.getFechaVenta().getMonthValue(),
                Collectors.counting()
        ));
    }

    // Para obtener las el total de $ por dia en un rango de 10 dias:
    @Override
    public Map<LocalDate, Double> obtenerTotalVentasPorDia(int anio) {

        List<Venta> ventas = (List<Venta>) ventaRepo.findAll();

        return ventas.stream()
                .filter(v -> v.getFechaVenta().getYear() == anio)
                .collect(Collectors.groupingBy(
                    v -> v.getFechaVenta().toLocalDate(),
                    Collectors.summingDouble(Venta::getTotal)
                ));
    }

    @Override
    public Long getCantidadVentasHoy() {
        return ventaRepo.countVentasHoy();
    }

    @Override
    public Double getTotalVendidoHoy() {
        return Optional.ofNullable(ventaRepo.totalVendidoHoy()).orElse(0.0);
    }

    @Override
    public Double getTicketPromedioHoy() {
        return Optional.ofNullable(ventaRepo.ticketPromedioHoy()).orElse(0.0);
    }

    @Override
    public Double gananciaNetaTotal() {
        List<Venta> ventas = (List<Venta>) ventaRepo.findAll();
        Double ganNeta = ventas.stream().mapToDouble(v -> v.getTotal()).sum();
        // model.addAttribute("ganNeta", ganNeta);
        return ganNeta;
    }

    // Importe por metodo de pago
    @Override
    public List<MetodoPagoDTO> obtenerTotalesPorMetodoPago() {
        return ventaRepo.obtenerTotalesPorMetodoPago();
    }

    // Calculos de la caja:
    @Override
    public BigDecimal calcularTotalVentasPorCaja(Long cajaId) {
        List<Venta> ventas = ventaRepo.findByCajaIdAndFechaVentaBetween(cajaId);
        return ventas.stream()
            .map(v -> BigDecimal.valueOf(v.getTotal()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calcularTotalVueltosPorCaja(Long cajaId) {
        List<Venta> ventas = ventaRepo.findByCajaIdAndFechaVentaBetween(cajaId);
        return ventas.stream()
            .map(v -> BigDecimal.valueOf(v.getVuelto()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}

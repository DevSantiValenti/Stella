package com.analistas.stella.model.service;

import com.analistas.stella.model.domain.MetodoPagoDTO;
import com.analistas.stella.model.domain.Venta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IVentaService {
    
    public void guardar(Venta venta);

    public List<Venta> listar();

    public Venta buscarPorId(Long id);

    public String getCategoriaTop();

    public Map<Integer, Double> obtenerVentasPorMes(int anio);

    // Intento de metodo para traer todas las ventas de cada mes(el total en numero)
    public Map<Integer, Long> obtenerNumVentasPorMes(int anio);

    public Map<LocalDate, Double> obtenerTotalVentasPorDia( int anio);

    public Long getCantidadVentasHoy();

    public Double getTotalVendidoHoy();

    public Double getTicketPromedioHoy();

    public Double gananciaNetaTotal();

    // Importe por metodos de pago
    public List<MetodoPagoDTO> obtenerTotalesPorMetodoPago();

    // Calculos de la caja
    public BigDecimal calcularTotalVentasPorCaja(Long cajaId);
    public BigDecimal calcularTotalVueltosPorCaja(Long cajaId);
}

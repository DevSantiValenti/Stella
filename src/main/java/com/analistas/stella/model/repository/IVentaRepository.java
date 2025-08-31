package com.analistas.stella.model.repository;

import com.analistas.stella.model.domain.MetodoPagoDTO;
import com.analistas.stella.model.domain.Venta;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface IVentaRepository extends CrudRepository<Venta, Long> {

    // Para encontrar la categoria mas vendida
    @Query("SELECT d.producto.categoria.nombre, SUM(d.cantidad) as total " +
            "FROM DetalleVenta d " +
            "GROUP BY d.producto.categoria.nombre " +
            "ORDER BY total DESC")
    List<Object[]> findTopCategoria();

    // Cantidad de ventas del día
    @Query("SELECT COUNT(v) FROM Venta v WHERE DATE(v.fechaVenta) = CURRENT_DATE")
    Long countVentasHoy();

    // Total vendido del día
    @Query("SELECT SUM(v.total) FROM Venta v WHERE DATE(v.fechaVenta) = CURRENT_DATE")
    Double totalVendidoHoy();

    // Ticket promedio (total / cantidad)
    @Query("SELECT AVG(v.total) FROM Venta v WHERE DATE(v.fechaVenta) = CURRENT_DATE")
    Double ticketPromedioHoy();

    // Importe total por categoria:
    @Query("SELECT v.metodoPago AS metodo, SUM(v.total) AS total " +
           "FROM Venta v " +
           "GROUP BY v.metodoPago")
    List<MetodoPagoDTO> obtenerTotalesPorMetodoPago();

}

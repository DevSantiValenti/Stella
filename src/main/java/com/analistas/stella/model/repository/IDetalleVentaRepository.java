package com.analistas.stella.model.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.analistas.stella.model.domain.CategoriaVentaDTO;
import com.analistas.stella.model.domain.DetalleVenta;
import com.analistas.stella.model.domain.TopProductoDTO;

public interface IDetalleVentaRepository extends CrudRepository<DetalleVenta, Long> {

    @Query(
    "SELECT p.nombre AS nombre, " +
    "       SUM(d.cantidad) AS totalUnidades, " +
    "       SUM(d.cantidad * d.precioUnitario) AS totalImporte " +
    "FROM DetalleVenta d " +
    "JOIN d.producto p " +
    "GROUP BY p.nombre " +
    "ORDER BY totalUnidades DESC")
    List<TopProductoDTO> findTopProductos(PageRequest pageable);

    @Query("SELECT c.nombre AS nombre, SUM(d.subtotal) AS total " +
           "FROM DetalleVenta d " +
           "JOIN d.producto p " +
           "JOIN p.categoria c " +
           "GROUP BY c.nombre " +
           "ORDER BY SUM(d.subtotal) DESC")
    List<CategoriaVentaDTO> findTopCategorias(PageRequest pageable);

}

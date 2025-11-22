package com.analistas.stella.model.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.analistas.stella.model.domain.Caja;
import com.analistas.stella.model.domain.CajaFisica;
import com.analistas.stella.model.domain.Usuario;

public interface ICajaRepository extends CrudRepository<Caja, Long> {
       // Con esto puedo saber si hay una caja abierta
       // Optional<Caja> findByAbiertaTrue();

       // Con esto puedo saber si hay tal caja fisica abierta
       Optional<Caja> findByCajaFisicaAndAbiertaTrue(CajaFisica cajaFisica);

       // Encontrar caja por id para cerrar
       Optional<Caja> findByIdAndAbiertaTrue(Long id);

       // @Query("SELECT COALESCE(SUM(v.total), 0) " +
       // "FROM Venta v " +
       // "WHERE v.caja = :caja AND v.metodoPago = :metodo")
       // BigDecimal calcularTotalPorMetodo(@Param("caja") Caja caja,
       // @Param("metodo") String metodo);
       @Query("SELECT COALESCE(SUM(p.monto), 0) " +
                     "FROM PagoVenta p " +
                     "WHERE p.venta.caja = :caja AND p.metodoPago = :metodo")
       BigDecimal calcularTotalPorMetodo(@Param("caja") Caja caja,
                     @Param("metodo") String metodo);

       Caja findByUsuarioAndAbiertaTrue(Usuario usuario);

       @Query("SELECT c " +
                     "FROM Caja c " +
                     "JOIN FETCH c.cajaFisica " +
                     "JOIN FETCH c.usuario " +
                     "WHERE c.usuario.nombrecompleto = :nombreCompleto " +
                     "AND c.abierta = true")
       Optional<Caja> findCajaAbiertaConFetch(@Param("nombreCompleto") String nombreCompleto);

}

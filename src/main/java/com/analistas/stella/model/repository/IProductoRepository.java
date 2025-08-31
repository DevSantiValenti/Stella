package com.analistas.stella.model.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.analistas.stella.model.domain.Producto;
import java.util.List;
import java.util.Optional;

public interface IProductoRepository extends CrudRepository<Producto, Long> {

    List<Producto> findByActivo(boolean activo);

    // Buscar por coincidencia en código o descripción (ignora
    // mayúsculas/minúsculas)
    @Query("SELECT p FROM Producto p " +
            "WHERE LOWER(p.codigoBarras) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "   OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Producto> buscarPorCodigoODescripcion(@Param("q") String q);

    // Sugerencias por código de barras o nombre (hasta 10)
    List<Producto> findTop10ByCodigoBarrasContainingIgnoreCaseOrNombreContainingIgnoreCase(
        String cod, String nom
    );

    // Búsqueda exacta por código de barras (para el escáner)
    Optional<Producto> findByCodigoBarras(String codigoBarras);

    // Encontrar productos con bajo stock
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMin * 1.2") 
    List<Producto> findProductosCercanosAlMinimo();
}

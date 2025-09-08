package com.analistas.stella.model.service;
import com.analistas.stella.model.domain.Proveedor;
import java.util.List;
import java.util.Optional;

public interface IProveedorService {
    List<Proveedor> listar();
    void guardar(Proveedor proveedor);
    Proveedor buscarPorId(Long id);
    void eliminar(Long id);

    Optional<Proveedor> findById(Long id);
}

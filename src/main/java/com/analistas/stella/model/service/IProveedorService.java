package com.analistas.stella.model.service;
import com.analistas.stella.model.domain.Proveedor;
import java.util.List;

public interface IProveedorService {
    public List<Proveedor> listar();
    public void guardar(Proveedor proveedor);
    public Proveedor buscarPorId(Long id);
    public void eliminar(Long id);

}

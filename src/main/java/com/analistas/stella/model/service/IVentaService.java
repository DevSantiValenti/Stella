package com.analistas.stella.model.service;

import com.analistas.stella.model.domain.Venta;
import java.util.List;

public interface IVentaService {
    
    public void guardar(Venta venta);

    public List<Venta> listar();

    public Venta buscarPorId(Long id);
}

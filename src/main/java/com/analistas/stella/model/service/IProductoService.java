package com.analistas.stella.model.service;

import java.util.List;

import com.analistas.stella.model.domain.Producto;
import com.analistas.stella.model.domain.TopProductoDTO;

public interface IProductoService {

    public void guardar(Producto producto);

    public List<Producto> buscarTodo();

    public Producto buscarPorId(Long id);

    public void borrarPorId(Long id);

    public List<Producto> buscarPorIds(List<Long> id);

    public List<Producto> listaProductosActivos();

    public List<Producto> buscarPorCodigoODescripcion(String q);

    public List<TopProductoDTO> obtenerTopProductos( int limite);

    // Encontrar productos con bajo stock
    public List<Producto> obtenerProductosCercanosAlMinimo();

}

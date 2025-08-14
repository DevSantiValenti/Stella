package com.analistas.stella.model.service;

import java.util.List;

import com.analistas.stella.model.domain.Producto;

public interface IProductoService {

    public void guardar(Producto producto);

    public List<Producto> buscarTodo();

    public Producto buscarPorId(Long id);

    public void borrarPorId(Long id);

    public List<Producto> buscarPorIds(List<Long> id);

    public List<Producto> listaProductosActivos();

}

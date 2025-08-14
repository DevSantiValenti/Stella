package com.analistas.stella.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analistas.stella.model.domain.Producto;
import com.analistas.stella.model.repository.IProductoRepository;

@Service
public class ProductoServiceImpl implements IProductoService {

    @Autowired
    IProductoRepository productoRepository;

    @Override
    public void guardar(Producto producto) {
        productoRepository.save(producto);
    }

    @Override
    public List<Producto> buscarTodo() {
        return (List<Producto>) productoRepository.findAll();
    }

    @Override
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Override
    public void borrarPorId(Long id) {
        productoRepository.deleteById(id);
    }

    @Override
    public List<Producto> buscarPorIds(List<Long> ids) {
        return (List<Producto>) productoRepository.findAllById(ids);
    }

    @Override
    public List<Producto> listaProductosActivos() {
        return productoRepository.findByActivo(true);
    }

}

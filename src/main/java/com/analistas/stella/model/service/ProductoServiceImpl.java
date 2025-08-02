package com.analistas.stella.model.service;

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

}

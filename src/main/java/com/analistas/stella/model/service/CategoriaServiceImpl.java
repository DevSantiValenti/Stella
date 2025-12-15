package com.analistas.stella.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.analistas.stella.model.domain.Categoria;
import com.analistas.stella.model.domain.CategoriaVentaDTO;
import com.analistas.stella.model.repository.ICategoriaRepository;
import com.analistas.stella.model.repository.IDetalleVentaRepository;

@Service
public class CategoriaServiceImpl implements ICategoriaService {

    @Autowired
    ICategoriaRepository categoriaRepository;

    @Autowired
    IDetalleVentaRepository detalleVentaRepository;

    @Override
    public List<Categoria> buscarTodo() {
        return (List<Categoria>) categoriaRepository.findAll();
    }

    @Override
    public List<CategoriaVentaDTO> obtenerTop10Categorias() {
        return detalleVentaRepository.findTopCategorias(PageRequest.of(0, 10));
    }

    @Override
    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id).orElse(null);
    }

}

package com.analistas.stella.model.service;

import java.util.List;


import com.analistas.stella.model.domain.Categoria;
import com.analistas.stella.model.domain.CategoriaVentaDTO;

public interface ICategoriaService {

    public List<Categoria> buscarTodo();

    // Obtener las mejores 10 categorias
    public List<CategoriaVentaDTO> obtenerTop10Categorias();

    public Categoria buscarPorId(Long id);
}

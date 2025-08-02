package com.analistas.stella.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analistas.stella.model.domain.Categoria;
import com.analistas.stella.model.repository.ICategoriaRepository;

@Service
public class CategoriaServiceImpl implements ICategoriaService {

    @Autowired
    ICategoriaRepository categoriaRepository;

    @Override
    public List<Categoria> buscarTodo() {
        return (List<Categoria>) categoriaRepository.findAll();
    }

}

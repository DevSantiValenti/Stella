package com.analistas.stella.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analistas.stella.model.domain.Departamento;
import com.analistas.stella.model.repository.IDepartamentoRepository;

@Service
public class DepartamentoServiceImpl implements IDepartamentoService{

    @Autowired
    IDepartamentoRepository departamentoRepository;

    @Override
    public List<Departamento> buscarTodo() {
        return (List<Departamento>) departamentoRepository.findAll();
    }

}

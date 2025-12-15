package com.analistas.stella.model.service;

import com.analistas.stella.model.domain.Compra;
import com.analistas.stella.model.domain.CompraDTO;

import java.util.List;

public interface ICompraService {
    public List<Compra> listar();

    public Compra guardar(Compra compra);

    public Compra guardarDesdeDTO(CompraDTO dto);
}
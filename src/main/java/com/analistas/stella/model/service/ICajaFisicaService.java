package com.analistas.stella.model.service;

import java.util.List;

import com.analistas.stella.model.domain.CajaFisica;

public interface ICajaFisicaService {
    
    public CajaFisica crearCaja(String nombre);

    public void eliminarCaja(Long id);

    public List<CajaFisica> listarCajasFisicas();

    public CajaFisica buscarPorId(Long id);
}

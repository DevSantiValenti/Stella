package com.analistas.stella.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analistas.stella.model.domain.CajaFisica;
import com.analistas.stella.model.repository.ICajaFisicaRepository;

@Service
public class CajaFisicaServiceImpl implements ICajaFisicaService {

    @Autowired
    ICajaFisicaRepository cajaFisicaRepository;

    @Override
    public CajaFisica crearCaja(String nombre) {
        CajaFisica caja = new CajaFisica(nombre);
        return cajaFisicaRepository.save(caja);
    }

    @Override
    public void eliminarCaja(Long id) {
        cajaFisicaRepository.deleteById(id);
    }

    @Override
    public List<CajaFisica> listarCajasFisicas() {
        return (List<CajaFisica>) cajaFisicaRepository.findAll();
    }

    @Override
    public CajaFisica buscarPorId(Long id) {
        return cajaFisicaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Caja física no encontrada con ID: " + id));
    }

}

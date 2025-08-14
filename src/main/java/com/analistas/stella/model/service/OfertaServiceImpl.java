package com.analistas.stella.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analistas.stella.model.domain.Oferta;
import com.analistas.stella.model.repository.IOfertaRepository;

@Service
public class OfertaServiceImpl implements IOfertaService{

    @Autowired
    IOfertaRepository ofertaRepository;

    @Override
    public void guardar(Oferta oferta) {
        ofertaRepository.save(oferta);
    }

    @Override
    public List<Oferta> buscarTodos() {
        return (List<Oferta>) ofertaRepository.findAll();
    }

    @Override
    public Oferta buscarPorId(Long id){
        return ofertaRepository.findById(id).orElse(null);
    }

    @Override
    public void borrarPorId(Long id) {
        ofertaRepository.deleteById(id);
    }

}

package com.analistas.stella.model.service;

import java.math.BigDecimal;
import java.util.List;

import com.analistas.stella.model.domain.Caja;

public interface ICajaService {

    public Caja abrirCaja(BigDecimal montoInicial,Long cajaFisicaId);

    public Caja cerrarCaja(Long cajaId, BigDecimal montoDeclarado, String comentarioCierre);

    public List<Caja> listarCajas();

    public void guardar(Caja caja);

    public Caja buscarPorId(Long id);

    
}

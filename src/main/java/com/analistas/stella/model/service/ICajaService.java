package com.analistas.stella.model.service;

import java.math.BigDecimal;
import java.util.List;

import com.analistas.stella.model.domain.Caja;

public interface ICajaService {

    public Caja abrirCaja(BigDecimal montoInicial,Long cajaFisicaId);

    public Caja cerrarCaja(Long cajaId);

    public List<Caja> listarCajas();
}

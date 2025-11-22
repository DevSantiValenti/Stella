package com.analistas.stella.model.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.analistas.stella.model.domain.Caja;
import com.analistas.stella.model.domain.Usuario;

public interface ICajaService {

    public Caja abrirCaja(BigDecimal montoInicial,Long cajaFisicaId);

    public Caja cerrarCaja(Long cajaId, BigDecimal montoDeclarado, String comentarioCierre);

    public List<Caja> listarCajas();

    public void guardar(Caja caja);

    public Caja buscarPorId(Long id);

    public BigDecimal calcularTotalPorMetodo(Caja caja, String metodoPago);

    Caja buscarCajaAbiertaPorUsuario(Usuario usuario);

    Optional<Caja> buscaarCajaAbiertaConFetch(String username);
}
